package com.getcapacitor;
import org.json.JSONObject;
/** Test taslagi: Capacitor JSObject'in kullanilan yuzeyi */
public class JSObject extends JSONObject {
    public JSObject() { super(); }
    @Override public JSObject put(String key, Object value) { try { super.put(key, value); } catch (org.json.JSONException e) { throw new RuntimeException(e); } return this; }
    public JSObject put(String key, String value) { return put(key, (Object) value); }
    public JSObject put(String key, int value) { return put(key, (Object) Integer.valueOf(value)); }
    public JSObject put(String key, boolean value) { return put(key, (Object) Boolean.valueOf(value)); }
}
