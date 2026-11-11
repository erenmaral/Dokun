package com.erenmaral.dokun;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    // Capacitor cekirdegi (@capacitor/app eklentisi olmadan) donanim geri tusunu hic islemez: tus dogrudan Activity varsayilanina duser
    // (uygulamadan cikar / arka plana atar). Bu yuzden geri tusu once oyuna sorulur: JS tarafindaki window.__dokunBack() true dondururse
    // uygulama kendi icinde geri gitmistir (alt ekrandan menuye, macta cikis uyarisi vb.); false ise sistemin varsayilani calisir.
    private static final String BACK_JS =
            "(function(){try{return typeof window.__dokunBack==='function'&&window.__dokunBack()===true;}catch(e){return false;}})()";
    // JS cevap vermezse (sayfa donmus / yuklenmemis / eski WebView hata sayfasi) geri tusu yine de calissin
    private static final long BACK_TIMEOUT_MS = 700;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean backPending = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(LanServerPlugin.class);     // yerel ag sunucusu (kisa kodla baglanti); super.onCreate'ten ONCE kaydedilmeli
        super.onCreate(savedInstanceState);
        applyWebViewSettings();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onHardwareBack(this);
            }
        });
    }

    // Oyun arayuzu piksel olculerle tasarlandi: telefonun "yazi boyutu" ayari WebView yazilarini buyutup
    // kartlarin ust uste binmesine yol acmasin diye sistem yazi olcegi yok sayilir.
    private void applyWebViewSettings() {
        try {
            if (getBridge() != null && getBridge().getWebView() != null) {
                getBridge().getWebView().getSettings().setTextZoom(100);
            }
        } catch (Exception ignored) { }
    }

    // Yazi boyutu / yon / dil degisince etkinlik yeniden olusturulmaz (manifestte configChanges); yarim kalan mac kaybolmasin
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyWebViewSettings();
    }

    @Override
    public void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        backPending = false;
        super.onDestroy();
    }

    private void onHardwareBack(final OnBackPressedCallback callback) {
        if (backPending) {
            return;                                // bir onceki geri tusu hala JS'ten cevap bekliyor
        }
        WebView webView = null;
        try {
            if (getBridge() != null) {
                webView = getBridge().getWebView();
            }
        } catch (Exception ignored) { }
        if (webView == null) {
            defaultBack(callback);
            return;
        }
        backPending = true;
        final boolean[] answered = { false };
        final Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if (answered[0]) {
                    return;
                }
                answered[0] = true;
                backPending = false;
                defaultBack(callback);
            }
        };
        mainHandler.postDelayed(timeout, BACK_TIMEOUT_MS);
        try {
            webView.evaluateJavascript(BACK_JS, value -> {
                if (answered[0]) {
                    return;
                }
                answered[0] = true;
                mainHandler.removeCallbacks(timeout);
                backPending = false;
                if (!"true".equals(value)) {
                    defaultBack(callback);
                }
            });
        } catch (Exception e) {
            if (!answered[0]) {
                answered[0] = true;
                mainHandler.removeCallbacks(timeout);
                backPending = false;
                defaultBack(callback);
            }
        }
    }

    // Sistemin varsayilan geri davranisi (ana menude: uygulamadan cik / arka plana at)
    private void defaultBack(OnBackPressedCallback callback) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        callback.setEnabled(false);
        try {
            getOnBackPressedDispatcher().onBackPressed();
        } finally {
            callback.setEnabled(true);
        }
    }
}
