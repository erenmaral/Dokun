# Dokun!

Aynı telefonu paylaşan 2-4 arkadaş için hızlı mini oyunlar. Ekran oyuncu sayısına göre bölünür, üstteki oyuncular karşıdan oynar. Çoklu dokunuş desteklenir. Tek dosya (`index.html`), harici dosya yok.

12 mini oyun var. Menüden tur sayısı seçilir (5 / 8 / 12), oyunlar rastgele sırayla ve tekrarsız oynanır.
- **Tepki:** yeşil olunca ilk dokunan kazanır, erken dokunan o turdan elenir.
- **Seri Dokun:** 5 saniyede en çok dokunan kazanır.
- **Tam Ortada:** kayan topu ortadaki çizgiye en yakın durduran kazanır.
- **Hedef Avı:** çıkan daireye dokun, 6 hedefe ilk ulaşan kazanır.
- **Sayı Sırası:** 1'den 6'ya sırayla dokun, ilk bitiren kazanır.
- **Tek Farklı:** farklı emojiyi ilk bulan kazanır, yanlış dokunan 1,2 sn cezalı.
- **Renk Tuzağı:** yazının ne yazdığına değil rengine bak, doğru rengi seç. İlk 4 doğruya ulaşan kazanır.
- **Hızlı Matematik:** doğru sonucu seç. İlk 4 doğruya ulaşan kazanır, yanlışta 1 sn ceza.
- **Işık Oyunu:** yeşilde dokun, kırmızıda dokunma. Herkes aynı ışık dizisini görür, yanlış dokunuş puan düşürür.
- **Renk Dizisi:** yanıp sönen 4 rengi aynı sırayla tekrarla, ilk bitiren kazanır.
- **Balon Patlat:** 9 saniyede en çok balonu patlat, bombalar 2 puan götürür.
- **Tut ve Bırak:** basılı tut, tam 3 saniye dolunca bırak. Sayaç ilk 1 saniye görünür, sonra gizlenir.

Telefonu dikey tut. Tarayıcıda yatay çevrilirse "Telefonu dikey tut" uyarısı çıkar.

Ses ve titreşim: tüm sesler Web Audio ile kodla üretilir (dosya yok). Titreşim `navigator.vibrate` ile çalışır; iPhone Safari titreşimi desteklemez, orada yalnızca ses olur. Menüden açılıp kapatılır, tercih hatırlanır.

## Oynamak
Canlı adres: https://erenmaral.github.io/dokun/
Telefonda dikey kullan. Yerelde: `npx serve .` veya `index.html` dosyasını tarayıcıda aç.

## APK (Android)
Her `main` push'unda GitHub Actions bir APK derler (Capacitor, debug anahtarıyla imzalı, dikey kilitli).
İndirmek için: depo → **Actions** → en yeni "Android APK" çalışması → **Artifacts** altındaki `Dokun-N` (zip içinde `.apk`). Release izni varsa **Releases** sayfasında da görünür.
Telefona kurmak için "bilinmeyen kaynaklardan yüklemeye" izin vermek gerekir. Debug anahtarı Play Store içindir değil, yalnızca denemek içindir.
Yerelde: `npm ci && npm run sync` sonra `android/` klasörünü Android Studio'da aç.

## Yayın
`.github/workflows/pages.yml` varsayılan daldaki `index.html` dosyasını GitHub Pages'e yayınlar.
Bir kez: depo → Settings → Pages → Source: **GitHub Actions**.
