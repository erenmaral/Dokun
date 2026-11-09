# Dokun!

Aynı telefonu paylaşan 2-4 arkadaş için hızlı mini oyunlar. Ekran oyuncu sayısına göre bölünür, üstteki oyuncular karşıdan oynar. Çoklu dokunuş desteklenir. Tek dosya (`index.html`), harici dosya yok.

16 mini oyun var. Ana menüden iki şekilde oynanır:
- **🎲 Karışık Maç:** oyunlar rastgele sırayla ve tekrarsız oynanır (3 / 5 / 8 / 12 tur).
- **🎮 Oyun Seç:** listeden bir oyun seçilir ve yalnızca o oyun seçilen tur sayısı kadar oynanır.

Her turdan önce 3-2-1 geri sayım vardır. Maç bitince kazanan bölge **altın renkte yanıp söner**, "👑 KAZANAN" yazar ve konfeti saçar. Diğer bölgeler sönükleşir ve sıralamasını gösterir. Sonuç her bölgede o oyuncuya doğru yazıldığı için iki yönden de okunur. Ardından "Tekrar" ve "Menü" düğmeleri çıkar.

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
- **Balon Patlat:** 10 saniyede en çok balonu patlat. Balonun tam üstüne basmak gerekmez, dokunduğun yere en yakın balon patlar. Bomba 1 puan götürür.
- **Tut ve Bırak:** basılı tut, tam 3 saniye dolunca bırak. Sayaç ilk 1 saniye görünür, sonra gizlenir.
- **Köstebek Avı:** deliklerden çıkan köstebeğe dokun, 10 saniyede en çok vuran kazanır.
- **Ok Yönü:** okun yönünü seç, "TERSİNE" yazıyorsa karşı yönü. İlk 5 doğruya ulaşan kazanır.
- **Hafıza Eşleştir:** kartları çevirip 4 çifti ilk bulan kazanır.
- **Say Bakalım:** ekrandaki şekilleri say, doğru sayıyı seç. İlk 4 doğruya ulaşan kazanır.

Her bölge koyu, nötr bir zeminde; oyuncu rengi yalnızca çerçeve ve etiket olarak kullanılır. Bilgi kutuları karşıdaki oyuncu için ters kopya olarak da gösterilir.

Telefonu dikey tut. Tarayıcıda yatay çevrilirse "Telefonu dikey tut" uyarısı çıkar.

## Modlar
Ana menüden oyuncu sayısı ve mod seçilir:
- **Klasik:** 3 / 5 / 8 / 12 tur, en çok puan alan kazanır.
- **Yarış:** hedef puana (3 / 5 / 7) ilk ulaşan kazanır.
- **Hayatta Kal:** her oyuncunun canı vardır (1 / 2 / 3). Turu kazanmayan can kaybeder, canı biten elenir, son kalan kazanır.
- **Takım:** 4 oyuncu, üstteki ikili alttakilere karşı, takım puanları toplanır.

**🎲 Karışık Maç** tüm oyunlardan, **🎮 Oyun Seç** ise listeden seçtiğin bir ya da birden fazla oyundan oynar.

## Duraklatma ve çıkış
Ekranın sol ve sağ kenarının ortasındaki ⏸ sekmesi oyunu duraklatır. Geri sayım, süreler ve balonlar gerçekten donar, devam edince kaldığı yerden sürer. Menüye dönmek için iki dokunuş gerekir. Telefon araması vb. ile uygulama arka plana giderse oyun kendiliğinden duraklar.
**Geri tuşu koruması:** oyun sırasında geri tuşuna (Android geri / tarayıcı geri / Esc) bir kez basmak yalnızca uyarı gösterir. 2,5 saniye içinde ikinci kez basılırsa oyundan çıkılır. Hızlı dokunulan oyunlarda (Seri Dokun gibi) yanlışlıkla çıkılmaz.

## Ses, müzik ve ayarlar
Ana menüdeki **⚙ Ayarlar**: ses efektleri, titreşim, müzik ve müzik sesi, ekranın açık kalması ve oyuncu adları. Tercihler hatırlanır.
Tüm sesler ve oyun içi sakin arka plan müziği Web Audio ile kodla üretilir (dosya yok). Titreşim `navigator.vibrate` ile çalışır; iPhone Safari titreşimi desteklemez.

## Çevrimiçi: her telefonda kendi ekranında oyna
**📡 Çevrimiçi** ile 2-4 telefon bağlanır, herkes kendi tam ekran bölgesinde oynar. Host'un ana menüde seçtiği mod, tur sayısı ve oyunlar kullanılır (Takım modu yoktur). Aynı tur aynı anda ve aynı tohumla başlar (ışık dizisi, kart dizilimi vb. herkeste aynı); herkes kendi oyununu oynar, sonuçlar host'ta karşılaştırılır.

**Kısa kodla bağlanma (Android uygulaması):** host "Oda kur" der, ekranda kısa bir **oda kodu** (örneğin `279`) çıkar. Arkadaşları aynı Wi-Fi'ya (ya da host'un telefon hotspot'una) bağlıyken "Odaya katıl" deyip kodu girer, hepsi bu. İnternet gerekmez.
- Nasıl çalışır: host telefon Wi-Fi'da küçük bir WebSocket sunucusu açar (`android/.../LanServerPlugin.java`). Kod, host IP adresinin son iki bölümüdür (192.168.**1.23** → 279). Misafir, kendi IP'sinin ilk iki bölümüyle host adresini bulup doğrudan bağlanır.
- Bluetooth web uygulamalarından ve WebView'den kullanılamadığı için yerel ağ bağlantısı kullanılır.
- Bağlanamazsan: ikisi de aynı Wi-Fi'da mı, router "istemci yalıtımı / AP isolation" açık mı (misafir ağlarında genelde açıktır)? Olmuyorsa birinin telefonunun hotspot'unu açıp diğerini ona bağla.
- "Gelişmiş bağlantı" seçeneği eski yöntemi (uzun kodu yapıştırarak WebRTC) sunar.

**Tarayıcıda (Pages):** tarayıcılar telefonda bağlantı kabul edemediği için kısa kod yoktur, uzun kod değiş tokuşu (WebRTC) kullanılır. Bağlantı için STUN vardır, TURN yoktur; bazı ağlarda kurulamayabilir.

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
