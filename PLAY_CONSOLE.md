# Dokun! — Google Play Console'da teste sokma rehberi

Bu depo Play Console'a hazır paket üretir: her `main` push'unda GitHub Actions **imzalı release APK** (telefona kurmak için) ve **AAB** (Play'e yüklemek için) derler. Sürüm numarası otomatik artar (`versionCode` = çalışma numarası, `versionName` = `1.0.<numara>`).

## 1) Bir kez: imza anahtarı parolasını GitHub'a ekle
Yükleme (upload) anahtarı şifreli olarak `android/signing/dokun-upload.jks.enc` dosyasındadır; çözmek için tek bir parola gerekir (sohbette verilen 32 karakterlik parola).
1. GitHub'da depo → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**
2. Name: `DOKUN_KEYSTORE_PASSWORD`  ·  Secret: (verilen parola)
3. **Actions** → "Android APK ve AAB" → **Run workflow** (ya da yeni bir commit push'la).
4. Çalışma bitince **Artifacts → Dokun-N** zip'inin içinde `Dokun-N.apk` (imzalı) ve `Dokun-N.aab` (Play'e yüklenecek) bulunur. Çalışma özetinde yükleme sertifikasının SHA-256 parmak izi yazar.

> Parola tanımlı değilse workflow yine çalışır ama yalnızca debug APK üretir (Play'e yüklenemez, ayrıca her seferinde farklı imzalandığı için üst üste kurulamaz). İmzalı APK'ya geçince telefondaki eski debug sürümü bir kez kaldırıp yenisini kur.
> Parolayı kaybedersen: Play Console → Kurulum → Uygulama bütünlüğü → "Yükleme anahtarını sıfırla" ile yeni anahtar tanımlanabilir.

## 2) Play Console'da uygulamayı oluştur
1. https://play.google.com/console → **Uygulama oluştur** → Ad: `Dokun! Parti Mini Oyunları`, Varsayılan dil: Türkçe, **Oyun**, **Ücretsiz**. Beyanları onayla.
2. **Kurulum → Uygulama bütünlüğü**: "Play Uygulama İmzalama" (Google yönetir) seçili kalsın. İlk yüklemede AAB'nin imzası "yükleme anahtarı" olarak kaydedilir.

## 3) Zorunlu "Uygulama içeriği" formları
Hazır cevaplar: `store/data-safety.md` (Veri güvenliği, reklam, erişim, içerik derecelendirme, hedef kitle).
- Gizlilik politikası URL'si: `https://erenmaral.github.io/Dokun/privacy.html` (GitHub Pages ile yayınlanır)
- Veri güvenliği: veri toplanmıyor / paylaşılmıyor
- İçerik derecelendirme: tüm sorulara "Hayır" → herkes için uygun
- Hedef kitle: 13 yaş ve üzeri (çocuklara yönelik değil)

## 4) Mağaza girişi
- Metinler: `store/listing-tr.md` (ad, kısa ve tam açıklama)
- Görseller: `store/out/` altında hazır — `icon-512.png` (uygulama simgesi), `feature-graphic-1024x500.png` (öne çıkan görsel), `screenshots/` (telefon ekran görüntüleri; en az 2, en çok 8)
- Kategori: Oyunlar → Parti. İletişim e-postası: kendi e-postan (herkese açık gösterilir).

## 5) Test sürümü
**İç test (en hızlısı, inceleme beklemez):** Test → **Dahili test** → Yeni sürüm oluştur → `Dokun-N.aab` dosyasını yükle → sürüm notu → Kaydet → İncele → Yayınla. "Test kullanıcıları" sekmesinden bir e-posta listesi oluştur (Gmail adresleri), çıkan **katılım bağlantısını** testçilere gönder; bağlantıyı açıp Play'den yüklerler. Dahili testte en çok 100 kişi olur ve yayın dakikalar içinde hazır olur.

**Kapalı test + üretim:** Kişisel geliştirici hesapları (Kasım 2023 sonrası açılanlar) üretime çıkmadan önce **en az 12 testçi 14 gün boyunca kesintisiz** kapalı testte olmak zorundadır. Test → **Kapalı test** → sürüm oluştur (AAB yükle) → testçi e-posta listesi ekle → 12+ kişi katılım bağlantısını açıp "Katıl" desin → 14 gün sonra **Üretim erişimi için başvur** düğmesi açılır. (Kurum hesaplarında bu şart yoktur.)

## 6) Güncelleme
Kodu `main`'e push'la → yeni AAB üretilir → aynı testte "Yeni sürüm oluştur" ile yükle. `versionCode` zaten otomatik artar. Yükleme anahtarı hep aynı olduğundan imza uyuşmazlığı olmaz.

## 7) Play'in "Yayın öncesi rapor" uyarıları
Play, yüklenen sürümü gerçek cihazlarda otomatik dener. Beklenen sonuç: çökme yok. İzinler: yalnızca ağ ve titreşim. `usesCleartextTraffic` yerel ağ oyunu (ws://) için gereklidir; veri bize ulaşmaz.

**Eski WebView:** `minWebViewVersion` 108'dir; daha eski bir "Android System WebView" olan cihazlarda oyun açılmak yerine Türkçe bir güncelleme sayfası (`webview-guncelle.html`) görünür. Play'in cihaz testlerinde bu sayfa görünürse test cihazının WebView'ünü güncellemek gerekir.

**Sürüm numarası ve çevrimiçi oyun:** çevrimiçi odada telefonlar el sıkışmada bir sürüm damgası karşılaştırır; oyun kuralı / rastgele çekim sırası değişmiş iki sürüm birbirine bağlanamaz ("Uygulama sürümleri uyuşmuyor"). Kademeli yayında (staged rollout) bu, yeni sürüme geçmiş ve geçmemiş arkadaşların aynı odaya girememesi demektir; yalnızca yorum / boşluk değişen yayınlarda damga değişmez.
