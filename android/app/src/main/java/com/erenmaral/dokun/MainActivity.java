package com.erenmaral.dokun;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(LanServerPlugin.class);     // yerel ag sunucusu (kisa kodla baglanti)
        super.onCreate(savedInstanceState);
        // Oyun arayuzu piksel olculerle tasarlandi: telefonun "yazi boyutu" ayari WebView yazilarini buyutup
        // kartlarin ust uste binmesine yol acmasin diye sistem yazi olcegi yok sayilir.
        try {
            getBridge().getWebView().getSettings().setTextZoom(100);
        } catch (Exception ignored) { }
    }
}
