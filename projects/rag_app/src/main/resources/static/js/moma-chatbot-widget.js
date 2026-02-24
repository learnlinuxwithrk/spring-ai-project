/**
 * ================================================================
 *  MoMA AI Chatbot — Embeddable Widget  v2.0
 *  Ministry of Minority Affairs, Government of India
 *
 *  HOW TO EMBED IN ANY WEBSITE (just 1 line):
 *
 *  <script
 *    src="moma-chatbot-widget.js"
 *    data-api="/chatme"
 *    data-upload="/doupload"
 *    data-title="MoMA AI Assistant"
 *    data-subtitle="Ministry of Minority Affairs"
 *    data-color="#1e40af"
 *    data-accent="#f59e0b"
 *    data-position="right"
 *  ></script>
 *
 *  All data-* attributes are OPTIONAL — defaults work immediately.
 * ================================================================
 */
(function () {
  'use strict';

  if (window.__MOMA_WIDGET__) return;
  window.__MOMA_WIDGET__ = true;

  /* ── 1. Config ────────────────────────────────────────────── */
  const tag = document.currentScript ||
    Array.from(document.querySelectorAll('script')).find(
      s => s.src && s.src.includes('moma-chatbot-widget')
    );

  const C = {
    apiChat  : tag?.getAttribute('data-api')      || '/chatme',
    apiUpload: tag?.getAttribute('data-upload')    || '/doupload',
    title    : tag?.getAttribute('data-title')     || 'MoMA AI Assistant',
    subtitle : tag?.getAttribute('data-subtitle')  || 'Ministry of Minority Affairs',
    color    : tag?.getAttribute('data-color')     || '#1e40af',
    accent   : tag?.getAttribute('data-accent')    || '#f59e0b',
    pos      : tag?.getAttribute('data-position')  || 'right',
  };

  /* ── 2. CSS ───────────────────────────────────────────────── */
  const style = document.createElement('style');
  style.textContent = `
    #mw-root,#mw-root *,#mw-root *::before,#mw-root *::after{
      box-sizing:border-box;margin:0;padding:0;
      font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;
    }
    /* FAB */
    #mw-fab{
      position:fixed;bottom:28px;${C.pos==='left'?'left:28px':'right:28px'};
      width:60px;height:60px;border-radius:50%;
      background:linear-gradient(135deg,${C.color},${C.color}bb);
      border:none;cursor:pointer;outline:none;
      box-shadow:0 8px 28px ${C.color}55;
      display:flex;align-items:center;justify-content:center;
      z-index:2147483646;
      transition:transform .25s cubic-bezier(.34,1.56,.64,1),box-shadow .25s;
      animation:mwPulse 3s ease-in-out infinite;
    }
    #mw-fab:hover{transform:scale(1.12);box-shadow:0 12px 36px ${C.color}88;}
    #mw-fab.mw-on{background:linear-gradient(135deg,#ef4444,#dc2626);animation:none;box-shadow:0 8px 28px #ef444455;}
    @keyframes mwPulse{0%,100%{box-shadow:0 8px 28px ${C.color}55;}50%{box-shadow:0 8px 42px ${C.color}99;}}
    #mw-fab .mw-ico-a{display:flex;}
    #mw-fab .mw-ico-b{display:none;}
    #mw-fab.mw-on .mw-ico-a{display:none;}
    #mw-fab.mw-on .mw-ico-b{display:flex;}
    #mw-badge{
      position:absolute;top:-2px;right:-2px;
      width:18px;height:18px;border-radius:50%;
      background:#ef4444;border:2.5px solid #fff;
      font-size:9px;font-weight:700;color:#fff;
      display:none;align-items:center;justify-content:center;
    }
    #mw-badge.mw-on{display:flex;}
    /* Window */
    #mw-win{
      position:fixed;bottom:104px;${C.pos==='left'?'left:28px':'right:28px'};
      width:368px;height:578px;
      background:#fff;border-radius:18px;
      box-shadow:0 20px 60px rgba(0,0,0,.22),0 4px 16px rgba(0,0,0,.1);
      border:1px solid #e2e8f0;
      display:none;flex-direction:column;
      z-index:2147483645;overflow:hidden;
    }
    #mw-win.mw-on{display:flex;animation:mwIn .32s cubic-bezier(.34,1.56,.64,1);}
    @keyframes mwIn{from{opacity:0;transform:translateY(20px) scale(.95);}to{opacity:1;transform:none;}}
    /* Header */
    #mw-hdr{
      background:linear-gradient(135deg,${C.color} 0%,${C.color}cc 100%);
      padding:14px 16px;display:flex;align-items:center;gap:12px;
      border-bottom:3px solid ${C.accent};flex-shrink:0;
    }
    #mw-hlogo{
      width:42px;height:42px;border-radius:50%;
      background:rgba(255,255,255,.18);border:2px solid rgba(255,255,255,.3);
      display:flex;align-items:center;justify-content:center;flex-shrink:0;
    }
    #mw-hinfo{flex:1;}
    #mw-hinfo h3{color:#fff;font-size:15px;font-weight:700;line-height:1.2;}
    #mw-hstat{display:flex;align-items:center;gap:5px;margin-top:3px;}
    #mw-hblink{
      width:7px;height:7px;border-radius:50%;background:#4ade80;
      animation:mwBlink 2s ease-in-out infinite;
    }
    @keyframes mwBlink{0%,100%{opacity:1}50%{opacity:.3}}
    #mw-hstat span{font-size:11px;color:rgba(255,255,255,.85);}
    #mw-hx{
      width:30px;height:30px;border-radius:8px;flex-shrink:0;
      background:rgba(255,255,255,.18);border:1px solid rgba(255,255,255,.25);
      cursor:pointer;display:flex;align-items:center;justify-content:center;
      transition:background .18s;
    }
    #mw-hx:hover{background:rgba(255,255,255,.32);}
    /* Tabs */
    #mw-tabs{
      display:flex;background:#f8fafc;
      border-bottom:1px solid #e2e8f0;flex-shrink:0;
    }
    .mw-tab{
      flex:1;padding:10px 8px;background:none;
      border:none;border-bottom:2.5px solid transparent;
      cursor:pointer;font-size:12px;font-weight:600;
      color:#94a3b8;display:flex;align-items:center;
      justify-content:center;gap:6px;transition:all .18s;
      font-family:inherit;
    }
    .mw-tab.mw-on{color:${C.color};border-bottom-color:${C.color};background:#fff;}
    .mw-tab:hover:not(.mw-on){color:${C.color};background:#e8ecf0;}
    /* Panels */
    .mw-pnl{display:none;flex:1;flex-direction:column;overflow:hidden;min-height:0;}
    .mw-pnl.mw-on{display:flex;}
    /* Chat body */
    #mw-body{
      flex:1;overflow-y:auto;padding:14px;
      background:#f8fafc;scroll-behavior:smooth;
    }
    #mw-body::-webkit-scrollbar{width:4px;}
    #mw-body::-webkit-scrollbar-thumb{background:#cbd5e1;border-radius:4px;}
    /* Welcome */
    #mw-wel{
      display:flex;flex-direction:column;align-items:center;
      justify-content:center;height:100%;text-align:center;padding:12px;gap:2px;
    }
    .mw-wico{
      width:56px;height:56px;border-radius:16px;margin-bottom:10px;
      background:linear-gradient(135deg,${C.color},${C.color}99);
      display:flex;align-items:center;justify-content:center;
      box-shadow:0 8px 24px ${C.color}44;
    }
    #mw-wel h2{font-size:16px;font-weight:700;color:#1e293b;margin-bottom:4px;}
    #mw-wel p{font-size:12px;color:#64748b;margin-bottom:16px;}
    .mw-suggs{display:flex;flex-direction:column;gap:7px;width:100%;}
    .mw-sg{
      padding:9px 13px;background:#fff;
      border:1.5px solid #e2e8f0;color:#64748b;
      border-radius:10px;cursor:pointer;
      font-size:12px;font-weight:500;font-family:inherit;
      display:flex;align-items:center;gap:10px;text-align:left;
      transition:all .18s;
    }
    .mw-sg:hover{background:${C.color};color:#fff;border-color:${C.color};transform:translateX(4px);}
    .mw-sgi{
      width:27px;height:27px;border-radius:8px;flex-shrink:0;
      background:#f1f5f9;display:flex;align-items:center;justify-content:center;
      transition:background .18s;
    }
    .mw-sg:hover .mw-sgi{background:rgba(255,255,255,.22);}
    /* Messages */
    .mw-msg{margin-bottom:13px;animation:mwMsg .22s ease;}
    @keyframes mwMsg{from{opacity:0;transform:translateY(8px)}to{opacity:1;transform:none}}
    .mw-meta{
      display:flex;
      align-items:right;
      gap:6px;
      font-size:11px;
      font-weight:600;
      color:#1e293b;
      margin-bottom:4px;
    }
    .mw-msg.mw-u .mw-meta{justify-content:flex-end;}
    .mw-mt{font-size:10px;color:#94a3b8;font-weight:400;}
    .mw-row{display:flex;align-items:flex-start;gap:8px;}
    .mw-av{
      width:26px;height:26px;border-radius:50%;flex-shrink:0;
      display:flex;align-items:center;justify-content:center;
      font-size:10px;font-weight:700;color:#fff;margin-top:2px;
    }
    .mw-msg.mw-b .mw-av{background:linear-gradient(135deg,${C.color},${C.color}99);}
    .mw-msg.mw-u .mw-av{background:linear-gradient(135deg,${C.accent},${C.accent}cc);order:2;}
    .mw-bbl{
    font-size: 13px;
    line-height: 1.6;
    color: var(--text-primary);
    background: var(--bg-primary);
    display:flex;align-items:right;
    justify-content:right;
    padding: 10px 12px;
    border-radius: 12px;
//    border: 1px solid var(--border);
    max-width: calc(100% - 10px);
//      font-size:13px;
//      line-height:1.65;
//      color:#1e293b;
//      padding:10px 12px;
//      border-radius:12px;
//      max-width:calc(100% - 34px);
    }
    .mw-msg.mw-b .mw-bbl{
      background:#fff;border:1px solid #e2e8f0;
      border-left:3px solid ${C.color};margin-left:34px;
    }
    .mw-msg.mw-u .mw-bbl{
      background:linear-gradient(135deg,${C.color},${C.color}cc);
      color:#fff;border:none;margin-left:auto;margin-right:34px;
    }
    .mw-bbl p{margin:0 0 6px;}.mw-bbl p:last-child{margin:0;}
    .mw-bbl ul,.mw-bbl ol{padding-left:18px;margin:5px 0;}
    .mw-bbl li{margin-bottom:3px;}
    .mw-bbl code{background:rgba(0,0,0,.07);padding:2px 5px;border-radius:4px;font-size:11px;font-family:monospace;}
    .mw-msg.mw-u .mw-bbl code{background:rgba(255,255,255,.25);}
    .mw-bbl pre{background:#f1f5f9;padding:10px;border-radius:8px;overflow-x:auto;margin:5px 0;font-size:11px;font-family:monospace;}
    .mw-bbl strong{font-weight:700;}
    .mw-bbl h1,.mw-bbl h2,.mw-bbl h3{font-size:14px;font-weight:700;margin:8px 0 4px;}
    /* Typing */
    #mw-typ{display:none;align-items:center;gap:8px;margin-bottom:10px;}
    #mw-typ.mw-on{display:flex;}
    .mw-tav{
      width:26px;height:26px;border-radius:50%;flex-shrink:0;
      background:linear-gradient(135deg,${C.color},${C.color}99);
      display:flex;align-items:center;justify-content:center;
    }
    .mw-tbbl{
      display:flex;gap:4px;align-items:center;
      background:#fff;border:1px solid #e2e8f0;
      border-left:3px solid ${C.color};
      padding:10px 14px;border-radius:12px;
    }
    .mw-td{
      width:6px;height:6px;border-radius:50%;
      background:${C.color}88;animation:mwBnc 1.4s ease infinite;
    }
    .mw-td:nth-child(2){animation-delay:.2s;}
    .mw-td:nth-child(3){animation-delay:.4s;}
    @keyframes mwBnc{0%,60%,100%{transform:translateY(0)}30%{transform:translateY(-7px)}}
    /* Footer input */
    #mw-ftr{padding:11px 13px;background:#fff;border-top:1px solid #e2e8f0;flex-shrink:0;}
    #mw-irow{display:flex;gap:8px;align-items:flex-end;}
    #mw-iwrap{
      flex:1;background:#f8fafc;border:2px solid #e2e8f0;
      border-radius:12px;padding:9px 11px;
      display:flex;align-items:center;gap:8px;transition:border-color .18s;
    }
    #mw-iwrap:focus-within{border-color:${C.color};}
    #mw-att{
      background:none;border:none;cursor:pointer;padding:0;
      color:#94a3b8;display:flex;align-items:center;flex-shrink:0;transition:color .18s;
    }
    #mw-att:hover{color:${C.color};}
    #mw-inp{
      flex:1;background:none;border:none;outline:none;
      color:#1e293b;font-size:13px;font-family:inherit;
      resize:none;max-height:80px;line-height:1.5;
    }
    #mw-inp::placeholder{color:#94a3b8;}
    #mw-snd{
      width:38px;height:38px;flex-shrink:0;border-radius:10px;
      background:linear-gradient(135deg,${C.color},${C.color}bb);
      border:none;cursor:pointer;
      display:flex;align-items:center;justify-content:center;
      box-shadow:0 3px 12px ${C.color}44;transition:all .2s;
    }
    #mw-snd:hover:not([disabled]){transform:scale(1.08);box-shadow:0 5px 18px ${C.color}66;}
    #mw-snd[disabled]{background:#cbd5e1;cursor:not-allowed;box-shadow:none;transform:none;}
    /* Upload */
    #mw-uppnl{padding:16px;overflow-y:auto;flex:1;background:#f8fafc;}
    .mw-uplbl{font-size:14px;font-weight:700;color:#1e293b;margin-bottom:14px;display:block;}
    #mw-dtype{
      width:100%;padding:10px 12px;border:1.5px solid #e2e8f0;border-radius:10px;
      background:#fff;color:#1e293b;font-size:13px;font-family:inherit;
      outline:none;cursor:pointer;margin-bottom:12px;transition:border-color .18s;
    }
    #mw-dtype:focus{border-color:${C.color};}
    #mw-dz{
      border:2px dashed #cbd5e1;border-radius:12px;
      padding:28px 16px;text-align:center;cursor:pointer;
      background:#fff;transition:all .2s;margin-bottom:12px;
    }
    #mw-dz:hover,#mw-dz.mw-drag{border-color:${C.color};background:${C.color}08;}
    .mw-dzico{
      width:46px;height:46px;border-radius:12px;margin:0 auto 10px;
      background:linear-gradient(135deg,${C.color},${C.color}99);
      display:flex;align-items:center;justify-content:center;
    }
    #mw-dz p{font-size:13px;font-weight:600;color:#1e293b;margin-bottom:3px;}
    #mw-dz span{font-size:11px;color:#64748b;}
    #mw-fprev{
      display:none;background:#f0fdf4;border:1.5px solid #4ade80;
      border-radius:10px;padding:10px 12px;margin-bottom:12px;
    }
    #mw-fprev.mw-on{display:flex;align-items:center;gap:9px;}
    .mw-fpico{
      width:32px;height:32px;border-radius:8px;background:#22c55e;flex-shrink:0;
      display:flex;align-items:center;justify-content:center;
    }
    .mw-fpinf{flex:1;min-width:0;}
    .mw-fpnm{font-size:13px;font-weight:600;color:#1e293b;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
    .mw-fpsz{font-size:11px;color:#64748b;}
    #mw-frm{background:none;border:none;cursor:pointer;color:#ef4444;display:flex;align-items:center;flex-shrink:0;}
    #mw-upbtn{
      width:100%;padding:12px;border:none;border-radius:10px;
      background:linear-gradient(135deg,${C.color},${C.color}bb);
      color:#fff;font-size:14px;font-weight:700;font-family:inherit;
      cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;
      box-shadow:0 3px 14px ${C.color}44;transition:all .2s;
    }
    #mw-upbtn:hover:not([disabled]){transform:translateY(-1px);box-shadow:0 6px 20px ${C.color}66;}
    #mw-upbtn[disabled]{background:#cbd5e1;cursor:not-allowed;box-shadow:none;transform:none;}
    #mw-upalrt{
      display:none;padding:10px 12px;border-radius:8px;
      font-size:12px;font-weight:600;margin-top:10px;
      align-items:center;gap:7px;line-height:1.4;
    }
    #mw-upalrt.mw-on{display:flex;}
    #mw-upalrt.mw-ok{background:#f0fdf4;border:1.5px solid #4ade80;color:#16a34a;}
    #mw-upalrt.mw-err{background:#fef2f2;border:1.5px solid #fca5a5;color:#dc2626;}
    .mw-spin{
      width:15px;height:15px;border-radius:50%;
      border:2px solid rgba(255,255,255,.3);border-top-color:#fff;
      animation:mwSpin .7s linear infinite;display:none;flex-shrink:0;
    }
    .mw-spin.mw-on{display:block;}
    @keyframes mwSpin{to{transform:rotate(360deg)}}
    /* Powered by */
    #mw-pw{
      text-align:center;padding:7px;font-size:10px;
      color:#94a3b8;background:#f8fafc;
      border-top:1px solid #e2e8f0;flex-shrink:0;letter-spacing:.2px;
    }
    #mw-pw b{color:${C.color};}
    /* Responsive */
    @media(max-width:480px){
      #mw-win{
        width:calc(100vw - 16px);height:calc(100vh - 94px);
        ${C.pos==='left'?'left:8px':'right:8px'};
        bottom:76px;border-radius:14px;
      }
      #mw-fab{${C.pos==='left'?'left:14px':'right:14px'};bottom:14px;width:54px;height:54px;}
    }
  `;
  document.head.appendChild(style);

  /* ── 3. SVG helpers ─────────────────────────────────────── */
  const s = (d, sz=16, sc='#fff', sw=2) =>
    `<svg width="${sz}" height="${sz}" viewBox="0 0 24 24" fill="none"
      stroke="${sc}" stroke-width="${sw}" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;

  const I = {
    chat:  s('<path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>',24),
    x:     s('<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>',18),
    send:  s('<line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>',17),
    att:   s('<path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/>',16,'#94a3b8'),
    up:    s('<path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M17 8l-5-5-5 5M12 3v12"/>',16),
    file:  s('<path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6zM14 2v6h6"/>',15),
    bot:   s('<circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3M12 17h.01"/>',15),
    ok:    s('<polyline points="20 6 9 17 4 12"/>',14,'#16a34a'),
    err:   s('<circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>',14,'#dc2626'),
    s1: s('<path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6zM14 2v6h6M16 13H8M16 17H8M10 9H8"/>',14,C.color),
    s2: s('<path d="M2 3h6a4 4 0 014 4v14a3 3 0 00-3-3H2zM22 3h-6a4 4 0 00-4 4v14a3 3 0 013-3h7z"/>',14,C.color),
    s3: s('<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>',14,C.color),
    s4: s('<path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>',14,C.color),
    ashoka:`<svg width="22" height="22" viewBox="0 0 64 64" fill="none">
      <circle cx="32" cy="32" r="26" stroke="#fff" stroke-width="4" fill="none"/>
      <circle cx="32" cy="32" r="5" fill="#fff"/>
      <line x1="32" y1="6" x2="32" y2="58" stroke="#fff" stroke-width="2.5"/>
      <line x1="6" y1="32" x2="58" y2="32" stroke="#fff" stroke-width="2.5"/>
      <line x1="14" y1="14" x2="50" y2="50" stroke="#fff" stroke-width="1.8"/>
      <line x1="50" y1="14" x2="14" y2="50" stroke="#fff" stroke-width="1.8"/>
    </svg>`,
  };

  const cc = (i) => i.replace(/stroke="[^"]*"/g,'stroke="currentColor"');

  /* ── 4. HTML ─────────────────────────────────────────────── */
  const root = document.createElement('div');
  root.id = 'mw-root';
  root.innerHTML = `
    <button id="mw-fab" aria-label="Open AI Chatbot">
      <span id="mw-badge">1</span>
      <span class="mw-ico-a">${I.chat}</span>
      <span class="mw-ico-b">${I.x}</span>
    </button>

    <div id="mw-win" role="dialog" aria-label="${C.title}">
      <div id="mw-hdr">
        <div id="mw-hlogo">${I.ashoka}</div>
        <div id="mw-hinfo">
          <h3>${C.title}</h3>
          <div id="mw-hstat"><div id="mw-hblink"></div><span>Online &bull; ${C.subtitle}</span></div>
        </div>
        <button id="mw-hx" aria-label="Close">${I.x}</button>
      </div>

      <div id="mw-tabs">
        <button class="mw-tab mw-on" data-pnl="mw-cpnl">${cc(I.chat)} Chat</button>
      </div>

      <!-- Chat Panel -->
      <div class="mw-pnl mw-on" id="mw-cpnl">
        <div id="mw-body">
          <div id="mw-wel">
            <div class="mw-wico">${s('<circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3M12 17h.01"/>',26)}</div>
            <h2>Hello! How can I help?</h2>
            <p>Ask me anything about Ministry schemes &amp; services</p>
            <div class="mw-suggs">
              <button class="mw-sg" data-q="Help me with documents"><div class="mw-sgi">${I.s1}</div>Help with documents</button>
              <button class="mw-sg" data-q="Tell me about Ministry schemes"><div class="mw-sgi">${I.s2}</div>Learn about schemes</button>
              <button class="mw-sg" data-q="What information can you provide?"><div class="mw-sgi">${I.s3}</div>Get information</button>
              <button class="mw-sg" data-q="What Ministry services are available?"><div class="mw-sgi">${I.s4}</div>Ministry services</button>
            </div>
          </div>
          <div id="mw-typ">
            <div class="mw-tav">${I.bot}</div>
            <div class="mw-tbbl"><div class="mw-td"></div><div class="mw-td"></div><div class="mw-td"></div></div>
          </div>
        </div>
        <div id="mw-ftr">
          <div id="mw-irow">
            <div id="mw-iwrap">
              <button id="mw-att" title="Attach file">${I.att}</button>
              <textarea id="mw-inp" placeholder="Type your message..." rows="1"></textarea>
            </div>
            <button id="mw-snd">${I.send}</button>
          </div>
        </div>
      </div>

      <!-- Upload Panel
      <div class="mw-pnl" id="mw-upnl">
        <div id="mw-uppnl">
          <span class="mw-uplbl">Upload Document</span>
          <select id="mw-dtype">
            <option value="">-- Select document type --</option>
            <option value="pdf">PDF Document</option>
            <option value="text">Text Document (.txt)</option>
            <option value="json">JSON Document</option>
          </select>
          <div id="mw-dz">
            <div class="mw-dzico">${s('<path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M17 8l-5-5-5 5M12 3v12"/>',22)}</div>
            <p>Drop file here or click to browse</p>
            <span>Supports PDF, TXT, JSON &mdash; Max 10 MB</span>
          </div>
          <div id="mw-fprev">
            <div class="mw-fpico">${I.file}</div>
            <div class="mw-fpinf">
              <div class="mw-fpnm" id="mw-fpnm">document.pdf</div>
              <div class="mw-fpsz" id="mw-fpsz">0 KB</div>
            </div>
            <button id="mw-frm">${s('<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>',15,'#ef4444')}</button>
          </div>
          <button id="mw-upbtn" disabled>
            <span class="mw-spin" id="mw-spin"></span>
            ${I.up}
            <span id="mw-uplbtxt">Upload Document</span>
          </button>
          <div id="mw-upalrt"></div>
        </div>
      </div>
-->
      <div id="mw-pw">Powered by <b>${C.title}</b> &bull; Govt. of India</div>
    </div>

    <input type="file" id="mw-fc" accept=".pdf,.txt,.json" style="display:none">
    <input type="file" id="mw-fu" accept=".pdf,.txt,.json" style="display:none">
  `;
  document.body.appendChild(root);

  /* ── 5. Wire ──────────────────────────────────────────────── */
  const G = id => document.getElementById(id);
  const fab    = G('mw-fab'), badge  = G('mw-badge'), win  = G('mw-win');
  const hx     = G('mw-hx'),  body   = G('mw-body'),  wel  = G('mw-wel');
  const typ    = G('mw-typ'), inp    = G('mw-inp'),   snd  = G('mw-snd');
  const att    = G('mw-att'), fc     = G('mw-fc'),    fu   = G('mw-fu');
  const dz     = G('mw-dz'),  fprev  = G('mw-fprev'), fpnm = G('mw-fpnm');
  const fpsz   = G('mw-fpsz'),frm   = G('mw-frm'),   dtype= G('mw-dtype');
  const upbtn  = G('mw-upbtn'),spin  = G('mw-spin'),  uptx = G('mw-uplbtxt');
  const upalrt = G('mw-upalrt');

  let open=false, es=null, uf=null;

  /* Toggle */
  function tog(){
    open=!open;
    win.classList.toggle('mw-on',open);
    fab.classList.toggle('mw-on',open);
    if(open){badge.classList.remove('mw-on');setTimeout(()=>inp.focus(),300);}
  }
  fab.addEventListener('click',tog);
  hx.addEventListener('click',tog);
  document.addEventListener('keydown',e=>{if(e.key==='Escape'&&open)tog();});

  /* Tabs */
  root.querySelectorAll('.mw-tab').forEach(t=>{
    t.addEventListener('click',()=>{
      root.querySelectorAll('.mw-tab').forEach(x=>x.classList.remove('mw-on'));
      root.querySelectorAll('.mw-pnl').forEach(x=>x.classList.remove('mw-on'));
      t.classList.add('mw-on');
      G(t.dataset.pnl).classList.add('mw-on');
    });
  });

  /* Suggestions */
  root.querySelectorAll('.mw-sg').forEach(b=>{
    b.addEventListener('click',()=>{inp.value=b.dataset.q;doSend();});
  });

  /* Auto-resize */
  inp.addEventListener('input',()=>{
    inp.style.height='auto';
    inp.style.height=Math.min(inp.scrollHeight,80)+'px';
  });
  inp.addEventListener('keydown',e=>{if(e.key==='Enter'&&!e.shiftKey){e.preventDefault();doSend();}});
  snd.addEventListener('click',doSend);

  /* Attach in chat */
  att.addEventListener('click',()=>fc.click());
  fc.addEventListener('change',e=>{
    const f=e.target.files[0];if(!f)return;
    addU('Uploading: '+f.name);upDirect(f);fc.value='';
  });

  /* Drop zone */
  dz.addEventListener('click',()=>fu.click());
  dz.addEventListener('dragover',e=>{e.preventDefault();dz.classList.add('mw-drag');});
  dz.addEventListener('dragleave',()=>dz.classList.remove('mw-drag'));
  dz.addEventListener('drop',e=>{e.preventDefault();dz.classList.remove('mw-drag');if(e.dataTransfer.files[0])setUF(e.dataTransfer.files[0]);});
  fu.addEventListener('change',e=>{if(e.target.files[0])setUF(e.target.files[0]);fu.value='';});

  function setUF(f){
    uf=f;fpnm.textContent=f.name;
    fpsz.textContent='Size: '+(f.size/1024).toFixed(1)+' KB';
    fprev.classList.add('mw-on');chkUp();
  }
  frm.addEventListener('click',()=>{uf=null;fprev.classList.remove('mw-on');chkUp();});
  dtype.addEventListener('change',chkUp);
  function chkUp(){upbtn.disabled=!(uf&&dtype.value);}

  upbtn.addEventListener('click',async()=>{
    if(!uf||!dtype.value)return;
    upbtn.disabled=true;uptx.textContent='Uploading...';spin.classList.add('mw-on');setAlrt('','');
    const fd=new FormData();fd.append('file',uf);fd.append('type',dtype.value);
    try{
      const r=await fetch(C.apiUpload,{method:'POST',body:fd});
      const d=await r.json();
      if(d.success){
        setAlrt('ok','Uploaded! '+(d.docs||0)+' documents indexed.');
        uf=null;fprev.classList.remove('mw-on');dtype.value='';chkUp();
        setTimeout(()=>setAlrt('',''),5000);
      }else throw new Error(d.message||'Upload failed');
    }catch(e){setAlrt('err',e.message);}
    finally{uptx.textContent='Upload Document';spin.classList.remove('mw-on');chkUp();}
  });

  function setAlrt(t,m){
    if(!t){upalrt.className='';upalrt.innerHTML='';return;}
    upalrt.className='mw-on mw-'+t;
    upalrt.innerHTML=(t==='ok'?I.ok:I.err)+'<span>'+m+'</span>';
  }

  /* Chat */
  function doSend(){
    const txt=inp.value.trim();if(!txt)return;
    addU(txt);inp.value='';inp.style.height='auto';stream(txt);
  }

  function rmWel(){if(wel&&wel.parentNode)wel.remove();}

  function addU(txt){
    rmWel();
    const d=document.createElement('div');
    d.className='mw-msg mw-u';
    d.innerHTML=`<div class="mw-meta"><span>You</span><span class="mw-mt">${now()}</span></div>
      <div class="mw-bbl">${esc(txt)}</div>`;
    body.insertBefore(d,typ);scrl();
  }

  function mkBot(){
    rmWel();
    const d=document.createElement('div');
    d.className='mw-msg mw-b';
    d.innerHTML=`<div class="mw-row">
      <div class="mw-av">AI</div>
      <div style="flex:1;min-width:0">
        <div class="mw-meta"><span>AI Assistant</span><span class="mw-mt">${now()}</span></div>
        <div class="mw-bbl"></div>
      </div></div>`;
    body.insertBefore(d,typ);scrl();
    return d.querySelector('.mw-bbl');
  }

  function stream(q){
    if(es){es.close();es=null;}
    snd.disabled=true;typ.classList.add('mw-on');scrl();
    const bbl=mkBot();
    let txt='';
    es=new EventSource(`${C.apiChat}?q=${encodeURIComponent(q)}&userId=${uid()}`);
    es.onmessage=ev=>{
      if(!ev.data)return;
      typ.classList.remove('mw-on');
      txt=join(txt,ev.data);
      try{
        const html=window.marked?window.marked.parse(txt):txt;
        bbl.innerHTML=window.DOMPurify?window.DOMPurify.sanitize(html):html;
      }catch{bbl.textContent=txt;}
      if(window.Prism)window.Prism.highlightAll();
      scrl();
    };
    es.addEventListener('done',done);
    es.onerror=()=>{done();if(!txt)bbl.innerHTML='<p style="color:#ef4444">Connection error. Please try again.</p>';};
    const tm=setTimeout(done,60000);
    function done(){
      clearTimeout(tm);if(es){es.close();es=null;}
      snd.disabled=false;typ.classList.remove('mw-on');
      if(!open){badge.textContent='1';badge.classList.add('mw-on');}
    }
  }

  async function upDirect(f){
    const fd=new FormData();fd.append('file',f);fd.append('type',f.name.endsWith('.pdf')?'pdf':'text');
    try{
      const r=await fetch(C.apiUpload,{method:'POST',body:fd});const d=await r.json();
      addBot(d.success?'File uploaded! '+(d.docs||0)+' docs indexed.':'Upload failed: '+(d.message||'Error'));
    }catch(e){addBot('Upload error: '+e.message);}
  }

  function addBot(txt){
    rmWel();
    const d=document.createElement('div');
    d.className='mw-msg mw-b';
    d.innerHTML=`<div class="mw-row">
      <div class="mw-av">AI</div>
      <div style="flex:1;min-width:0">
        <div class="mw-meta"><span>AI Assistant</span><span class="mw-mt">${now()}</span></div>
        <div class="mw-bbl"><p>${esc(txt)}</p></div>
      </div></div>`;
    body.insertBefore(d,typ);scrl();
  }

  /* Utils */
  function scrl(){body.scrollTo({top:body.scrollHeight,behavior:'smooth'});}
  function now(){return new Date().toLocaleTimeString('en-US',{hour:'2-digit',minute:'2-digit',hour12:true});}
  function esc(t){const d=document.createElement('div');d.textContent=t;return d.innerHTML;}
  function join(a,b){
    if(!a)return b;
    if(a.endsWith(' ')||a.endsWith('\n')||b.startsWith(' ')||b.startsWith('\n'))return a+b;
    return a+' '+b;
  }
  function uid(){
    let id=localStorage.getItem('mw_uid');
    if(!id){
      id='xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,c=>{
        const r=Math.random()*16|0;return(c==='x'?r:(r&0x3|0x8)).toString(16);
      });
      localStorage.setItem('mw_uid',id);
    }
    return id;
  }

  /* Load deps */
  function loadSrc(src,cb){
    if(document.querySelector(`script[src="${src}"]`)){if(cb)cb();return;}
    const s=document.createElement('script');s.src=src;s.onload=cb||null;
    document.head.appendChild(s);
  }
  loadSrc('https://cdn.jsdelivr.net/npm/marked@9/marked.min.js',()=>
    loadSrc('https://cdn.jsdelivr.net/npm/dompurify@3.0.8/dist/purify.min.js'));

  /* Badge on load */
  setTimeout(()=>{if(!open)badge.classList.add('mw-on');},2500);

})();
