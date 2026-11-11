package com.erenmaral.dokun;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;

import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Yerel ag (Wi-Fi / hotspot) uzerinden "oda kur" icin telefonda kucuk bir WebSocket sunucusu acar.
 * Misafirler WebView icindeki normal WebSocket ile baglanir; bu sinif yalnizca sunucu tarafini ve yerel IP'yi saglar.
 *
 * JS tarafi: getLocalIp(), start({port}), stop(), send({id,data}), broadcast({data}), close({id})
 * Olaylar: clientOpen {id}, clientMessage {id,data}, clientClose {id}, serverError {}
 *
 * Capacitor TUM eklenti cagrilarini tek bir arka plan is parcaciginda ("CapacitorPlugins") sirayla calistirir ve bir yontemden
 * kacan her istisna uygulamayi cokertir. Bu yuzden: (1) hicbir @PluginMethod istisna firlatmaz, (2) yavas isler (sunucuyu kapatma /
 * acma) ayri bir yasam dongusu is parcaciginda sirayla yapilir, boylece "gonder" cagrilari bekletilmez.
 */
@CapacitorPlugin(name = "LanServer")
public class LanServerPlugin extends Plugin {
    static final int DEFAULT_PORT = 47813;
    /** Protokol mesajlari kucuktur (en buyugu birkac yuz bayt); bunun uzeri kotu niyetli ya da bozuk istemcidir ve baglantisi kapatilir. */
    static final int MAX_MESSAGE_BYTES = 16 * 1024;
    /** Oda en fazla 4 kisiliktir; yari acik / sahte baglantilar yuzunden gercek oyuncular disarida kalmasin diye makul bir ust sinir. */
    static final int MAX_CLIENTS = 12;
    private static final int BIND_ATTEMPTS = 4;
    private static final long BIND_RETRY_MS = 300;
    private static final long START_WAIT_MS = 4000;
    private static final int STOP_WAIT_MS = 1000;

    private final Object lock = new Object();
    private WebSocketServer server;                                   // lock ile korunur
    private final Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger();            // onOpen birden cok is parcaciginda calisir
    private final ThreadPoolExecutor lifecycle = newLifecycleExecutor();

    /** Tek is parcacikli, bosta kapanan, sirali (FIFO) yurutucu: "durdur" her zaman sonraki "baslat"tan once biter. */
    private static ThreadPoolExecutor newLifecycleExecutor() {
        ThreadPoolExecutor ex = new ThreadPoolExecutor(1, 1, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "LanServerLifecycle");
                t.setDaemon(true);
                return t;
            }
        });
        ex.allowCoreThreadTimeOut(true);
        return ex;
    }

    // ------------------------------------------------------------------ yerel IP

    /** Bir ag arayuzunun sade ozeti: NetworkInterface'ten ayri tutulur ki secim mantigi tek basina denenebilsin. */
    static final class Cand {
        final String name;
        final String ip;
        final boolean pointToPoint;
        int score;

        Cand(String name, String ip, boolean pointToPoint) {
            this.name = name == null ? "" : name;
            this.ip = ip;
            this.pointToPoint = pointToPoint;
        }
    }

    /** Mobil veri, VPN, Wi-Fi Direct ve sanal arayuzlerin adlari: bunlar oyun odasi icin yanlis adrestir. */
    private static boolean isUnusableName(String lower) {
        String[] bad = { "rmnet", "ccmni", "pdp", "ppp", "tun", "tap", "wg", "ipsec", "v4-", "clat", "dummy", "p2p", "docker", "veth", "tailscale" };
        for (String b : bad) {
            if (lower.startsWith(b)) return true;
        }
        return false;
    }

    /** Son sekizli tam olarak "1" ise telefon o alt agin cikis kapisidir (hotspot): ornegin 192.168.43.1, 172.20.10.1 */
    private static boolean isGatewayLike(String ip) {
        int i = ip.lastIndexOf('.');
        return i > 0 && "1".equals(ip.substring(i + 1));
    }

    /**
     * Adaylari eler ve en iyiden en kotuye siralar:
     *  4 = hotspot (telefon agin cikis kapisi: ap*, softap*, swlan*, ya da wlan* uzerinde adres ".1" ile biter)
     *  3 = wlan* (bir yonlendiriciye bagli Wi-Fi)    2 = eth*, usb*, rndis* vb.    1 = diger bilinmeyen arayuzler
     * Mobil veri (rmnet*, ccmni*...), VPN (tun*, wg*...), noktadan noktaya arayuzler ve Wi-Fi Direct hic dahil edilmez:
     * operatorler cogu zaman bu arayuzlere de 10.x gibi ozel adresler verir ve oda kodu yanlis cihaza isaret ederdi.
     */
    static List<Cand> rank(List<Cand> in) {
        List<Cand> out = new ArrayList<>();
        for (Cand c : in) {
            String n = c.name.toLowerCase(Locale.ROOT);
            if (c.pointToPoint || isUnusableName(n) || c.ip == null) continue;
            boolean gatewayLike = isGatewayLike(c.ip);
            if (n.startsWith("ap") || n.startsWith("softap") || n.startsWith("swlan") || (n.startsWith("wlan") && gatewayLike)) c.score = 4;
            else if (n.startsWith("wlan")) c.score = 3;
            else if (n.startsWith("eth") || n.startsWith("usb") || n.startsWith("rndis") || n.startsWith("en")) c.score = 2;
            else c.score = 1;
            out.add(c);
        }
        Collections.sort(out, new Comparator<Cand>() {
            @Override
            public int compare(Cand a, Cand b) {
                if (a.score != b.score) return b.score - a.score;
                int byName = a.name.compareTo(b.name);
                return byName != 0 ? byName : a.ip.compareTo(b.ip);
            }
        });
        return out;
    }

    /** Cihazdaki ozel (site-local) IPv4 adresleri; IPv6 ve genel adresler oda kodu icin kullanilamaz. */
    private static List<Cand> collect() {
        List<Cand> out = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if (en == null) return out;
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                try {
                    if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                    boolean p2p = ni.isPointToPoint();
                    Enumeration<InetAddress> addrs = ni.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress a = addrs.nextElement();
                        if (a instanceof Inet4Address && !a.isLoopbackAddress() && a.isSiteLocalAddress()) {
                            out.add(new Cand(ni.getName(), a.getHostAddress(), p2p));
                        }
                    }
                } catch (Exception ignored) { }                       // tek bir arayuzun hatasi digerlerini engellemesin
            }
        } catch (Exception ignored) { }
        return out;
    }

    /** Oda kodunun uretilecegi adres (yoksa null). Testlerde ezilebilsin diye korumali. */
    protected String findLocalIp() {
        List<Cand> l = rank(collect());
        return l.isEmpty() ? null : l.get(0).ip;
    }

    @PluginMethod
    public void getLocalIp(PluginCall call) {
        try {
            JSObject ret = new JSObject();
            List<Cand> l = rank(collect());
            ret.put("ip", l.isEmpty() ? null : l.get(0).ip);
            JSONArray all = new JSONArray();
            for (Cand c : l) all.put(c.ip);
            ret.put("ips", all);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Ağ adresi okunamadı");
        }
    }

    // ------------------------------------------------------------------ sunucu yasam dongusu

    @PluginMethod
    public void start(final PluginCall call) {
        try {
            Integer p = call.getInt("port", DEFAULT_PORT);
            final int port = (p != null && p >= 1024 && p <= 65535) ? p : DEFAULT_PORT;
            final String ip = findLocalIp();
            if (ip == null) {
                call.reject("Wi-Fi ya da hotspot bağlantısı yok");
                return;
            }
            submit(new Runnable() {
                @Override
                public void run() {
                    startNow(call, port, ip);
                }
            });
        } catch (Exception e) {
            call.reject("Oda kurulamadı. Wi-Fi bağlantısını kontrol et");
        }
    }

    @PluginMethod
    public void stop(PluginCall call) {
        try {
            submit(new Runnable() {
                @Override
                public void run() {
                    stopNow();
                }
            });
        } catch (Exception ignored) { }
        call.resolve();
    }

    private void submit(Runnable r) {
        try {
            lifecycle.execute(r);
        } catch (RejectedExecutionException e) {                      // yurutucu kapanmis (uygulama kapaniyor): isi yine de yap
            new Thread(r, "LanServerLifecycleLate").start();
        }
    }

    /** Eski sunucuyu TAMAMEN kapatir (baglantilar kapanir, soket birakilir); yalniz yasam dongusu is parcaciginda cagrilir. */
    private void stopNow() {
        final WebSocketServer s;
        synchronized (lock) {
            s = server;
            server = null;
        }
        if (s == null) return;
        try {
            s.stop(STOP_WAIT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) { }
        clients.clear();                                          // takilan kayit yeni odanin istemci sinirini doldurmasin
    }

    private void startNow(PluginCall call, int port, String ip) {
        try {
            stopNow();                                                // ayni port hemen yeniden acilabilsin diye eskisi once tamamen kapanir
            Exception last = null;
            for (int attempt = 0; attempt < BIND_ATTEMPTS; attempt++) {
                final CountDownLatch up = new CountDownLatch(1);
                final AtomicReference<Exception> failure = new AtomicReference<>();
                WebSocketServer s = buildServer(port, up, failure);
                synchronized (lock) {
                    server = s;
                }
                s.start();
                boolean ready = up.await(START_WAIT_MS, TimeUnit.MILLISECONDS);
                Exception f = failure.get();
                if (ready && f == null) {
                    JSObject ret = new JSObject();
                    ret.put("ip", ip);
                    ret.put("port", port);
                    call.resolve(ret);
                    return;
                }
                synchronized (lock) {
                    if (server == s) server = null;
                }
                try { s.stop(200); } catch (Exception ignored) { }
                last = f != null ? f : new java.io.IOException("zaman asimi");
                if (!(f instanceof BindException)) break;             // yalniz "port dolu" tekrar denenir (eski soket birazdan birakilabilir)
                Thread.sleep(BIND_RETRY_MS);
            }
            call.reject(last instanceof BindException
                    ? "Oda kurulamadı: bağlantı noktası başka bir uygulamada kullanımda"
                    : "Oda kurulamadı. Wi-Fi bağlantısını kontrol et");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            call.reject("Oda kurulamadı");
        } catch (Exception e) {
            call.reject("Oda kurulamadı. Wi-Fi bağlantısını kontrol et");
        }
    }

    private WebSocketServer buildServer(final int port, final CountDownLatch up, final AtomicReference<Exception> failure) {
        // Tek bir cerceve / mesaj icin ust sinir: Draft_6455'in varsayilani neredeyse sinirsizdir (bellek tuketimi ile cokertme riski)
        List<Draft> drafts = new ArrayList<>();
        drafts.add(new Draft_6455(Collections.<IExtension>emptyList(), Collections.<IProtocol>singletonList(new Protocol("")), MAX_MESSAGE_BYTES));
        WebSocketServer s = new WebSocketServer(new InetSocketAddress(port), 2, drafts) {
            @Override
            public void onStart() {
                up.countDown();
            }

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                if (clients.size() >= MAX_CLIENTS) {
                    conn.close(CloseFrame.TRY_AGAIN_LATER);
                    return;
                }
                String id = "c" + seq.incrementAndGet();
                conn.setAttachment(id);
                clients.put(id, conn);
                JSObject o = new JSObject();
                o.put("id", id);
                notifyListeners("clientOpen", o);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                String id = conn.getAttachment();
                if (id == null) return;
                clients.remove(id);
                JSObject o = new JSObject();
                o.put("id", id);
                notifyListeners("clientClose", o);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                String id = conn.getAttachment();
                if (id == null || message == null) return;
                JSObject o = new JSObject();
                o.put("id", id);
                o.put("data", message);
                notifyListeners("clientMessage", o);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                if (conn != null) return;                             // tek bir baglantinin hatasi: kapanisi onClose bildirir
                if (up.getCount() > 0) {                              // sunucu baslatilamadi (ornegin port dolu)
                    failure.set(ex == null ? new java.io.IOException("bilinmeyen hata") : ex);
                    up.countDown();
                } else {                                              // calisirken sunucunun kendisi coktu: JS odayi kapatir
                    notifyListeners("serverError", new JSObject());
                }
            }
        };
        s.setReuseAddr(true);
        s.setTcpNoDelay(true);                                        // kucuk ve sik mesajlar (ilerleme, ping) Nagle gecikmesi yemesin
        s.setConnectionLostTimeout(20);
        return s;
    }

    // ------------------------------------------------------------------ mesajlasma (hicbiri istisna firlatmaz)

    private static void sendTo(WebSocket c, String data) {
        if (c == null) return;
        try {
            if (c.isOpen()) c.send(data);
        } catch (Exception ignored) { }                               // baglanti tam bu sirada kopmus olabilir
    }

    @PluginMethod
    public void send(PluginCall call) {
        try {
            sendTo(clients.get(call.getString("id", "")), call.getString("data", ""));
        } catch (Exception ignored) { }
        call.resolve();
    }

    @PluginMethod
    public void broadcast(PluginCall call) {
        try {
            String data = call.getString("data", "");
            for (WebSocket c : clients.values()) sendTo(c, data);
        } catch (Exception ignored) { }
        call.resolve();
    }

    @PluginMethod
    public void close(PluginCall call) {
        try {
            WebSocket c = clients.get(call.getString("id", ""));
            if (c != null) c.close();
        } catch (Exception ignored) { }
        call.resolve();
    }

    @Override
    protected void handleOnDestroy() {
        try {
            submit(new Runnable() {
                @Override
                public void run() {
                    stopNow();
                }
            });
            lifecycle.shutdown();                                     // kuyruktaki "durdur" biter, sonra is parcacigi sonlanir
        } catch (Exception ignored) { }
    }
}
