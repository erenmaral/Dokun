package com.erenmaral.dokun;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(LanServerPlugin.class);     // yerel ag sunucusu (kisa kodla baglanti)
        super.onCreate(savedInstanceState);
    }
}
