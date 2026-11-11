// Dokun! Google Play magaza gorselleri: ozellik grafigi (1024x500) ve telefon/tablet ekran goruntuleri.
// Calistir: node store/build-store-assets.js   (DOKUN=<klasor> ile baska bir kopya secilebilir)
// Cikti: store/out/feature-graphic-1024x500.png, store/out/screenshots/phone-*.png (1080x1920), tablet-*.png (1600x2560)
const { chromium } = require('playwright');
const fs = require('fs'), path = require('path');
const ROOT = process.env.DOKUN || path.resolve(__dirname, '..');
const URL = 'file://' + ROOT + '/index.html';
const OUT = path.join(__dirname, 'out');
const SHOTS = path.join(OUT, 'screenshots');
fs.mkdirSync(SHOTS, { recursive: true });

const sleep = ms => new Promise(r => setTimeout(r, ms));

async function featureGraphic(b) {
  const p = await (await b.newContext({ viewport: { width: 1024, height: 500 }, deviceScaleFactor: 1 })).newPage();
  await p.goto(URL); await p.waitForTimeout(600);
  await p.evaluate(() => {
    const sym = id => { const s = document.getElementById(id); return s ? s.getAttribute('viewBox') : '0 0 48 48'; };
    const ic = (id, size) => '<svg viewBox="' + sym(id) + '" width="' + size + '" height="' + size + '" style="overflow:visible"><use href="#' + id + '"/></svg>';
    const tile = (col, rot, id, size) => '<div style="width:160px;height:160px;border-radius:28px;background:var(--' + col + ');border:5px solid var(--ink);box-shadow:0 8px 0 var(--ink);display:flex;align-items:center;justify-content:center;transform:rotate(' + rot + 'deg);position:relative;overflow:hidden">' +
      '<div style="position:absolute;inset:0;background:radial-gradient(rgba(255,255,255,.22) 2px,transparent 2.5px) 0 0/18px 18px"></div>' +
      '<div style="position:relative;filter:none">' + ic(id, size) + '</div></div>';
    const logo = document.querySelector('.logo').cloneNode(true);
    logo.style.fontSize = '128px'; logo.style.padding = '0'; logo.style.gap = '4px';
    const ov = document.createElement('div');
    ov.id = 'fg';
    ov.style.cssText = 'position:fixed;inset:0;z-index:99999;background:var(--night);overflow:hidden;font-family:var(--f-body)';
    ov.innerHTML =
      '<div style="position:absolute;inset:0;background:radial-gradient(rgba(255,255,255,.09) 2.5px,transparent 3px) 0 0/26px 26px"></div>' +
      '<div id="fg-left" style="position:absolute;left:50px;top:0;bottom:0;width:540px;display:flex;flex-direction:column;justify-content:center;gap:18px"></div>' +
      '<div style="position:absolute;right:64px;top:50%;margin-top:-178px;display:grid;grid-template-columns:160px 160px;gap:24px 22px">' +
        tile('red', -6, 'a-balloon', 106) + tile('green', 5, 'a-target', 106) + tile('blue', 4, 'f-pig', 106) + tile('yellow', -5, 'a-mole', 106) +
      '</div>';
    document.body.appendChild(ov);
    const left = ov.querySelector('#fg-left');
    left.appendChild(logo);
    const tag = document.createElement('div');
    tag.style.cssText = 'font-family:var(--f-display);font-weight:600;font-size:31px;line-height:1.2;color:var(--paper);max-width:470px;text-shadow:var(--ol2)';
    tag.textContent = 'Arkadaşlarla hızlı mini oyun yarışları';
    left.appendChild(tag);
    const pills = document.createElement('div');
    pills.style.cssText = 'display:flex;gap:12px;flex-wrap:wrap';
    ['16 mini oyun', '2-4 oyuncu', 'Reklamsız'].forEach((t, i) => {
      const c = ['--yellow', '--green', '--blue'][i];
      const e = document.createElement('div');
      e.style.cssText = 'font-family:var(--f-display);font-weight:700;font-size:22px;padding:5px 16px;border-radius:999px;background:var(' + c + ');color:var(--ink);border:4px solid var(--ink);box-shadow:0 4px 0 var(--ink)';
      e.textContent = t; pills.appendChild(e);
    });
    left.appendChild(pills);
  });
  await p.waitForTimeout(300);
  await p.screenshot({ path: path.join(OUT, 'feature-graphic-1024x500.png') });
  await p.context().close();
}


// ---- ekran goruntuleri: gercek oyun durumlari ----
const gi = (p, name) => p.evaluate(n => __dokun.GAMES.findIndex(g => g.name === n), name);
const startGame = async (p, name, n) => {
  const i = await gi(p, name);
  await p.evaluate(([n, i]) => { __dokun.quitMatch(); __dokun.setup(n); __dokun.resetZones(); window.__r = __dokun.GAMES[i].run(); }, [n, i]);
};
// ekrandaki ogelere (secici) gercek dokunus gonder
const tapEls = async (p, sel, max = 3) => {
  const pts = await p.evaluate(([sel, max]) => [...document.querySelectorAll(sel)].slice(0, max).map(e => { const r = e.getBoundingClientRect(); return [r.x + r.width / 2, r.y + r.height / 2]; }), [sel, max]);
  for (const [x, y] of pts) { await p.touchscreen.tap(x, y); await sleep(90); }
};

async function phoneShots(b, tag, vw, vh, dsf, list) {
  const ctx = await b.newContext({ viewport: { width: vw, height: vh }, deviceScaleFactor: dsf, hasTouch: true, isMobile: true });
  const p = await ctx.newPage();
  await p.goto(URL); await p.waitForTimeout(800);
  const out = n => path.join(SHOTS, tag + '-' + n + '.png');
  const S = {
    '01-menu': async () => { await p.screenshot({ path: out('01-menu') }); },
    '02-tur-karti': async () => {
      await p.evaluate(() => { __dokun.setup(2); __dokun.play([__dokun.GAMES.find(g => g.name === 'Balon Patlat')]); });
      await p.waitForTimeout(1100); await p.screenshot({ path: out('02-tur-karti') }); await p.evaluate(() => __dokun.quitMatch());
    },
    '03-balon': async () => {
      await startGame(p, 'Balon Patlat', 2); await p.waitForTimeout(1800);
      await tapEls(p, '.zone .balloon, .zone [class*="balloon"]', 2); await p.waitForTimeout(160);
      await p.screenshot({ path: out('03-balon') });
    },
    '04-hafiza-4': async () => {
      await startGame(p, 'Hafıza Eşleştir', 4); await p.waitForTimeout(900);
      for (let z = 0; z < 4; z++) await tapEls(p, '.zone:nth-child(' + (z + 1) + ') .card', 2);
      await p.waitForTimeout(200); await p.screenshot({ path: out('04-hafiza-4') });
    },
    '05-tepki': async () => {
      await startGame(p, 'Tepki', 2);
      await p.waitForFunction(() => __dokun.zones[0] && __dokun.zones[0].dataset.s === 'sig', null, { timeout: 15000, polling: 30 }).catch(() => {});
      await p.screenshot({ path: out('05-tepki') });
    },
    '06-hedef-3': async () => { await startGame(p, 'Hedef Avı', 3); await p.waitForTimeout(900); await p.screenshot({ path: out('06-hedef-3') }); },
    '07-kazanan': async () => {
      await p.evaluate(() => { __dokun.quitMatch(); __dokun.setup(2); __dokun.scores[0] = 5; __dokun.scores[1] = 2; __dokun.finish(); });
      await p.waitForTimeout(1500); await p.screenshot({ path: out('07-kazanan') });
    },
    '08-oyun-sec': async () => {
      await p.evaluate(() => { __dokun.quitMatch(); }); await p.waitForTimeout(300);
      await p.click('#pick-btn'); await p.waitForTimeout(500); await p.screenshot({ path: out('08-oyun-sec') });
      await p.click('#back').catch(() => {}); await p.waitForTimeout(200);
    },
    '09-cevrimici': async () => {
      await p.evaluate(() => { __dokun.quitMatch(); }); await p.waitForTimeout(300);
      await p.click('#net-btn'); await p.waitForTimeout(500); await p.screenshot({ path: out('09-cevrimici') });
    },
  };
  for (const k of list) {
    try { await S[k](); } catch (e) { console.log('UYARI', tag, k, e.message.split('\n')[0]); }
    await p.evaluate(() => { try { __dokun.quitMatch(); } catch (_) {} }); await p.waitForTimeout(250);
    if (k === '09-cevrimici') { await p.reload(); await p.waitForTimeout(600); }
  }
  await ctx.close();
}

(async () => {
  const b = await chromium.launch();
  await featureGraphic(b);
  const ALL = ['01-menu', '02-tur-karti', '03-balon', '04-hafiza-4', '05-tepki', '06-hedef-3', '07-kazanan', '08-oyun-sec', '09-cevrimici'];
  await phoneShots(b, 'phone', 360, 640, 3, ALL);              // 1080x1920
  await phoneShots(b, 'tablet7', 600, 960, 2, ['01-menu', '03-balon', '04-hafiza-4']);   // 1200x1920
  await phoneShots(b, 'tablet10', 800, 1280, 2, ['01-menu', '03-balon', '04-hafiza-4']); // 1600x2560
  await b.close();
  console.log('tamam: ' + OUT);
})();
