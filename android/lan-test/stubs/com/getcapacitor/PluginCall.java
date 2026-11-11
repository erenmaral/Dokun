package com.getcapacitor;
import java.util.concurrent.*;
/** Test taslagi: cagrinin sonucu bir kuyruga yazilir */
public class PluginCall {
    public final JSObject data = new JSObject();
    public final BlockingQueue<Object> results = new LinkedBlockingQueue<>();
    public PluginCall with(String k, Object v) { data.put(k, v); return this; }
    public Integer getInt(String name, Integer def) { Object v = data.opt(name); return v instanceof Integer ? (Integer) v : def; }
    public String getString(String name, String def) { Object v = data.opt(name); return v instanceof String ? (String) v : def; }
    public void resolve() { results.add(new JSObject()); }
    public void resolve(JSObject o) { results.add(o); }
    public void reject(String msg) { results.add("REJECT:" + msg); }
    public void reject(String msg, Exception ex) { results.add("REJECT:" + msg); }
    public Object await(long ms) throws InterruptedException { return results.poll(ms, TimeUnit.MILLISECONDS); }
}
