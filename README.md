# Dokun!

Aynı telefonu paylaşan 2-4 arkadaş için hızlı mini oyunlar. Ekran oyuncu sayısına göre bölünür, üstteki oyuncular karşıdan oynar. Çoklu dokunuş desteklenir. Tek dosya (`index.html`), harici dosya yok.

16 mini oyun var. Ana menüden iki şekilde oynanır:
- **🎲 Karışık Maç:** oyunlar rastgele sırayla ve tekrarsız oynanır (3 / 5 / 8 / 12 tur).
- **🎮 Oyun Seç:** listeden bir oyun seçilir ve yalnızca o oyun seçilen tur sayısı kadar oynanır.

Her turdan önce ekranın ortasında **tur kartı** çıkar ("TUR 2 / 8" + oyunun adı + kural özeti, iki yönden okunur), ardından 3-2-1 geri sayım başlar. Oyun sırasında her bölgenin alt/üst kenarında oyuncu adı, puan ve "oyun adı · tur" etiketi durur; 3-4 oyunculu dar bölgelerde bu etiket ayrı bir satıra alınır, böylece kesilmez ve oyun alanına binmez. Tur sayacı küçük bir hap olarak bölgenin ucundadır ve içeriğin üstüne taşmaz. Maç bitince kazanan bölge **altın renkte yanıp söner**, "👑 KAZANAN" yazar ve konfeti saçar. Diğer bölgeler sönükleşir ve sıralamasını gösterir. Sonuç her bölgede o oyuncuya doğru yazıldığı için iki yönden de okunur. Ardından "Tekrar" ve "Menü" düğmeleri çıkar.

- **Tepki:** yeşil olunca ilk dokunan kazanır. Bazen önce sarı bir sahte işaret yanar; sarıda ya da erken dokunan o turdan elenir. Tepki süresi gösterilir.
- **Seri Dokun:** 5 saniyede en çok dokunan kazanır. Rakiplere göre ilerleme çubuğu görünür.
- **Tam Ortada:** kayan topu yeşil banda en yakın durduran kazanır. Top herkes için aynı hızda ve zamanla hızlanır.
- **Hedef Avı:** çıkan hedefe dokun, 6 hedefe ilk ulaşan kazanır. Hedef her isabette küçülür, yakına dokunmak yeter. Herkes aynı hedef dizisini görür (yeni hedef öncekinden uzakta çıkar), ıska küçük bir "x" ve yumuşak sesle belli olur; üst üste 3 ıska kısa süre dondurur. İsabetler pentatonik bir merdiven çıkar; üstte 15 saniyelik süre çubuğu vardır.
- **Sayı Sırası:** 1'den 6'ya sırayla dokun, ilk bitiren kazanır.
- **Tek Farklı:** farklı emojiyi bul. 3 bulmacayı (ızgara büyür, emojiler birbirine yaklaşır) ilk çözen kazanır, yanlış dokunan 1,2 sn cezalı.
- **Renk Tuzağı:** yazının ne yazdığına değil rengine bak, aynı renkteki düğmeyi seç. Her rengin bir şekli de var (üçgen, kare, daire, baklava); Renk körü modunda desenler de eklenir. İlk 4 doğruya ulaşan kazanır, yanlışta kısa ceza.
- **Hızlı Matematik:** doğru sonucu seç. Sorular zorlaşır (küçük toplama, çıkarma, iki haneli sayılar, çarpma). İlk 4 doğruya ulaşan kazanır; yanlışta 1 sn bekletilirsin, art arda yanlışta ceza 1,5 ve 2 sn'ye çıkar.
- **Işık Oyunu:** yeşilde dokun (hızlı basarsan 2 puan), kırmızıda dokunma. Herkes aynı ışık dizisini görür, ışık giderek kısa yanar, yanlış dokunuş puan düşürür.
- **Renk Dizisi:** yanıp sönen 5 rengi aynı sırayla tekrarla, ilk bitiren kazanır. Yanlış basarsan dizi sana yeniden gösterilir.
- **Balon Patlat:** 10 saniyede en çok balonu patlat. Balonun tam üstüne basmak gerekmez, yakına dokunmak yeter. ⭐ Altın balon 3 puan, 💣 bomba 1 puan götürür; bomba yalnızca tam üstüne dokunulursa patlar, boş gökyüzüne dokunmak ceza değildir. Herkes aynı balon dizisini görür; balonlar ve bomba oranı süreyle hızlanır/artar. Üstte 10 saniyelik süre çubuğu (son 3 sn kırmızı nabız), süre bitince kalan balonlar patlar.
- **Tut ve Bırak:** basılı tut, tam 3 saniye dolunca bırak. Sayaç ilk 1 saniye görünür, sonra gizlenir.
- **Köstebek Avı:** deliklerden çıkan köstebeğe dokun, 10 saniyede en çok vuran kazanır. Herkes aynı delik programını görür; altın köstebek 3 puan, dikenli kirpiye dokunmak 1 puan götürür, boş deliğe dokunan kısa süre donar (her yere basarak kazanılmaz). Köstebekler zamanla daha kısa durur; üstte 10 saniyelik süre çubuğu vardır.
- **Ok Yönü:** okun yönünü seç, "TERSİNE" yazıyorsa karşı yönü (ilk iki soru hep "AYNI YÖN"). İlk 5 doğruya ulaşan kazanır, yanlışta kısa ceza.
- **Hafıza Eşleştir:** kartları çevirip 4 çifti ilk bulan kazanır. Yanlış eşleşmede kısa süre kilitlenirsin, rastgele basarak kazanılmaz.
- **Say Bakalım:** ekrandaki şekilleri say, doğru sayıyı seç; sayılar her soruda büyür. İlk 4 doğruya ulaşan kazanır, yanlışta kısa ceza.

Her bölge koyu, nötr bir zeminde; oyuncu rengi yalnızca çerçeve ve etiket olarak kullanılır. Bilgi kutuları karşıdaki oyuncu için ters kopya olarak da gösterilir.

Telefonu dikey tut. Tarayıcıda yatay çevrilirse "Telefonu dikey tut" uyarısı çıkar; oyun sırasında telefon yan çevrilirse oyun kendiliğinden duraklar (çevrimiçi maçlarda oyun durmaz: kenardaki sekme yalnızca "Maç sürüyor / Maça dön / Çık" menüsünü açar). Android uygulamasında sistem yazı boyutu büyütülmüş olsa da arayüz taşmasın diye WebView yazı yakınlaştırması %100'e sabitlenir; menü ekranları çentik/durum çubuğu boşluğunu (`safe-area`) dikkate alır. Tablet ve büyük ekranlarda (yatay dahil) bu uyarı çıkmaz; oyun yatayda da oynanır (Android 16 büyük ekranlarda yön kilidini yok sayar).

## Ekran boyutları, erişilebilirlik ve uyumluluk
- Denetlenen ekranlar: 320x568, 360x640, 360x800, 390x780, 412x915, 430x932, 600x960, 768x1024, 1024x768 (yatay tablet), 844x390 (yatay telefon: "Telefonu dikey tut" katmanı); ayrıca 1024x600, 960x540, 1280x800.
- Android WebView / Chrome tabanı **108** (`capacitor.config.json` → `minWebViewVersion`). Tek 108 sonrası özellik `text-wrap:balance` (114; yalnızca güzelleştirme). Ayrıntılı tablo ve erişilebilirlik notları: `DESIGN.md` bölüm 9.
- Erişilebilirlik: WCAG yazı kontrastı ölçülür, dokunma hedefleri >= 34 px, renk körü modu + her renkli öğede renk dışı ipucu, `prefers-reduced-motion` desteği, ayar anahtarlarında `role="switch"`.

## Modlar
Ana menüden oyuncu sayısı ve mod seçilir:
- **Klasik:** 3 / 5 / 8 / 12 tur, en çok puan alan kazanır.
- **Yarış:** hedef puana (3 / 5 / 7) ilk ulaşan kazanır.
- **Hayatta Kal:** her oyuncunun canı vardır (1 / 2 / 3). Turu kazanmayan can kaybeder, canı biten elenir, son kalan kazanır.
- **Takım:** 4 oyuncu, üstteki ikili alttakilere karşı, takım puanları toplanır (bir turda bir takım en fazla +1 alır).

**Beraberlik ve Altın Tur:** Maç berabere biterse (puanlar eşit; takımda iki takım eşit) bitiş ekranında **Altın Tur** düğmesi de çıkar (yalnızca yerel maçlarda, Hayatta Kal hariç). Eşit kalan oyuncular tek bir rastgele oyun daha oynar, diğerleri "Bekliyor" der; kazanan +1 alır. Yine berabere kalırsa Altın Tur yeniden denenebilir. **Hayatta Kal'da** üst üste iki turda kimse kazanamazsa hayatta kalan herkes bir can kaybeder (maç sonsuza uzamaz); herkes aynı turda elenirse sonuç beraberliktir ("Birlikte elendi").

**🎲 Karışık Maç** tüm oyunlardan, **🎮 Oyun Seç** ise listeden seçtiğin bir ya da birden fazla oyundan oynar.

## Duraklatma ve çıkış
Ekranın sol ve sağ kenarının ortasındaki ⏸ sekmesine **basılı tutarak** oyunu duraklatırsın (kısa dokunuş yetmez, hızlı dokunulan oyunlarda yanlışlıkla durmasın diye). Geri sayım, süreler ve balonlar gerçekten donar, devam edince kaldığı yerden sürer. Menüye dönmek için iki dokunuş gerekir. Telefon araması vb. ile uygulama arka plana giderse oyun kendiliğinden duraklar.
**Geri tuşu:** Oyun Seç, Ayarlar ve Çevrimiçi ekranlarında geri tuşu ana menüye döner. Oyun sırasında geri tuşuna (Android geri / tarayıcı geri / Esc) bir kez basmak yalnızca uyarı gösterir. 2,5 saniye içinde ikinci kez basılırsa oyundan çıkılır. Hızlı dokunulan oyunlarda (Seri Dokun gibi) yanlışlıkla çıkılmaz.

## Ses, müzik ve ayarlar
Ana menüdeki **⚙ Ayarlar**: ses efektleri, titreşim, müzik, menü müziği ve müzik sesi, ekranın açık kalması ve oyuncu adları. Tercihler hatırlanır (bozuk ya da eski kayıtlar güvenle varsayılana döner).

**Ses motoru.** Tüm sesler ve müzik Web Audio ile kodla üretilir (dosya yok); tek bir `AudioContext` ve tek çıkış zinciri kullanılır: efektler yumuşak bir üst kesmeden, müzik doğrudan geçer, ikisi de yumuşak bir sınırlayıcıdan çıkar (aynı anda çok ses olsa bile keskin kırpılma olmaz). Efektler katmanlı ve yumuşaktır (çan / marimba benzeri notalar, yuvarlak "tok" sesleri, gürültü patlamaları); sert kare / testere dalgaları yoktur, tekrarlarda perde ve doku hafifçe değişir. Eşzamanlı ses sayısı sınırlıdır, çok sık gelen aynı sesler seyreltilir, biten her düğüm temizlenir. Uygulama arka plana gidince, ekran kilitlenince ya da arama gibi bir kesinti olunca bağlam askıya alınır, geri dönünce (ve ilk dokunuşta) yeniden başlar; askıdayken ses kuyruğa alınmaz, dönüşte toplu patlama olmaz. Sesler ve konfeti kendi sabit üreteçlerini kullanır; çevrimiçi turlarda tohumlu `Math.random` yalnızca oyun içeriğine aittir. Android uygulamasında (WebView otomatik oynatmaya izin verir) menü müziği ilk dokunuşu beklemeden başlar; tarayıcıda ilk dokunuşta açılır.

**Müzik.** Oyuna göre değişir: menüde hafif ve neşeli (Do majör), hafıza/zeka oyunlarında sakin ve sıcak (Fa majör), arcade oyunlarında canlı (Sol majör), refleks oyunlarında gergin ama yumuşak (La minör). Her ruh hali 16 ölçülük bir formdur (iki akor dizisi, üç arpej deseni, araya serpilen ince notalar) ve kaldığı yerden devam eder, böylece döngü yormaz. Tur başında ruh hali kesintisiz geçer (eski ruh hali yumuşakça sönerken yenisi açılır), ruh halleri arasındaki ses seviyesi ölçülerek eşitlenmiştir. Duraklatınca müzik kısılıp boğuklaşır, menüye dönünce eski seviyesine gelir. Efekt sesleri müziğin anahtarına uyar.

**Titreşim.** `navigator.vibrate` tek kapıdan geçer: ayara, destek durumuna ve sayfanın görünürlüğüne bakılır; çok sık dokunuş titreşimleri seyreltilir, süren önemli bir titreşimi hafif bir dokunuş kesmez, desenler kısaltılır. Android için `VIBRATE` izni manifestte vardır; iPhone Safari titreşimi desteklemez.

## Çevrimiçi: her telefonda kendi ekranında oyna
**📡 Çevrimiçi** ile 2-4 telefon bağlanır, herkes kendi tam ekran bölgesinde oynar. Host'un ana menüde seçtiği mod, tur sayısı ve oyunlar kullanılır (Takım modu yoktur). Aynı tur aynı anda ve aynı tohumla başlar (ışık dizisi, kart dizilimi vb. herkeste aynı); herkes kendi oyununu oynar, sonuçlar host'ta karşılaştırılır.

**Çevrimiçi oyun kalitesi (aynı Wi-Fi):**
- **Eşzamanlı başlangıç:** telefonlar arası saat farkı sürekli ölçülür (ping/pong, en düşük gecikmeli örnek), her tur host'un belirlediği ortak ana göre başlar. 25-75 ms Wi-Fi gecikmesi altında oyunun telefonlar arası başlama farkı ~70 ms'den ~9 ms'ye indi (ölçüm: tek yönde 25-75 ms yapay gecikmeli 3 telefon, 3 tur).
- **Canlı rakip ilerlemesi:** ekranın üstünde rakiplerin puanı, ilerleme çubukları ve ✓ (bitirdi) görünür, yanında bağlantı kalitesi (📶 ms). Her turdan önceki tur kartı ekranın ortasında çıkar (üstteki rakip şeridine ve ekran kenarına binmez); alt etikette "oyun adı · tur" yazar.
- **Otomatik yeniden bağlanma (kısa kod):** bağlantısı kopan oyuncu 25 sn boyunca otomatik yeniden bağlanır, kaydı ve puanı korunur, diğerleri maçı sürdürür. 14 sn hiç cevap gelmeyen bağlantı kopmuş sayılır. Host odayı kapatınca misafirler hemen bilgilendirilir.
- **Bekleme odası:** oyuncu yuvaları, renkli avatarlar ve herkesin ping değeri görünür. Aynı adı kullanan oyuncular otomatik numaralanır ("Ali", "Ali 2"), boş ad "Oyuncu N" olur; oda 4 kişiliktir, 5. telefon "Oda dolu" uyarısı alır, maç başladıktan sonra gelen "Maç zaten başladı" der.
- **Bitirdin göstergesi:** turu önce bitiren oyuncuda "BİTİRDİN! 6,6 sn · 2 rakip bekleniyor" kutusu çıkar (kalan rakip sayısı azalır, sonuç gelince tur sonucuyla yer değiştirir); oyunun eski mesajı ("Sıra sende!" gibi) ekranda kalmaz.
- **Sürüm denetimi:** el sıkışmada telefonlar bir **sürüm damgası** karşılaştırır (protokol numarası + oyun sayısı + oyun kurallarının ve rastgele çekim sırasının koduna dayalı özet). Oyun kodu değişmiş eski/yeni sürümlü telefonlar aynı tohumla farklı içerik görmesin diye "Uygulama sürümleri uyuşmuyor, herkes Dokun!'u güncellemeli" uyarısıyla reddedilir; host da kimin neden katılamadığını görür. `//` yorumları ve boşluklar sayılmaz; APK ile web aynı kodu çalıştırdığı için damgaları aynıdır.
- **Sağlamlık:** bozuk, eksik, çok büyük (8 KB üstü) ya da türü yanlış mesajlar sessizce yok sayılır; sayı olmayan skor o oyuncuya "puan yok" yazar, turu kimse için bozamaz; el sıkışmayan bağlantı 8 sn sonra atılır ve oda yerini doldurmaz; kayıtlı oyuncunun tekrar "hello"su adını ya da yerini değiştirmez; yeniden bağlanma belirteci tarayıcının güvenli rastgele kaynağından üretilir. Maçtan bilerek çıkan oyuncu 25 sn beklenmez ("X ayrıldı"), oyuncu numaraları maç boyunca sabit kalır, herkes ayrılırsa host'ta maç biter. Maç başlarken bağlantısı koptuğu için "başla" mesajını kaçıran oyuncu geri bağlanınca devam eden maça katılır; önceki tur bitmeden yeni tur gelirse (arka planda kalmış telefon) eski tur bırakılır.

**Kısa kodla bağlanma (Android uygulaması):** host "Oda kur" der, ekranda kısa bir **oda kodu** (örneğin `279`) çıkar. Arkadaşları aynı Wi-Fi'ya (ya da host'un telefon hotspot'una) bağlıyken "Odaya katıl" deyip kodu girer, hepsi bu. İnternet gerekmez.
- Nasıl çalışır: host telefon Wi-Fi'da küçük bir WebSocket sunucusu açar (`android/.../LanServerPlugin.java`). Kod, host IP adresinin son iki bölümüdür (192.168.**1.23** → 279). Misafir, kendi IP'sinin ilk iki bölümüyle host adresini bulup doğrudan bağlanır.
- Bluetooth web uygulamalarından ve WebView'den kullanılamadığı için yerel ağ bağlantısı kullanılır.
- IP seçimi: yalnızca özel (site-local) IPv4 adresleri; hotspot sahibi (ap*/swlan*/`.1` ile biten wlan) > Wi-Fi (wlan*) > Ethernet > diğerleri. Mobil veri (rmnet*, ccmni*...), VPN (tun*, wg*...), noktadan noktaya arayüzler ve Wi-Fi Direct elenir; yalnızca mobil veri varsa "Wi-Fi ya da hotspot bağlantısı yok" der. IPv6 kullanılmaz.
- Sunucu (`LanServerPlugin.java`): tek mesaj en fazla 16 KB (aşan bağlantı kapatılır), en fazla 12 eş zamanlı bağlantı, `TCP_NODELAY`, durdur/başlat tek sıralı iş parçacığında yapılır (aynı port hemen yeniden açılır; port başka uygulamada doluysa Türkçe hata verilir). Capacitor eklenti çağrılarını tek iş parçacığında çalıştırdığı ve oradan kaçan her istisna uygulamayı çökerttiği için hiçbir eklenti yöntemi istisna fırlatmaz.
- Bağlanamazsan: ikisi de aynı Wi-Fi'da mı, router "istemci yalıtımı / AP isolation" açık mı (misafir ağlarında genelde açıktır)? Kod yalnızca IP'nin son iki bölümünü taşıdığı için iki telefonun IP'sinin ilk iki bölümü aynı olmalıdır (geniş okul / iş yeri ağlarında telefonlar farklı alt ağlarda olabilir; VPN açıksa yerel ağ adresine ulaşılamayabilir). Olmuyorsa birinin telefonunun hotspot'unu açıp diğerini ona bağla ya da "Gelişmiş bağlantı"yı dene.
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

**Android ayrıntıları:**
- Dikey kilit (`screenOrientation=portrait`); yazı boyutu / yön / dil değişince etkinlik yeniden oluşturulmaz (`configChanges`: `fontScale`, `layoutDirection` dahil), yarım kalan maç kaybolmaz. Android 16'da (targetSdk 36) büyük ekranlarda (sw >= 600dp) sistem dikey kilidi ve yeniden boyutlandırma kısıtlarını yok sayar; oyunlar bu kuraldan muaftır, bu yüzden uygulama manifestte `android:appCategory="game"` ile oyun olarak işaretlidir ve tabletlerde de dikey tasarım korunur.
- **Eski WebView:** `capacitor.config.json` içinde `android.minWebViewVersion = 108` (arayüz `dvh` birimini kullanır) ve `server.errorPath = webview-guncelle.html`. WebView 108'den eskiyse oyun bozuk görünmek yerine Türkçe bir sayfa ("Android Sistem WebView'ünü güncelle", Play Store bağlantısı) açılır. Sayfa yalnızca APK'ya girer (`npm run www` kopyalar). Not: Capacitor, Android 7/7.1'de (API < 26) WebView sürümünü yalnızca `com.android.chrome` / `com.android.webview` paketlerinden okur; bu cihazlarda Chrome devre dışıysa hata sayfası yanlışlıkla görünebilir.
- **Sistem çubukları:** pencere / durum / gezinme çubuğu zemini oyunun koyu rengidir (`#1D1838`), `SystemBars` eklentisi `style: DARK` ile simgeleri açık renk yapar (açık temada bile okunur). `viewport-fit=cover` + `env(safe-area-inset-*)`: WebView 140 ve üstünde sistem çubukları altına tam ekran çizer, daha eskisinde Capacitor çubuk kadar boşluk bırakır.
- **Deneme düzeneği:** `android/lan-test/run.sh` gerçek `LanServerPlugin.java` ve Java-WebSocket'i (Capacitor sınıfları taslak) JVM'de sınar (port yeniden kullanımı, mesaj sınırı, istisna güvenliği, Türkçe hata); IP seçim mantığı için `android/app/src/test/.../LanServerPluginTest.java` (`cd android && ./gradlew testDebugUnitTest`). Android SDK olmadan derlenemeyen Java değişiklikleri bu yolla denenir.
- **Simgeler:** `store/build-icons.js` (Playwright) simgeleri üretir; `ONLY=splash,adaptive node store/build-icons.js` yalnızca açılış ekranını / uyarlanabilir simgeyi yeniden çizer. Android 12+ açılış simgesi sistem tarafından dairesel maskeyle (tuvalin 2/3'ü) kırpılır: emblem bu yüzden 124 birimlik görünüm kutusunda küçültüldü; uyarlanabilir simgenin ön planı da daire maskesi köşeleri kırpmasın diye küçültüldü.

## Yayın
`.github/workflows/pages.yml` varsayılan daldaki `index.html` dosyasını GitHub Pages'e yayınlar.
Bir kez: depo → Settings → Pages → Source: **GitHub Actions**.
