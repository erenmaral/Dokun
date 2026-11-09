package com.erenmaral.dokun;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Yerel ag (Wi-Fi / hotspot) uzerinden "oda kur" icin telefonda kucuk bir WebSocket sunucusu acar.
 * Misafirler WebView icindeki normal WebSocket ile baglanir; bu sinif yalnizca sunucu tarafini ve yerel IP'yi saglar.
 *
 * JS tarafi: getLocalIp(), start({port}), stop(), send({id,data}), broadcast({data}), close({id})
 * Olaylar: clientOpen {id}, clientMessage {id,data}, clientClose {id}
 */
@CapacitorPlugin(name = "LanServer")
public class LanServerPlugin extends Plugin {
    private WebSocketServer server;
    private final Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private int seq = 0;

    /** Wi-Fi / hotspot arayuzundeki ozel (site-local) IPv4 adresini bulur; mobil veri arayuzlerine daha dusuk puan verir. */
    private String findLocalIp() {
        String best = null;
        int bestScore = -1;
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                String name = ni.getName().toLowerCase();
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (!(a instanceof Inet4Address) || a.isLoopbackAddress() || !a.isSiteLocalAddress()) continue;
                    int score = name.startsWith("wlan") ? 3
                            : (name.startsWith("ap") || name.startsWith("swlan") || name.startsWith("eth")) ? 2 : 1;
                    if (score > bestScore) {
                        best = a.getHostAddress();
                        bestScore = score;
                    }
                }
            }
        } catch (Exception ignored) { }
        return best;
    }

    @PluginMethod
    public void getLocalIp(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("ip", findLocalIp());
        call.resolve(ret);
    }

    @PluginMethod
    public void start(final PluginCall call) {
        final int port = call.getInt("port", 47813);
        stopServer();
        final String ip = findLocalIp();
        if (ip == null) {
            call.reject("Wi-Fi bağlantısı yok");
            return;
        }
        server = new WebSocketServer(new InetSocketAddress(port)) {
            private boolean answered = false;

            private synchronized void answer(Exception err) {
                if (answered) return;
                answered = true;
                if (err == null) {
                    JSObject ret = new JSObject();
                    ret.put("ip", ip);
                    ret.put("port", port);
                    call.resolve(ret);
                } else {
                    call.reject("Sunucu açılamadı: " + err.getMessage());
                }
            }

            @Override
            public void onStart() { answer(null); }

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                String id = "c" + (++seq);
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
                if (id == null) return;
                JSObject o = new JSObject();
                o.put("id", id);
                o.put("data", message);
                notifyListeners("clientMessage", o);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                if (conn == null) answer(ex);          // sunucu baslatilamadi (ornegin port dolu)
            }
        };
        server.setReuseAddr(true);
        server.setConnectionLostTimeout(20);
        server.start();
    }

    @PluginMethod
    public void send(PluginCall call) {
        WebSocket c = clients.get(call.getString("id", ""));
        String data = call.getString("data", "");
        if (c != null && c.isOpen()) c.send(data);
        call.resolve();
    }

    @PluginMethod
    public void broadcast(PluginCall call) {
        String data = call.getString("data", "");
        for (WebSocket c : clients.values()) if (c.isOpen()) c.send(data);
        call.resolve();
    }

    @PluginMethod
    public void close(PluginCall call) {
        WebSocket c = clients.get(call.getString("id", ""));
        if (c != null) c.close();
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        stopServer();
        call.resolve();
    }

    private void stopServer() {
        final WebSocketServer s = server;
        server = null;
        clients.clear();
        if (s == null) return;
        new Thread(() -> {
            try { s.stop(300); } catch (Exception ignored) { }
        }).start();
    }

    @Override
    protected void handleOnDestroy() {
        stopServer();
    }
}
