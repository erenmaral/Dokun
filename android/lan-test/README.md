# LanServerPlugin JVM deneme duzenegi

Capacitor ve Android olmadan, **gercek** `LanServerPlugin.java` ve gercek Java-WebSocket ile (Capacitor siniflari `stubs/` altinda kucuk taslaklar):

- ayni portta hizli durdur / baslat, ust uste start, baska surec portu tutuyorsa Turkce hata
- asiri buyuk mesaj (16 KB siniri, kod 1009), istemci siniri (12), es zamanli baglantilarda benzersiz kimlik
- `send` / `broadcast` / `close` istisna firlatmaz (Capacitor eklenti is parcaciginda istisna uygulamayi cokertir)
- `handleOnDestroy` portu serbest birakir; gercek Chromium WebSocket istemcisiyle el sikisma uyumu (`Serve`)

Calistirma (JDK 17+ ve Maven Central erisimi gerekir; AGP / Android SDK gerekmez):

```
./run.sh            # tum senaryolar
./run.sh restart    # tek senaryo (restart startstart basic big race many throw badport destroy)
```

IP secim mantigi (`rank`) icin ayrica birim testi: `android/app/src/test/java/com/erenmaral/dokun/LanServerPluginTest.java`
(`cd android && ./gradlew testDebugUnitTest`).
