package com.getcapacitor;
import java.util.*;
import java.util.concurrent.*;
/** Test taslagi: Capacitor Plugin'in kullanilan yuzeyi (olaylar bir kuyruga yazilir) */
public class Plugin {
    public final BlockingQueue<String> events = new LinkedBlockingQueue<>();
    public final List<String> eventLog = Collections.synchronizedList(new ArrayList<String>());
    protected void notifyListeners(String eventName, JSObject data) { String s = eventName + " " + data; events.add(s); eventLog.add(s); }
    protected void handleOnDestroy() {}
    public void callDestroy() { handleOnDestroy(); }
}
