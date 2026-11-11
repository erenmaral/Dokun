# Play Console — "Veri güvenliği" (Data safety) formu için hazır cevaplar

Dokun! kişisel veri toplamaz/paylaşmaz. Formu şöyle doldur:

1. **Uygulamanız gerekli kullanıcı verisi türlerinden herhangi birini topluyor veya paylaşıyor mu?** → **Hayır**
   (Oyuncu adları ve ayarlar yalnızca cihazda `localStorage`'da durur; yakındaki telefonlara yerel ağda iletilen adlar/puanlar bizim sunucumuza ulaşmaz. "Toplama" Google'ın tanımına göre verinin cihazdan dışarı, geliştiriciye veya üçüncü tarafa aktarılmasıdır.)
2. **Tüm kullanıcı verileri aktarım sırasında şifreleniyor mu?** → Veri toplanmadığı için soru gösterilmez. (Yerel ağ oyunu `ws://` düz bağlantı kullanır ama veri bize ulaşmaz.)
3. **Kullanıcılar veri silme isteyebilir mi?** → Soru gösterilmez (veri toplanmıyor).
4. **Gizlilik politikası URL'si:** https://erenmaral.github.io/Dokun/privacy.html

## Diğer "Uygulama içeriği" beyanları
- **Reklamlar:** Uygulama reklam içermiyor → Hayır
- **Reklam kimliği (Advertising ID) kullanımı:** Uygulama reklam kimliğini kullanmıyor → Hayır (Android 13+ için Play Console bunu ayrıca sorar)
- **Uygulama erişimi:** Tüm özellikler giriş yapmadan kullanılabilir → "Tüm işlevler özel erişim gerektirmeden kullanılabilir"
- **İçerik derecelendirme (IARC) anketi:** Kategori "Oyun". Şiddet, korku, cinsel içerik, kumar, uyuşturucu, küfür, kullanıcı etkileşimi/paylaşımı (yalnızca yerel ağda aynı odadaki oyuncular, sohbet yok), konum paylaşımı, dijital satın alma → hepsi **Hayır**. Beklenen sonuç: PEGI 3 / Herkes.
- **Hedef kitle:** "13 yaş ve üzeri" gruplarını seç (çocuklara yönelik değil → Aile Politikası ek koşulları gerekmez). Çocuklara yönelik seçilirse reklamsız/veri toplamayan yapı yine uyumludur ama ek form adımları çıkar.
- **Haber uygulaması / COVID-19 / Devlet uygulaması / Finansal özellikler / Sağlık:** hepsi Hayır
- **Kategori:** Oyunlar → Parti (veya Bulmaca/Aksiyon), Etiketler: çok oyunculu, parti, mini oyunlar
- **İzinler:** INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE (yerel ağ adresini bulmak, yakın telefonlarla bağlanmak), VIBRATE (titreşim). "Hassas izin" yok.
