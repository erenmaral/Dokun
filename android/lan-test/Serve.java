import com.getcapacitor.*;
import com.erenmaral.dokun.LanServerPlugin;
import java.util.concurrent.TimeUnit;

/** LanServerPlugin'i bir echo sunucusu olarak calistirir (Playwright/Chromium istemcisi ile gercek WebSocket uyumlulugu denemesi) */
public class Serve {
    static class P extends LanServerPlugin { @Override protected String findLocalIp() { return "127.0.0.1"; } }
    public static void main(String[] a) throws Exception {
        int port = Integer.parseInt(a[0]); int secs = Integer.parseInt(a[1]);
        P p = new P(); PluginCall c = new PluginCall().with("port", port); p.start(c);
        Object r = c.await(8000); System.out.println("START " + r); System.out.flush();
        long end = System.currentTimeMillis() + secs * 1000L;
        while (System.currentTimeMillis() < end) {
            String e = p.events.poll(100, TimeUnit.MILLISECONDS);
            if (e == null) continue;
            System.out.println("EVT " + e.substring(0, Math.min(120, e.length()))); System.out.flush();
            if (e.startsWith("clientMessage ")) {
                org.json.JSONObject o = new org.json.JSONObject(e.substring(14));
                PluginCall s = new PluginCall().with("id", o.getString("id")).with("data", "echo:" + o.getString("data")); p.send(s);
            }
            if (e.startsWith("clientOpen ")) { org.json.JSONObject o = new org.json.JSONObject(e.substring(11)); p.send(new PluginCall().with("id", o.getString("id")).with("data", "{\"t\":\"hosgeldin\"}")); }
        }
        PluginCall st = new PluginCall(); p.stop(st); st.await(2000); Thread.sleep(1500); System.out.println("STOPPED"); System.exit(0);
    }
}
