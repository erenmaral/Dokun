# Dokun! tasarim dili: "Cikartma / oyuncak kutusu" (Yon A)

Her sey bir cikartma ya da oyuncak gibi: **duz doygun renkler, kalin murekkep dis cizgisi, sert (bulaniksiz) kaydirilmis golgeler, nokta dokusu, tombul yazi tipi, ziplayan (squash & stretch) hareket.** Tum degisiklikler `index.html` icinde (CSS + satir ici SVG + kucuk JS yamalari); oyun mantigi, zamanlayicilar, ag protokolu ve yerlesim duzeltmeleri korunmustur.

## 1. Renkler (`:root`)
| belirtec | deger | kullanim |
|---|---|---|
| `--ink` | `#1d1838` | TUM dis cizgiler, sert golgeler, panel metni, board zemini |
| `--paper` | `#fff4dc` | kagit krem: paneller, fisler, kartlar, oyun ici yazi |
| `--night` / `--night2` | `#2e2766` / `#3a3279` | bolge zemini (bekleme / oyun anlari), menu zemini |
| `--red` `--green` `--blue` `--yellow` | `#ff5a5f` `#2fd68c` `#47a9f5` `#ffd23f` | oyuncu 1-4 renkleri (cerceve, isim fisi, hedef, ilerleme) |
| `--purple` `--orange` | `#a56bff` `#ff9442` | yardimci renkler (hafiza karti, ritim, ayarlar karosu) |
| `--*-d` | koyu tonlar | golge/yuzey tonu (oyuncu topu); `color-mix` KULLANILMIYOR (eski WebView guvenligi) |

Kural: bir yuzeyde en fazla bir doygun renk + murekkep + kagit. Oyuncu rengi yalniz cerceve, isim fisi ve oyun nesnelerinde; zemin hep nötr (renk karisikligi olmaz, Renk Tuzagi okunur kalir).

## 2. Yazi tipleri (dis kaynak yok, base64 gomulu, `font-display:block`)
| aile | kullanim | dosya | bayt (woff2) |
|---|---|---|---|
| **DynaPuff** (degisken, 400-700) | logo, basliklar, dugmeler, fisler, rakamlar, oyun ici buyuk yazi | latin + latin-ext | 37.572 + 27.080 |
| **Baloo 2** (600) | aciklama / ipucu / uzun metin | latin + latin-ext | 19.496 + 15.984 |
| **Toplam** | | 4 dosya | **100.132 bayt (<= 130 KB)** |

Latin altkumesi ı ö ü ç â î û x - ... noktalamasini, latin-ext altkumesi **ğ Ğ ş Ş İ** harflerini tasir (`unicode-range` ile ayri `@font-face`; fontTools ile dogrulandi: Turkce harflerin tamami iki ailede de var). Kaynak: `@fontsource-variable/dynapuff` ve `@fontsource/baloo-2` (SIL OFL). `font-synthesis:none` (sahte kalin yok). Fontlarin gercekten calistigi ekran goruntusunde dogrulandi (sistem fontuna dusme yok; "ğ ş İ" DynaPuff ile cizilir).

Not: Lilita One ve Fredoka denendi ama Turkce ğ ş İ glifleri YOK; bu yuzden secilmedi.

## 3. Olcekler
- **Kose yaricapi:** `--r-s 10px` (hucre, giris, kucuk dugme) / `--r-m 16px` (dugme, kart) / `--r-l 24px` (panel, buyuk dugme, tur karti) / `--r-pill 999px` (fis, anahtar). Bolge cercevesi 22px.
- **Cizgi:** `--ln-s 2px` (kucuk fis, sayac) / `--ln 3px` (varsayilan) / `--ln-b 4px` (panel, buyuk dugme, tur karti). Bolge cercevesi 5px oyuncu rengi + 3px murekkep ic cizgi.
- **Bosluk:** 4 / 8 / 12 / 16 / 24 (`--s1..--s5`), eleman ici bosluklar 4-8 tabanli.
- **Golge (yalniz kaydirma, bulaniklik YOK):** `--sh-xs 0 2px 0` fis, `--sh-s 0 3px 0` kucuk, `--sh-m 0 4px 0` varsayilan, `--sh-l 0 6px 0` panel/buyuk. Basili: `translateY(3px)` + `0 1px 0` ("cikartma basilir").
- **Murekkep kontur metin:** `--ol2/--ol3/--ol5` (12 yonlu `text-shadow`; `paint-order`'a bagimli degil). 2px kucuk, 3px govde, 5px sayac/logo.
- **Hareket egrileri:** `--pop cubic-bezier(.34,1.56,.64,1)` (asma, ziplama), `--out .22,.8,.3,1` (hizli oturma), `--soft .65,0,.35,1` (dongusel nefes).

## 4. Bilesen kurallari
- **Bolge:** yuvarlak kose, 5px oyuncu rengi `border` + ic murekkep cizgi, nokta dokusu (`::before`, statik), durumlar yalniz zemin rengi degisimi: `go` (acik gece) / `sig` (yesil) / `warn` (sari) / `out` (soluk mor) / `win` (altin + gunisigi) / `champ` (altin + yavasca donen gunisigi) / `lose`. Geri bildirim `::after` kaplamasinin `opacity` animasyonudur (kutu golgesi animasyonu YOK).
- **Fisler:** isim fisi = oyuncu rengi, puan fisi = kagit; 2px cizgi, 2px golge. Dar bolgede oyun adi ustte ayri satir.
- **Dugme:** kagit zemin, murekkep cizgi, sert golge; `.sel` sari; `.go` yesil; `.alt` sari buyuk; `.sm` kucuk. Anahtar (toggle) cikartma dugmesi: yesil/gri iz + kagit top.
- **Paneller:** kagit krem + 4px cizgi + 6px golge; basliklar murekkep hap etiketi.
- **Oyun Sec kartlari:** kategori rengi (refleks kirmizi, hiz turuncu, zeka mavi, dikkat yesil) simge rozeti; secili kart hafif egilir + yesil "onay" cikartmasi.
- **Bilgi kutusu / tur karti:** kagit karti, 4px cizgi; ust satir sari "TUR n / toplam" hapi, oyun simgesi rozeti + ad. Iki yonden okunur (180 derece kopya korunur).
- **Geri sayim:** `cdp` squash & stretch (1.8x0.5 -> 0.82x1.22 -> 1.1x0.92 -> 1) + 5px kontur.
- **Bitis:** kazanan: donen gunisigi + kagit madalyon icinde tac + "KAZANAN"; digerleri: oyuncu rengi sira rozeti. Beraberlikte uc yildiz.
- **Duraklatma / yeniden baglanma / toast:** duz yari saydam murekkep zemin (backdrop blur YOK), kagit cikartma kartlari.
- **Soru-cevap oyunlari (Renk Tuzagi / Hizli Matematik / Ok Yonu / Say Bakalim):** ortak `.quiz` kabi bolgenin govdesini doldurur (container query birimleri: tablette buyur, 320x568'de secenek >= 34 px kalir); ustte sure cubugu (`.pz-tm`) + sayac, ortada soru alani, altta SABIT yukseklikli 2x2 secenek (sorular arasinda kayma yok). Soru dizisi herkes icin ortak, yanlista ayni soruda kalinir (ceza 1 / 1,5 / 2 sn, denenen secenek pasif), dogru cevabin ardindan ~250 ms girdi yok sayilir; dogru secenekte yesil, yanlista kirmizi rozet.
- **Rakip seridi (ag):** 9px murekkep iz + oyuncu rengi dolgu, baglanti kalitesi kagit hap + renkli nokta (yesil/sari/kirmizi).

## 5. Simgeler ve oyun varliklari (emoji YOK)
Tek bir satir ici SVG `<symbol>` sprite'i (`<use href="#...">`; ~29 KB): 3px murekkep cizgi, duz dolgu, kucuk kagit parlama noktasi.
- Arayuz: `i-back i-play i-pause i-home i-redo i-check i-x i-phone` (tek renk, `currentColor`); cikartma: `i-heart i-skull i-star i-stars i-crown i-bolt i-wifi i-gear i-gamepad i-dice i-link i-flag i-team i-lock`.
- 16 oyun simgesi `g-*` (tur karti, Oyun Sec kartlari, menu seridi).
- **Ritim:** hedef daire oyuncu renginde (kalin murekkep cizgi + sert golge), daralan halka kagit rengi + cift murekkep cizgi (`.rit-ring`, yalniz transform/opacity animasyonu), vurus sonucu yazisi (`.rit-fb`), 8 vurus noktasi (`.rit-dots`). Halka animasyon sinifi `run` (genel `.go` dugme kuralina carpmamak icin `go` KULLANILMAZ).
- **Hedef Avi:** `a-target` (oyuncu renkli halkalar, sari goz, parlama), sicrayarak belirir, hafif sallanir, isabette patlama yildizi.
- **Hafiza Eslestir:** 8 cikartma yuz (`f-bear f-cat f-frog f-bunny f-panda f-pig f-octo f-pizza`), mor noktali kart arkasi + "?" , cevirme `scaleX` animasyonu, eslesince ziplama; genis bolgede 4x2 yerlesim (kartlar buyuk), dar bolgede 2x4.
- **Tek Farkli:** ayni yuzler + `o-smile/wink/smirk` (zorluk artar). **Ok Yonu:** `a-arrow` (kagit kadran icinde buyuk ok; kural rozeti: yesil "AYNI YON" + `q-same` (es oklar), kirmizi "TERSINE!" + `q-rev` (zit oklar); secenek oklari oyuncu renginde). **Say Bakalim:** `a-bee a-apple a-fish i-star a-balloon-s`, nesneler sabit boyutlu tepsideki 4x3 kutulara (kutu basina en fazla bir, kayma + egim) yerlesir. **Renk Tuzagi:** her rengin sekli (Renk Dizisi ile ayni: kirmizi ucgen, yesil daire, mavi kare, sari baklava) secenekte hep gorunur, kelime kutusunun kosesinde murekkep renginin simgesi durur; Renk koru modunda ayrica desen (cizgi / nokta / izgara / yatay cizgi) ve renkli simge cipi eklenir. **Kostebek:** `a-mole` + toprak delik, sicrayarak cikar. **Seri Dokun:** oyuncu renkli sayac rozeti, her dokunusta siçrar. **Isik Oyunu:** kalin cizgili lamba + halka dalgasi. **Renk Dizisi:** parlak ped, yanarken kagit kaplama + buyume. **Tam Ortada:** kagit iz + yesil hedef bolgesi + oyuncu rengi top.

## 6. Performans kurallari (dusuk donanim, Android WebView)
- Hic `filter`, `backdrop-filter`, `blur()`, `color-mix`, animasyonlu `box-shadow` / `background-position` YOK. Tum surekli animasyonlar `transform` / `opacity` (gecisler: renk, `transform`).
- Nokta dokusu statik gradyan; donen gunisigi yalniz kazanan bolgede.
- Sert golge = tek katman; basili durumda tek `transform`.
- `hero` dugmesinin kendisi hareket etmez (Playwright ve parmak icin sabit hedef); parlama seridi ve zar alt ogelerde oynar.
- Balon, hedef, kostebek, kart yuzleri `<use>` ile paylasilir (DOM cok hafif).

## 7. Kalan oyunlar icin yapilacaklar (varsa)
Tum 16 oyun yeni kabuga ve simgelere gecirildi. Isteyen icin ince ayar fikirleri:
1. **Sayi Sirasi / Tek Farkli / Hizli Matematik:** hucrelere kucuk "dogru" cikartmasi (kagit yildiz) animasyonu.
2. **Tut ve Birak / Tepki:** rakam ve sonuc yazilarina simge (kronometre) eklemek.
3. ~~**Renk Tuzagi:** secenek hucrelerine renk-kor modu icin desen (nokta/cizgi) ayrimi.~~ Yapildi: sekil + cb modunda desen (bkz. bolum 5).
4. Menude kucuk "cikartma" dekorlari (yildiz, sprey nokta) eklenebilir; simdilik temiz birakildi (floating emoji deseni kaldirildi).
5. Gece/gunduz temasi gerekirse yalniz `--night`, `--night2`, `--paper` degistirilir.

## 8. Kural ozeti (yeni bir oge eklerken)
1. Doygun duz renk + 3px `--ink` cizgi + `--sh-m` golge.
2. Metin: DynaPuff (baslik/dugme/rakam) ya da Baloo 2 (aciklama); oyun ici buyuk metin `--ol3` konturlu kagit.
3. Simge: sprite'a yeni `<symbol>` ekle, emoji kullanma.
4. Hareket: yalniz `transform/opacity`, `--pop` ile asma, basma `translateY(3px)`.
5. Dokunma hedefi >= 34px; ust bolgeler 180 derece donuk oldugundan hicbir sey tek yone gore konumlandirilmaz.

## 9. Ses dili (oyuncak kutusu sesi)
Gorsel dilin sesteki karsiligi: **yuvarlak, tatli, yumusak.** Hepsi Web Audio ile kodla uretilir (dosya yok), tek merkezi motordan (`audio`) gecer.
- **Palet:** cam / marimba benzeri "pluck" notalari (yumusak testere + parlaktan yumusaga suzulen alcak geciren), yuvarlak "tok" sesleri (perde dusen sinus + yumusak govde), kisa gurultu patlamalari (balon "pop", kart cevirme, "vuup"). Sert kare / testere dalgasi YOK; eski cagrilar bile yumusak dalgaya cevrilir. Ust kesme ~7,6 kHz.
- **Perde:** efektler Do major pentatonik (C D E G A) merdivenindedir ve muzigin ruh haline gore anahtar degistirir (menu Do, sakin Fa, canli Sol, gergin La minor = Do pentatonik), boylece efekt muzige karsi durmaz.
- **Seviye hiyerarsisi (tepe):** dokunus tiki ~ -21 dBFS, kucuk olaylar (iska, cevirme, uyari) ~ -12..-14, ana geri bildirim (dogru / yanlis / pop / nota) ~ -9..-6, tur / mac sesleri (go, kazanma, fanfar, bomba) ~ -5..-2. Muzik (Orta) ~ -28 dBFS rms; tum seviyeler `OfflineAudioContext` ile olculur (`audio.render`, `music.render`, `music.renderMix`).
- **Tekrarda varyasyon:** sik calan sesler her seferinde kucuk perde sapmasi ve donen varyantlar kullanir (kendi sabit uretecimizle; `Math.random` KULLANILMAZ).
- **Yeni ses eklerken:** `RC` tablosuna tarif yaz (`[oncelik, en kisa aralik ms, kaynak sayisi, uzunluk sn, cizim]`), oyundan `audio.play('ad', arg)` ile cagir; ham `createOscillator` / `navigator.vibrate` yazma (`audio.note` / `haptic.play` kullan). Titresim her zaman `haptic.play(desen, oncelik)`.

## 10. Buyuk ekran, erisilebilirlik ve uyumluluk (QA1 notlari)
**Ekran boyutlari (denetlenen 10 boyut):** 320x568, 360x640, 360x800, 390x780, 412x915, 430x932, 600x960, 768x1024, 1024x768 (yatay tablet), 844x390 (yatay telefon). Ek olarak 1024x600 / 960x540 (7 inc yatay tablet) ve 1280x800 taranir.
- **Yon:** "Telefonu dikey tut" katmani (`#rotate`) yalnizca dar telefon yataynda cikar (`orientation:landscape` + `max-height:520px` + `min-width:560px`). Android 16 buyuk ekranlarda yon kilidini yok sayar: tablet yatayinda oyun oynanir; kisa yatay tabletlerde (<= 660 px yukseklik) vmin tabanli boyutlar `dvh` ile sinirlanir.
- **Bolge `narrow` esigi 300 px** (600 px genisliginde 4 oyuncuda etiket satirlari tek satira sigmiyordu). Dar bolgede oyun adi ustte ayri satirdir.
- **Duraklat sekmeleri** (ekran kenarlarinda dikey ortada) icerigi kapatmasin diye kenara degen bolgeler `data-tab="l|r|l r"` ile (bolgenin kendi yonunde) `--tl` / `--tr` payi alir; sure cubugu / sayac satirlari bu payi padding olarak kullanir.
- **Genis ekran (>= 600 x 600):** baslik fisleri, tur karti, duraklat ve bitis dugmeleri bir kademe buyur; Oyun Sec 3 (>= 700 px) / 4 (>= 1000 px) sutun; dikey tablette (>= 900 px yukseklik) ana menu / ayarlar / cevrimici `zoom:1.2`; yatay genis ekranda ana menunun iki yanina magaza grafigindeki cikartma karolari (balon, hedef, domuz, kostebek) konur.
- **Kategori etiketleri** (Oyun Sec): dar ekranda 3 + 2 izgara (hicbiri kesilmez), >= 440 px tek satir.
- Toast ve bilgi kutusu `width:max-content` (ortalama icin kullanilan `left:50%` kutuyu %50 genislige sikistirip yaziyi erken kiriyordu).

**Erisilebilirlik**
- **Kontrast (WCAG 2.1):** yazi / zemin orani olculur (`qa` ciktisi: menu, ayarlar, oyun ici etiketler, tur karti, cipler, bitis, rehber). Kagit zemindeki ikincil etiketler (`.plbl`, `.gcard .tg`) `opacity:.72`, kontrast >= 4,5. Oyun ici buyuk yazilar murekkep konturludur; kontrast kontur ile (>= 15) saglanir. Devre disi (disabled) dugmeler WCAG kapsami disindadir.
- **Dokunma hedefi >= 34 px:** menu, ayarlar, oyun secme, cevrimici ve oyun ici ogeler (qaMisc bolum 1 + oyun ici tarama).
- **Renk koru:** renkle ayrilan her ogenin renk-disi ipucu vardir (sekil: Renk Dizisi / Renk Tuzagi, simge + yazi: dogru / yanlis rozeti, lamba, kategori noktasi + yazi, takim A / B rozeti, ag kalitesi sayisi); Renk koru modu desen ekler. Machado (2009) benzetimi (protan / deutan / tritan) ve CIEDE2000 olcumu ile dogrulandi.
- **Hareketi azalt:** `prefers-reduced-motion: reduce` altinda surekli (sonsuz) animasyon kalmaz: menu susleri, Tepki simgeleri, son saniye uyari cubuklari, bomba / altin balon halesi, Tut ve Birak halkasi, Sayi Sirasi ipucu nabzi kapanir; geri bildirim animasyonlari kisa ve tek seferliktir.
- **Yazi boyutu:** Android'de `setTextZoom(100)`; tarayicida `text-size-adjust:100%`. 1,3x yazi benzetiminde ana ekranlar tasmaz.
- **Ekran okuyucu:** ayar anahtarlari `role="switch"` + `aria-checked` + baslik; secim dugmeleri (oyuncu sayisi, mod, tur, kategori, ses, oyun kartlari) `aria-pressed`.

**Uyumluluk tabani: Android WebView / Chrome 108** (`capacitor.config.json` `minWebViewVersion`). Betik ES2018 sozdizimi (optional chaining, `??`, `at()`, `replaceAll`, `structuredClone` YOK).
| ozellik | en dusuk Chrome | durum |
|---|---|---|
| container-type / @container / cq birimleri | 105 | `@supports (height:1cqh)` yedegi (dvh tabanli degerler) |
| dvh birimi | 108 | taban surum; yedek gerekmez |
| `text-wrap:balance` | 114 | yalniz guzellestirme; desteklenmezse normal satir kirma |
| inset, aspect-ratio, overflow:clip, clip-path | 87, 88, 90, 55 | taban altinda |
| min() / max() / clamp(), env() | 79, 69 | taban altinda |
| `gap` (flex), position:sticky, prefers-reduced-motion | 84, 56, 74 | taban altinda |
| repeating-conic-gradient, `font-synthesis` | 69, 97 | taban altinda |
| `<use href>` (xlink:href'siz), CSS degiskenleri `<use>` golge agacinda | 50, 49 | taban altinda |
| woff2 + degisken yazi tipi (`font-weight:400 700`), unicode-range | 36, 62, 36 | taban altinda |
| PointerEvent + setPointerCapture, Web Audio, OfflineAudioContext | 55, 35 | taban altinda |
| navigator.wakeLock, navigator.share, Clipboard | 84, 89, 66 | var-mi denetimi + yedek |
| CompressionStream('deflate-raw') / DecompressionStream / Blob.stream | 103, 103, 76 | kodlamada var-mi denetimi + sikistirmasiz yedek (etiket `DK0`) |
| MediaQueryList.addEventListener('change') | 79 | taban altinda |
