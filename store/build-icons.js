// Dokun! simge ve acilis ekrani uretici: SVG -> PNG (Playwright/Chromium). Calistir: node store/build-icons.js
// Cikti: android/app/src/main/res/{mipmap-*,drawable*} + store/out/icon-512.png
// Yalniz bir kismini uretmek icin: ONLY=splash,adaptive node store/build-icons.js   (parcalar: legacy, adaptive, store, splash)
const { chromium } = require('playwright');
const fs = require('fs'), path = require('path');
const ROOT = path.resolve(__dirname, '..');
const RES = path.join(ROOT, 'android/app/src/main/res');
const OUT = path.join(__dirname, 'out');
fs.mkdirSync(OUT, { recursive: true });

const INK = '#0a0f26', BG1 = '#1b2552', BG2 = '#0d1230';
// Acilis ekrani zemini: uygulamanin ilk karesiyle (html/body zemini, index.html --ink) ayni duz renk; renk sicramasi olmasin
const SPLASH_BG = '#1d1838';
const want = k => !process.env.ONLY || process.env.ONLY.split(',').includes(k);
const C = { red: '#ff5d6c', green: '#3ddc97', blue: '#4aa3ff', yellow: '#ffd23f', orange: '#ff8a1f' };

// 1024x1024 tasarim uzayinda 2x2 bolge + merkezde dokunus halkasi (oyunun 4 bolgeli ekranini anlatir)
const tile = (x, y, col) => `
  <rect x="${x}" y="${y + 20}" width="340" height="340" rx="96" fill="${INK}"/>
  <rect x="${x}" y="${y}" width="340" height="340" rx="96" fill="${col}" stroke="${INK}" stroke-width="14"/>
  <path d="M${x + 58} ${y + 120} Q${x + 58} ${y + 58} ${x + 120} ${y + 58} L${x + 190} ${y + 58}" fill="none" stroke="#fff" stroke-opacity=".5" stroke-width="24" stroke-linecap="round"/>`;
const tiles = () => tile(152, 152, C.red) + tile(532, 152, C.green) + tile(152, 532, C.blue) + tile(532, 532, C.yellow);
const center = () => `
  <circle cx="512" cy="540" r="124" fill="${INK}"/>
  <circle cx="512" cy="512" r="124" fill="#fff" stroke="${INK}" stroke-width="16"/>
  <circle cx="512" cy="512" r="70" fill="none" stroke="${INK}" stroke-width="16"/>
  <circle cx="512" cy="512" r="26" fill="${C.orange}" stroke="${INK}" stroke-width="10"/>`;
const defs = `<defs><radialGradient id="g" cx="50%" cy="38%" r="75%"><stop offset="0" stop-color="${BG1}"/><stop offset="1" stop-color="${BG2}"/></radialGradient>
  <mask id="m"><rect width="1024" height="1024" fill="#fff"/><circle cx="512" cy="512" r="150" fill="#000"/></mask></defs>`;

const svgFull = () => `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">${defs}<rect width="1024" height="1024" fill="url(#g)"/>${tiles()}${center()}</svg>`;
// adaptif on plan: 108 birimlik tuval; emblem 54 birim genislikte ortalanir (translate = 54 - 360 * olcek). Sistemin dairesel maskesi
// (yaricap 36 birim) en uzak kose noktalarini (~490 tasarim birimi * 0.075 = 36.8) kirpmasin diye eski 0.0833 olcegi kucultuldu.
const FG = 'translate(27 27) scale(0.075) translate(-152 -152)';
const svgFg = () => `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 108 108">${defs.replace(/<mask.*<\/mask>/s, '')}<g transform="${FG}">${tiles()}${center()}</g></svg>`;
const svgMono = () => `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 108 108">${defs}<g transform="${FG}">
  <g mask="url(#m)" fill="#fff"><rect x="152" y="152" width="340" height="340" rx="96"/><rect x="532" y="152" width="340" height="340" rx="96"/><rect x="152" y="532" width="340" height="340" rx="96"/><rect x="532" y="532" width="340" height="340" rx="96"/></g>
  <circle cx="512" cy="512" r="112" fill="#fff"/></g></svg>`;

const DENS = { mdpi: 1, hdpi: 1.5, xhdpi: 2, xxhdpi: 3, xxxhdpi: 4 };
(async () => {
  const b = await chromium.launch();
  const render = async (svg, size, file, { clip = null, bg = null, extraCss = '' } = {}) => {
    const p = await b.newPage({ viewport: { width: size, height: size } });
    await p.setContent(`<html><body style="margin:0;background:${bg || 'transparent'};width:${size}px;height:${size}px;overflow:hidden"><div style="width:${size}px;height:${size}px;${clip ? 'clip-path:' + clip + ';' : ''}${extraCss}">${svg.replace('<svg ', `<svg width="${size}" height="${size}" `)}</div></body></html>`);
    fs.mkdirSync(path.dirname(file), { recursive: true });
    await p.screenshot({ path: file, omitBackground: true });
    await p.close();
  };
  for (const [d, k] of Object.entries(DENS)) {
    const dir = path.join(RES, 'mipmap-' + d);
    if (want('legacy')) {
      await render(svgFull(), Math.round(48 * k), path.join(dir, 'ic_launcher.png'), { clip: 'inset(0 round 22%)' });
      await render(svgFull(), Math.round(48 * k), path.join(dir, 'ic_launcher_round.png'), { clip: 'circle(50% at 50% 50%)' });
    }
    if (want('adaptive')) {
      await render(svgFg(), Math.round(108 * k), path.join(dir, 'ic_launcher_foreground.png'));
      await render(svgMono(), Math.round(108 * k), path.join(dir, 'ic_launcher_monochrome.png'));
    }
  }
  if (want('store')) {
    await render(svgFull(), 512, path.join(OUT, 'icon-512.png'));
    await render(svgFull(), 1024, path.join(OUT, 'icon-1024.png'));
  }
  // Android 12+ sistem acilis ekrani simgesi: sistem simgeyi tuvalin ortasindaki DAIRE (capi tuvalin 2/3'u) ile maskeler;
  // bu yuzden emblem 124 birimlik gorunum kutusuna yerlestirilir (en uzak kose dairenin ~%89'unda kalir, kirpilmaz)
  if (want('splash')) await render(svgFg().replace('viewBox="0 0 108 108"', 'viewBox="-8 -8 124 124"'), 960, path.join(RES, 'drawable-nodpi/splash_icon.png'));
  // eski surumler / acilis arka plani: koyu zemin + ortada simge
  const splash = async (w, h, file) => {
    const p = await b.newPage({ viewport: { width: w, height: h } });
    const s = Math.round(Math.min(w, h) * 0.34);
    await p.setContent(`<html><body style="margin:0;width:${w}px;height:${h}px;background:${SPLASH_BG};display:flex;align-items:center;justify-content:center">
      <div style="width:${s}px;height:${s}px;border-radius:22%;overflow:hidden;box-shadow:0 ${Math.round(s * .06)}px ${Math.round(s * .2)}px rgba(0,0,0,.5)">${svgFull().replace('<svg ', `<svg width="${s}" height="${s}" `)}</div></body></html>`);
    fs.mkdirSync(path.dirname(file), { recursive: true });
    await p.screenshot({ path: file });
    await p.close();
  };
  const SP = { mdpi: [320, 480], hdpi: [480, 800], xhdpi: [720, 1280], xxhdpi: [960, 1600], xxxhdpi: [1280, 1920] };
  if (want('splash')) {
    for (const [d, [w, h]] of Object.entries(SP)) {
      await splash(w, h, path.join(RES, 'drawable-port-' + d, 'splash.png'));
      await splash(h, w, path.join(RES, 'drawable-land-' + d, 'splash.png'));
    }
    await splash(480, 800, path.join(RES, 'drawable/splash.png'));
  }
  await b.close();
  console.log('tamam');
})();
