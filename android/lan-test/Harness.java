import com.getcapacitor.*;
import com.erenmaral.dokun.LanServerPlugin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/** LanServerPlugin'i JVM'de, gercek Java-WebSocket ve gercek soketlerle dener (Capacitor sinifleri taslak). */
public class Harness {
    static int PORT = Integer.getInteger("port", 47820);   // -Dport=... ile degistirilebilir
    static int pass = 0, fail = 0;
    static void ok(String name, boolean c, String extra) { if (c) pass++; else fail++; System.out.println((c ? "OK   " : "HATA ") + name + (extra == null || extra.isEmpty() ? "" : "  " + extra)); }

    static class Cli extends WebSocketClient {
        final BlockingQueue<String> msgs = new LinkedBlockingQueue<>();
        volatile int closeCode = -1; volatile String closeReason = ""; final CountDownLatch opened = new CountDownLatch(1), closed = new CountDownLatch(1);
        Cli(int port) throws Exception { super(new URI("ws://127.0.0.1:" + port)); }
        public void onOpen(ServerHandshake h) { opened.countDown(); }
        public void onMessage(String m) { msgs.add(m); }
        public void onClose(int code, String reason, boolean remote) { closeCode = code; closeReason = reason; closed.countDown(); }
        public void onError(Exception e) { }
    }

    static Object call(LanServerPlugin p, String m, PluginCall c, long ms) throws Exception {
        switch (m) {
            case "start": p.start(c); break; case "stop": p.stop(c); break; case "send": p.send(c); break;
            case "close": p.close(c); break; case "broadcast": p.broadcast(c); break; case "getLocalIp": p.getLocalIp(c); break;
            default: throw new IllegalArgumentException(m);
        }
        return c.await(ms);
    }
    static String startOn(LanServerPlugin p, int port) throws Exception {
        Object r = call(p, "start", new PluginCall().with("port", port), 8000);
        return r == null ? "ZAMAN ASIMI" : r.toString();
    }
    static String nextEvent(Plugin p, String prefix, long ms) throws Exception {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) {
            String e = p.events.poll(50, TimeUnit.MILLISECONDS);
            if (e != null && e.startsWith(prefix)) return e;
        }
        return null;
    }

    public static void main(String[] a) throws Exception {
        String only = a.length > 0 ? a[0] : "all";
        LanServerPlugin p = new TestPlugin();

        if (only.equals("all") || only.contains("restart")) {
            System.out.println("--- 1) durdur / yeniden baslat: ayni port ---");
            int bad = 0, n = 25; StringBuilder errs = new StringBuilder();
            for (int i = 0; i < n; i++) {
                String r = startOn(p, PORT);
                if (!r.contains("ip")) { bad++; if (errs.length() < 300) errs.append(r).append(" | "); }
                Cli c = new Cli(PORT); c.connectBlocking(2, TimeUnit.SECONDS);
                if (i % 2 == 0) c.close();
                Object s = call(p, "stop", new PluginCall(), 3000);
                if (i % 3 == 0) Thread.sleep(5);
            }
            ok("ayni portta " + n + " hizli durdur/baslat dongusu hatasiz", bad == 0, bad + " hata " + errs);
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(600);
        }

        if (only.equals("all") || only.contains("startstart")) {
            System.out.println("--- 2) stop cagirmadan ust uste start (oda kur, geri, oda kur) ---");
            int bad = 0; StringBuilder errs = new StringBuilder();
            for (int i = 0; i < 15; i++) { String r = startOn(p, PORT); if (!r.contains("ip")) { bad++; if (errs.length() < 300) errs.append(r).append(" | "); } }
            ok("ust uste start hatasiz", bad == 0, bad + " hata " + errs);
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(600);
        }

        if (only.equals("all") || only.contains("basic")) {
            System.out.println("--- 3) temel akis: baglan, mesaj, gonder, kapat ---");
            String r = startOn(p, PORT); ok("start", r.contains("\"ip\""), r);
            Cli c1 = new Cli(PORT); ok("istemci 1 baglandi", c1.connectBlocking(3, TimeUnit.SECONDS), "");
            String ev = nextEvent(p, "clientOpen", 2000); ok("clientOpen olayi", ev != null, ev);
            String id = ev == null ? "?" : new org.json.JSONObject(ev.substring("clientOpen ".length())).getString("id");
            c1.send("{\"t\":\"hello\"}");
            String m = nextEvent(p, "clientMessage", 2000); ok("clientMessage olayi", m != null && m.contains("hello") && m.contains(id), m);
            call(p, "send", new PluginCall().with("id", id).with("data", "merhaba"), 1000);
            String got = c1.msgs.poll(2, TimeUnit.SECONDS); ok("send -> istemci alir", "merhaba".equals(got), got);
            call(p, "broadcast", new PluginCall().with("data", "herkese"), 1000);
            got = c1.msgs.poll(2, TimeUnit.SECONDS); ok("broadcast -> istemci alir", "herkese".equals(got), got);
            call(p, "close", new PluginCall().with("id", id), 1000);
            ok("close -> istemci kapanir", c1.closed.await(3, TimeUnit.SECONDS), "kod " + c1.closeCode);
            String cc = nextEvent(p, "clientClose", 2000); ok("clientClose olayi", cc != null && cc.contains(id), cc);
            call(p, "send", new PluginCall().with("id", id).with("data", "x"), 1000);
            Object sr = call(p, "send", new PluginCall().with("id", "yok").with("data", "x"), 1000);
            ok("kapali / bilinmeyen kimlige send sessizce tamam", sr != null && !sr.toString().startsWith("REJECT"), String.valueOf(sr));
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("big")) {
            System.out.println("--- 4) cok buyuk mesaj ---");
            String r = startOn(p, PORT); ok("start", r.contains("\"ip\""), r);
            Cli c = new Cli(PORT); c.connectBlocking(3, TimeUnit.SECONDS); nextEvent(p, "clientOpen", 2000);
            StringBuilder sb = new StringBuilder(); for (int i = 0; i < 3 * 1024 * 1024; i++) sb.append('x');
            c.send(sb.toString());
            String m = nextEvent(p, "clientMessage", 4000);
            boolean accepted = m != null && m.length() > 1_000_000;
            ok("3 MB mesaj reddedilir (yalnizca kucuk protokol mesajlari gecmeli)", !accepted, accepted ? "KABUL EDILDI, uzunluk " + m.length() : "olay yok, kod " + c.closeCode);
            boolean closed = c.closed.await(3, TimeUnit.SECONDS);
            ok("asiri buyuk mesaj gonderen baglanti kapatilir", closed, "kod " + c.closeCode);
            Cli c2 = new Cli(PORT); ok("sunucu hala yeni baglanti kabul eder", c2.connectBlocking(3, TimeUnit.SECONDS), "");
            c2.send("{\"t\":\"ping\"}"); String m2 = nextEvent(p, "clientMessage", 2000); ok("ve mesaj alir", m2 != null && m2.contains("ping"), m2);
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("race")) {
            System.out.println("--- 5) es zamanli baglantilarda kimlik benzersizligi ---");
            String r = startOn(p, PORT); ok("start", r.contains("\"ip\""), r);
            int N = 10; List<Cli> cs = new ArrayList<>(); ExecutorService ex = Executors.newFixedThreadPool(N); CountDownLatch go = new CountDownLatch(1);
            List<Future<Boolean>> fs = new ArrayList<>();
            for (int i = 0; i < N; i++) { final Cli c = new Cli(PORT); cs.add(c); fs.add(ex.submit(() -> { go.await(); return c.connectBlocking(5, TimeUnit.SECONDS); })); }
            go.countDown(); int okc = 0; for (Future<Boolean> f : fs) if (f.get()) okc++;
            Thread.sleep(800);
            Set<String> ids = new HashSet<>(); int opens = 0;
            for (String e : new ArrayList<>(p.eventLog)) if (e.startsWith("clientOpen")) { opens++; ids.add(new org.json.JSONObject(e.substring(11)).getString("id")); }
            ok("tum baglantilar acildi (en fazla " + N + ")", okc == N && opens >= Math.min(okc, 12), "baglanan " + okc + " olay " + opens);
            ok("kimlikler benzersiz", ids.size() == opens, ids.size() + " / " + opens);
            for (Cli c : cs) c.close(); ex.shutdownNow();
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("many")) {
            System.out.println("--- 6) cok sayida baglanti (4 kisilik oda icin sinir) ---");
            String r = startOn(p, PORT);
            List<Cli> cs = new ArrayList<>(); int accepted = 0;
            for (int i = 0; i < 40; i++) { Cli c = new Cli(PORT); cs.add(c); if (c.connectBlocking(2, TimeUnit.SECONDS)) accepted++; }
            Thread.sleep(500);
            int opens = 0; for (String e : new ArrayList<>(p.eventLog)) if (e.startsWith("clientOpen")) opens++;
            ok("40 baglantidan fazlasi sinirlanir (olay sayisi <= 12)", opens <= 12, "clientOpen olay sayisi " + opens + ", el sikisan " + accepted);
            for (Cli c : cs) c.close(); Thread.sleep(300);
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("throw")) {
            System.out.println("--- 7) send sirasinda koparak giden baglanti: eklenti yontemi istisna firlatmamali ---");
            String r = startOn(p, PORT);
            Cli c = new Cli(PORT); c.connectBlocking(3, TimeUnit.SECONDS); String ev = nextEvent(p, "clientOpen", 2000);
            String id = new org.json.JSONObject(ev.substring(11)).getString("id");
            // gercek yarisi taklit: isOpen() dogru, send() istisna
            java.lang.reflect.Field f = LanServerPlugin.class.getDeclaredField("clients"); f.setAccessible(true);
            @SuppressWarnings("unchecked") Map<String, org.java_websocket.WebSocket> map = (Map<String, org.java_websocket.WebSocket>) f.get(p);
            final org.java_websocket.WebSocket real = map.get(id);
            org.java_websocket.WebSocket fake = (org.java_websocket.WebSocket) java.lang.reflect.Proxy.newProxyInstance(Harness.class.getClassLoader(), new Class[]{org.java_websocket.WebSocket.class}, (proxy, method, args) -> {
                if (method.getName().equals("isOpen")) return true;
                if (method.getName().equals("send")) throw new org.java_websocket.exceptions.WebsocketNotConnectedException();
                if (method.getName().equals("close")) throw new IllegalStateException("kapali");
                return method.invoke(real, args);
            });
            map.put(id, fake);
            boolean threw = false; String what = "";
            try { call(p, "send", new PluginCall().with("id", id).with("data", "x"), 1000); } catch (Throwable t) { threw = true; what = t.toString(); }
            ok("send: istisna disari sizmaz", !threw, what);
            threw = false; try { call(p, "broadcast", new PluginCall().with("data", "x"), 1000); } catch (Throwable t) { threw = true; what = t.toString(); }
            ok("broadcast: istisna disari sizmaz", !threw, what);
            threw = false; try { call(p, "close", new PluginCall().with("id", id), 1000); } catch (Throwable t) { threw = true; what = t.toString(); }
            ok("close: istisna disari sizmaz", !threw, what);
            map.put(id, real); c.close();
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("badport")) {
            System.out.println("--- 8) port baska surec tarafindan kullaniliyor: anlasilir (Turkce) hata ---");
            java.net.ServerSocket blocker = new java.net.ServerSocket(PORT + 1);
            long t0 = System.currentTimeMillis();
            String r = startOn(p, PORT + 1);
            long dt = System.currentTimeMillis() - t0;
            ok("start reddedilir", r.startsWith("REJECT"), r);
            ok("hata metni Turkce ve Ingilizce istisna metni icermez", r.startsWith("REJECT") && !r.toLowerCase().contains("address already") && !r.contains("java.") && r.matches(".*[ğüşıöçĞÜŞİÖÇ].*|.*(kullan|açıl|açıl).*"), r);
            ok("makul surede (<8 sn) yanit verir", dt < 8000, dt + " ms");
            blocker.close();
            String r2 = startOn(p, PORT + 1); ok("port bosalinca tekrar start calisir", r2.contains("\"ip\""), r2);
            call(p, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }

        if (only.equals("all") || only.contains("destroy")) {
            System.out.println("--- 9) yikim: handleOnDestroy sunucuyu kapatir, port serbest kalir ---");
            String r = startOn(p, PORT);
            Cli c = new Cli(PORT); c.connectBlocking(3, TimeUnit.SECONDS);
            p.callDestroy();
            ok("istemci kapatilir", c.closed.await(4, TimeUnit.SECONDS), "kod " + c.closeCode);
            Thread.sleep(500);
            boolean free; try (java.net.ServerSocket ss = new java.net.ServerSocket(PORT)) { free = true; } catch (Exception e) { free = false; }
            ok("port serbest", free, "");
            LanServerPlugin p2 = new TestPlugin(); String r2 = startOn(p2, PORT); ok("yeni eklenti ornegi ayni portu acar", r2.contains("\"ip\""), r2);
            call(p2, "stop", new PluginCall(), 3000); Thread.sleep(500);
        }
        System.out.println("TOPLAM: " + pass + " basarili, " + fail + " hatali");
        System.exit(fail == 0 ? 0 : 1);
    }

    /** IP bulma: test icin sabit */
    static class TestPlugin extends LanServerPlugin {
        @Override protected String findLocalIp() { return "127.0.0.1"; }
    }
}
