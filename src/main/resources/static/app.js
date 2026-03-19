// ════════════════════════════════════════════════════
//  app.js  —  도서관 대출/반납 관리 프론트엔드
// ════════════════════════════════════════════════════

// ── CONFIG ────────────────────────────────────────────
const API_BASE = '/api/rentals';

// Mock 모드: true → 샘플 데이터 사용 / false → 실제 Spring Boot API 호출
const MOCK_MODE = true;


// ════════════════════════════════════════════════════
//  MOCK DATA  (MOCK_MODE = true 일 때만 사용)
// ════════════════════════════════════════════════════
function daysFromNow(d) {
    const t = new Date();
    t.setDate(t.getDate() + d);
    return t.toISOString().split('T')[0];
}

let mockRentals = [
    { rentalId:101, bookId:3,  bookTitle:'채식주의자',    bookAuthor:'한강',      memberId:1001, rentalDate:daysFromNow(-10), dueDate:daysFromNow(4),  returnDate:null, status:'RENTED',   renewCount:0 },
    { rentalId:102, bookId:7,  bookTitle:'82년생 김지영', bookAuthor:'조남주',    memberId:1001, rentalDate:daysFromNow(-16), dueDate:daysFromNow(-2), returnDate:null, status:'RENTED',   renewCount:0 },
    { rentalId:103, bookId:12, bookTitle:'아몬드',        bookAuthor:'손원평',    memberId:1001, rentalDate:daysFromNow(-20), dueDate:daysFromNow(-6), returnDate:null, status:'RENTED',   renewCount:1 },
    { rentalId:104, bookId:5,  bookTitle:'데미안',        bookAuthor:'헤르만 헤세', memberId:1002, rentalDate:daysFromNow(-8),  dueDate:daysFromNow(6),  returnDate:null, status:'RENTED',   renewCount:0 },
    { rentalId:105, bookId:9,  bookTitle:'어린 왕자',     bookAuthor:'생텍쥐페리', memberId:1002, rentalDate:daysFromNow(-14), dueDate:daysFromNow(0),  returnDate:null, status:'RENTED',   renewCount:0 },
];

let mockStats = [
    [3,  '채식주의자',    42],
    [7,  '82년생 김지영', 38],
    [12, '아몬드',        31],
    [5,  '데미안',        27],
    [9,  '어린 왕자',     25],
    [14, '소년이 온다',   19],
    [2,  '흰',            16],
    [21, '경애의 마음',   14],
];

let nextRentalId = 200;


// ════════════════════════════════════════════════════
//  API HELPERS  —  실제 API & Mock 공통 인터페이스
// ════════════════════════════════════════════════════

/**
 * GET 요청
 * @param {string} url  ex) '/member/1001', '/stats'
 */
async function apiGet(url) {
    if (MOCK_MODE) return mockGet(url);
    const res = await fetch(API_BASE + url);
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

/**
 * POST 요청
 * @param {string} url   ex) '', '/return', '/renew/101'
 * @param {object} body  요청 바디 (JSON)
 */
async function apiPost(url, body) {
    if (MOCK_MODE) return mockPost(url, body);
    const res = await fetch(API_BASE + url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });
    if (!res.ok) throw new Error(await res.text());
    return res.text();
}

// ── Mock GET ──────────────────────────────────────────
function mockGet(url) {
    // GET /api/rentals/member/{id}  → 사용자 대출 목록
    if (url.startsWith('/member/')) {
        const id = parseInt(url.split('/member/')[1]);
        return Promise.resolve(
            mockRentals.filter(r => r.memberId === id && r.status === 'RENTED')
        );
    }
    // GET /api/rentals/stats  → 인기 도서
    if (url === '/stats') return Promise.resolve([...mockStats]);
    return Promise.resolve([]);
}

// ── Mock POST ─────────────────────────────────────────
function mockPost(url, body) {
    // POST /api/rentals  → 대출 신청
    if (url === '') {
        const todayStr = new Date().toISOString().split('T')[0];
        const todayCount = mockRentals.filter(r => r.memberId === body.memberId && r.rentalDate === todayStr).length;
        if (todayCount >= 3) return Promise.reject(new Error('하루 최대 3권까지 대출 가능합니다.'));
        const already = mockRentals.find(r => r.bookId === body.bookId && r.status === 'RENTED');
        if (already)        return Promise.reject(new Error('이미 대출된 책입니다.'));
        const due = new Date();
        due.setDate(due.getDate() + 14);
        mockRentals.push({
            rentalId:   nextRentalId++,
            bookId:     body.bookId,
            bookTitle:  `도서 #${body.bookId}`,
            bookAuthor: '저자명',
            memberId:   body.memberId,
            rentalDate: todayStr,
            dueDate:    due.toISOString().split('T')[0],
            returnDate: null,
            status:     'RENTED',
            renewCount: 0,
        });
        return Promise.resolve('대출 완료');
    }

    // POST /api/rentals/return  → 반납
    if (url === '/return') {
        const r = mockRentals.find(r => r.rentalId === body.rentalId);
        if (!r)                    return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.status === 'RETURNED') return Promise.reject(new Error('이미 반납된 도서입니다.'));
        r.status     = 'RETURNED';
        r.returnDate = new Date().toISOString().split('T')[0];
        return Promise.resolve('반납 완료');
    }

    // POST /api/rentals/renew/{id}  → 재대출(연장)
    if (url.startsWith('/renew/')) {
        const id = parseInt(url.split('/renew/')[1]);
        const r  = mockRentals.find(r => r.rentalId === id);
        if (!r)              return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.renewCount >= 1) return Promise.reject(new Error('재대출은 1회만 가능합니다.'));
        r.renewCount++;
        const due = new Date(r.dueDate);
        due.setDate(due.getDate() + 14);
        r.dueDate = due.toISOString().split('T')[0];
        return Promise.resolve('재대출 완료');
    }
}


// ════════════════════════════════════════════════════
//  UI HELPERS
// ════════════════════════════════════════════════════

// ── 페이지 전환 ───────────────────────────────────────
let currentPage = 'list';
const pageMeta = {
    list:   { title: '대출 목록 조회', bc: '대출 관리 / 목록'  },
    rent:   { title: '대출 신청',      bc: '대출 관리 / 신청'  },
    return: { title: '반납 처리',      bc: '대출 관리 / 반납'  },
    stats:  { title: '인기 도서 통계', bc: '통계 / 인기 도서' },
};

function showPage(name) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-' + name).classList.add('active');
    document.querySelectorAll('.sb-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.sb-btn')[['list','rent','return','stats'].indexOf(name)].classList.add('active');
    document.getElementById('topbar-title').textContent = pageMeta[name].title;
    document.getElementById('topbar-bc').textContent    = pageMeta[name].bc;
    currentPage = name;
}

function refreshCurrentPage() {
    if (currentPage === 'list') {
        if (document.getElementById('list-member-id').value) loadRentals();
    } else if (currentPage === 'stats') {
        loadStats();
    }
}

// ── 토스트 알림 ───────────────────────────────────────
function toast(msg, type = 'success') {
    const icons = {
        success: 'bi-check-circle-fill',
        error:   'bi-x-circle-fill',
        warn:    'bi-exclamation-circle-fill',
    };
    const el = document.createElement('div');
    el.className = `toast-msg t-${type}`;
    el.innerHTML = `<i class="bi ${icons[type]}"></i> ${msg}`;
    document.getElementById('toast-container').appendChild(el);
    setTimeout(() => el.remove(), 3200);
}

// ── 인라인 알림 박스 ──────────────────────────────────
function showAlert(id, msg, type = 'success') {
    const icons = {
        success: 'bi-check-circle-fill',
        error:   'bi-x-circle-fill',
        warn:    'bi-exclamation-triangle-fill',
    };
    const el = document.getElementById(id);
    el.className = `alert-box show alert-${type}`;
    el.innerHTML = `<i class="bi ${icons[type]}"></i> ${msg}`;
    setTimeout(() => el.classList.remove('show'), 4500);
}

// ── 폼 초기화 ─────────────────────────────────────────
function clearForm(type) {
    if (type === 'rent') {
        document.getElementById('rent-member-id').value = '';
        document.getElementById('rent-book-id').value   = '';
        document.getElementById('rent-alert').classList.remove('show');
    } else {
        document.getElementById('return-rental-id').value = '';
        document.getElementById('return-alert').classList.remove('show');
    }
}

// ── 버튼 로딩 상태 ────────────────────────────────────
function setLoading(btnId, loading, label = '') {
    const btn = document.getElementById(btnId);
    btn.disabled = loading;
    if (loading) {
        btn.innerHTML = `<span class="spinner-sm"></span> 처리 중…`;
    } else {
        btn.innerHTML = label;
    }
}


// ════════════════════════════════════════════════════
//  DATE / STATUS HELPERS
// ════════════════════════════════════════════════════

/** 오늘 기준 남은 일수 (음수 = 연체) */
function daysLeft(dueDateStr) {
    const due = new Date(dueDateStr);
    const now = new Date();
    due.setHours(0,0,0,0);
    now.setHours(0,0,0,0);
    return Math.round((due - now) / 86400000);
}

/** YYYY-MM-DD → YYYY.MM.DD */
function formatDate(str) {
    if (!str) return '—';
    return str.replace(/-/g, '.');
}

/** 상태 뱃지 HTML */
function statusBadge(r) {
    const days = daysLeft(r.dueDate);
    if (r.status === 'RETURNED')      return `<span class="badge-s bs-returned">반납 완료</span>`;
    if (days < 0)                     return `<span class="badge-s bs-overdue">연체 ${Math.abs(days)}일</span>`;
    if (r.renewCount > 0)             return `<span class="badge-s bs-renewed">연장 대출</span>`;
    return `<span class="badge-s bs-rented">대출 중</span>`;
}


// ════════════════════════════════════════════════════
//  PAGE: 대출 목록 조회
// ════════════════════════════════════════════════════
let allRentals = [];

/** GET /api/rentals/member/{memberId} */
async function loadRentals() {
    const id = document.getElementById('list-member-id').value.trim();
    if (!id) { toast('회원 ID를 입력하세요', 'warn'); return; }

    const tbody = document.getElementById('rental-tbody');
    tbody.innerHTML = `<tr><td colspan="9" style="text-align:center;padding:30px;">
    <span class="spinner-sm" style="width:20px;height:20px;border-width:3px;"></span>
  </td></tr>`;

    try {
        allRentals = await apiGet(`/member/${id}`);
        renderRentals(allRentals);
        updateStatCards(allRentals);
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="9"><div class="empty-state">
      <i class="bi bi-exclamation-circle"></i><p>${e.message}</p>
    </div></td></tr>`;
        toast('조회 실패: ' + e.message, 'error');
    }
}

/** 테이블 렌더링 */
function renderRentals(list) {
    const tbody = document.getElementById('rental-tbody');
    if (!list.length) {
        tbody.innerHTML = `<tr><td colspan="9"><div class="empty-state">
      <i class="bi bi-inbox"></i><p>대출 내역이 없습니다</p>
    </div></td></tr>`;
        document.getElementById('list-count').textContent = '0건';
        return;
    }
    document.getElementById('list-count').textContent = `총 ${list.length}건`;

    tbody.innerHTML = list.map(r => {
        const days = daysLeft(r.dueDate);
        const daysText =
            r.status === 'RETURNED'  ? '—' :
                days < 0                 ? `<span class="overdue-days">+${Math.abs(days)}일 연체</span>` :
                    days === 0               ? `<span style="color:var(--amber);font-weight:600;font-family:'IBM Plex Mono',monospace;font-size:0.78rem;">오늘 마감</span>` :
                        `<span class="td-mono">${days}일 남음</span>`;

        const actionBtns = r.status !== 'RETURNED'
            ? `<button class="btn-sm-action bsa-return me-1" onclick="quickReturn(${r.rentalId})">
           <i class="bi bi-box-arrow-up"></i> 반납
         </button>
         ${r.renewCount < 1
                ? `<button class="btn-sm-action bsa-renew" onclick="quickRenew(${r.rentalId})">
                <i class="bi bi-arrow-repeat"></i> 연장
              </button>`
                : ''}`
            : '<span style="font-size:0.75rem;color:var(--muted);">완료</span>';

        return `
    <tr onclick="openDetail(${r.rentalId})">
      <td class="td-mono" style="color:var(--muted);">#${r.rentalId}</td>
      <td class="td-book">${r.bookTitle  || '—'}</td>
      <td class="td-muted">${r.bookAuthor || '—'}</td>
      <td class="td-mono">${formatDate(r.rentalDate)}</td>
      <td class="td-mono">${formatDate(r.dueDate)}</td>
      <td>${daysText}</td>
      <td>${statusBadge(r)}</td>
      <td><span class="td-mono">${r.renewCount}/1</span></td>
      <td onclick="event.stopPropagation()" style="white-space:nowrap;">${actionBtns}</td>
    </tr>`;
    }).join('');
}

/** 필터 (키워드 + 상태) */
function filterTable() {
    const kw = document.getElementById('filter-keyword').value.toLowerCase();
    const st = document.getElementById('filter-status').value;

    const filtered = allRentals.filter(r => {
        const matchKw = !kw ||
            (r.bookTitle  || '').toLowerCase().includes(kw) ||
            (r.bookAuthor || '').toLowerCase().includes(kw);

        const days   = daysLeft(r.dueDate);
        let   status = r.status;
        if (status === 'RENTED' && days < 0) status = 'OVERDUE';
        const matchSt = !st || status === st;

        return matchKw && matchSt;
    });
    renderRentals(filtered);
}

/** 상단 통계 카드 갱신 */
function updateStatCards(list) {
    document.getElementById('st-total').textContent    = list.length;
    document.getElementById('st-overdue').textContent  = list.filter(r => r.status === 'RENTED' && daysLeft(r.dueDate) < 0).length;
    document.getElementById('st-duetoday').textContent = list.filter(r => r.status === 'RENTED' && daysLeft(r.dueDate) === 0).length;
    document.getElementById('st-renewed').textContent  = list.filter(r => r.renewCount > 0).length;
}


// ════════════════════════════════════════════════════
//  인라인 반납 / 연장 (목록 화면)
// ════════════════════════════════════════════════════

/** POST /api/rentals/return */
async function quickReturn(rentalId) {
    if (!confirm(`대출 #${rentalId} 도서를 반납 처리하시겠습니까?`)) return;
    try {
        await apiPost('/return', { rentalId });
        toast('반납 처리 완료!', 'success');
        loadRentals();
    } catch (e) {
        toast(e.message, 'error');
    }
}

/** POST /api/rentals/renew/{id} */
async function quickRenew(rentalId) {
    if (!confirm(`대출 #${rentalId} 도서를 연장(재대출)하시겠습니까?`)) return;
    try {
        await apiPost(`/renew/${rentalId}`, {});
        toast('연장 완료! 14일 추가됩니다.', 'success');
        loadRentals();
    } catch (e) {
        toast(e.message, 'error');
    }
}


// ════════════════════════════════════════════════════
//  대출 상세 모달
// ════════════════════════════════════════════════════
function openDetail(rentalId) {
    const r = allRentals.find(x => x.rentalId === rentalId);
    if (!r) return;

    const days = daysLeft(r.dueDate);
    const daysDisplay =
        r.status === 'RETURNED' ? '—' :
            days < 0 ? `<span style="color:var(--accent);font-weight:600;">${Math.abs(days)}일 연체</span>` :
                `${days}일 남음`;

    document.getElementById('detail-body').innerHTML = `
    <div class="detail-row"><span class="detail-key">대출 ID</span>    <span class="detail-val td-mono">#${r.rentalId}</span></div>
    <div class="detail-row"><span class="detail-key">도서명</span>     <span class="detail-val">${r.bookTitle  || '—'}</span></div>
    <div class="detail-row"><span class="detail-key">저자</span>       <span class="detail-val">${r.bookAuthor || '—'}</span></div>
    <div class="detail-row"><span class="detail-key">도서 ID</span>    <span class="detail-val td-mono">#${r.bookId}</span></div>
    <div class="detail-row"><span class="detail-key">회원 ID</span>    <span class="detail-val td-mono">#${r.memberId}</span></div>
    <div class="detail-row"><span class="detail-key">대출일</span>     <span class="detail-val td-mono">${formatDate(r.rentalDate)}</span></div>
    <div class="detail-row"><span class="detail-key">반납 기한</span>  <span class="detail-val td-mono">${formatDate(r.dueDate)}</span></div>
    <div class="detail-row"><span class="detail-key">반납일</span>     <span class="detail-val td-mono">${formatDate(r.returnDate)}</span></div>
    <div class="detail-row"><span class="detail-key">남은 일수</span>  <span class="detail-val">${daysDisplay}</span></div>
    <div class="detail-row"><span class="detail-key">상태</span>       <span class="detail-val">${statusBadge(r)}</span></div>
    <div class="detail-row"><span class="detail-key">재대출 횟수</span><span class="detail-val td-mono">${r.renewCount} / 1회</span></div>
  `;

    document.getElementById('detail-footer').innerHTML = r.status !== 'RETURNED'
        ? `<button class="btn-ghost" onclick="closeDetailPanel()">닫기</button>
       ${r.renewCount < 1
            ? `<button class="btn-ghost" style="color:var(--amber);border-color:var(--amber);"
              onclick="quickRenew(${r.rentalId});closeDetailPanel()">
              <i class="bi bi-arrow-repeat"></i> 연장
            </button>`
            : ''}
       <button class="btn-teal" onclick="quickReturn(${r.rentalId});closeDetailPanel()">
         <i class="bi bi-box-arrow-up"></i> 반납 처리
       </button>`
        : `<button class="btn-ink" onclick="closeDetailPanel()">닫기</button>`;

    document.getElementById('detail-overlay').classList.add('show');
}

function closeDetailPanel() {
    document.getElementById('detail-overlay').classList.remove('show');
}

function closeDetail(e) {
    if (e.target === document.getElementById('detail-overlay')) closeDetailPanel();
}


// ════════════════════════════════════════════════════
//  PAGE: 대출 신청
// ════════════════════════════════════════════════════

/** POST /api/rentals */
async function submitRent() {
    const memberId = parseInt(document.getElementById('rent-member-id').value);
    const bookId   = parseInt(document.getElementById('rent-book-id').value);
    if (!memberId || !bookId) {
        showAlert('rent-alert', '회원 ID와 도서 ID를 모두 입력하세요.', 'warn');
        return;
    }
    setLoading('rent-submit-btn', true);
    try {
        await apiPost('', { memberId, bookId });
        showAlert('rent-alert', `도서 #${bookId} 대출 완료! (반납 기한: 오늘로부터 14일)`, 'success');
        toast('대출 신청 완료!', 'success');
        clearForm('rent');
    } catch (e) {
        showAlert('rent-alert', e.message, 'error');
        toast(e.message, 'error');
    } finally {
        setLoading('rent-submit-btn', false, '<i class="bi bi-check2"></i> 대출 신청');
    }
}


// ════════════════════════════════════════════════════
//  PAGE: 반납 처리
// ════════════════════════════════════════════════════

/** POST /api/rentals/return */
async function submitReturn() {
    const rentalId = parseInt(document.getElementById('return-rental-id').value);
    if (!rentalId) {
        showAlert('return-alert', '대출 ID를 입력하세요.', 'warn');
        return;
    }
    setLoading('return-submit-btn', true);
    try {
        await apiPost('/return', { rentalId });
        showAlert('return-alert', `대출 #${rentalId} 반납 처리 완료!`, 'success');
        toast('반납 처리 완료!', 'success');
        clearForm('return');
    } catch (e) {
        showAlert('return-alert', e.message, 'error');
        toast(e.message, 'error');
    } finally {
        setLoading('return-submit-btn', false, '<i class="bi bi-check2"></i> 반납 처리');
    }
}

// ── 빠른 반납: 회원 대출 목록 조회 ──────────────────────
async function loadQuickReturn() {
    const id    = document.getElementById('quick-member-id').value.trim();
    const tbody = document.getElementById('quick-return-tbody');
    if (!id) return;

    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:20px;">
    <span class="spinner-sm" style="width:18px;height:18px;border-width:2px;"></span>
  </td></tr>`;

    try {
        const list = await apiGet(`/member/${id}`);
        if (!list.length) {
            tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state" style="padding:24px;">
        <i class="bi bi-inbox"></i><p>대출 중인 도서가 없습니다</p>
      </div></td></tr>`;
            return;
        }
        tbody.innerHTML = list.map(r => `
      <tr>
        <td class="td-mono" style="color:var(--muted);">#${r.rentalId}</td>
        <td class="td-book" style="font-size:0.82rem;">${r.bookTitle || '—'}</td>
        <td class="td-mono">${formatDate(r.dueDate)}</td>
        <td>${statusBadge(r)}</td>
        <td>
          <button class="btn-sm-action bsa-return"
            onclick="quickReturnAndRefresh(${r.rentalId}, '${id}')">
            <i class="bi bi-box-arrow-up"></i> 반납
          </button>
        </td>
      </tr>`).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state">
      <i class="bi bi-exclamation-circle"></i><p>${e.message}</p>
    </div></td></tr>`;
    }
}

async function quickReturnAndRefresh(rentalId, memberId) {
    if (!confirm(`대출 #${rentalId} 도서를 반납 처리하시겠습니까?`)) return;
    try {
        await apiPost('/return', { rentalId });
        toast('반납 처리 완료!', 'success');
        document.getElementById('quick-member-id').value = memberId;
        loadQuickReturn();
    } catch (e) {
        toast(e.message, 'error');
    }
}


// ════════════════════════════════════════════════════
//  PAGE: 인기 도서 통계
// ════════════════════════════════════════════════════

/** GET /api/rentals/stats */
async function loadStats() {
    const tbody = document.getElementById('stats-tbody');
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:30px;">
    <span class="spinner-sm" style="width:20px;height:20px;border-width:3px;"></span>
  </td></tr>`;

    try {
        const list = await apiGet('/stats');
        if (!list.length) {
            tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state">
        <i class="bi bi-inbox"></i><p>데이터가 없습니다</p>
      </div></td></tr>`;
            return;
        }
        const max = list[0][2] || 1;
        const medals = ['🥇', '🥈', '🥉'];

        tbody.innerHTML = list.map(([bookId, title, count], i) => {
            const pct    = Math.round((count / max) * 100);
            const medal  = medals[i] ?? `${i + 1}`;
            const color  = i === 0 ? 'var(--gold)' : i <= 2 ? 'var(--accent)' : 'var(--teal)';
            return `
      <tr>
        <td style="font-weight:700;font-size:0.9rem;text-align:center;">${medal}</td>
        <td class="td-mono" style="color:var(--muted);">#${bookId}</td>
        <td style="font-weight:500;">${title}</td>
        <td>
          <span style="font-family:'IBM Plex Mono',monospace;font-size:0.9rem;font-weight:600;">${count}</span>
          <span style="font-size:0.72rem;color:var(--muted);margin-left:4px;">회</span>
        </td>
        <td>
          <div style="display:flex;align-items:center;gap:8px;">
            <div style="flex:1;height:6px;background:var(--paper-dark);border-radius:3px;overflow:hidden;">
              <div style="height:100%;width:${pct}%;background:${color};border-radius:3px;transition:width 0.5s;"></div>
            </div>
            <span style="font-family:'IBM Plex Mono',monospace;font-size:0.7rem;color:var(--muted);min-width:30px;">${pct}%</span>
          </div>
        </td>
      </tr>`;
        }).join('');
    } catch (e) {
        toast('통계 로드 실패: ' + e.message, 'error');
    }
}


// ════════════════════════════════════════════════════
//  INIT
// ════════════════════════════════════════════════════
(function init() {
    const d = new Date();
    const days = ['일','월','화','수','목','금','토'];
    document.getElementById('topbar-date').textContent =
        `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} ${days[d.getDay()]}요일`;
})();