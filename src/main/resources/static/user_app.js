const API_BASE  = '/api/rentals';
const MOCK_MODE = true; // false → 실제 API 호출

// ┌─────────────────────────────────────────────────────┐
// │  ⚠️  로그인 연동 시 아래 세 줄만 수정하세요!        │
// └─────────────────────────────────────────────────────┘
// 이렇게 되어있어야 해요
const CURRENT_MEMBER_ID   = 1001;
const CURRENT_MEMBER_NAME = '김도서';
const CURRENT_ROLE        = 'ADMIN';


// // 나중에 로그인 연동 후 (예시)
// const CURRENT_MEMBER_ID   = session.memberId;  // 로그인 팀원이 알려준 방식으로
// const CURRENT_MEMBER_NAME = session.mname;     // 로그인 팀원이 알려준 방식으로
// const CURRENT_MEMBER_ROLE = session.role;     // 로그인 팀원이 알려준 방식으로


// ── MOCK DATA ─────────────────────────────────────────
function daysFromNow(d) {
    const t = new Date();
    t.setDate(t.getDate() + d);
    return t.toISOString().split('T')[0];
}

let mockRentals = [
    { rentalId:101, bookId:3,  bookTitle:'채식주의자',    bookAuthor:'한강',      memberId:1001, rentalDate:daysFromNow(-10), dueDate:daysFromNow(4),  returnDate:null, status:'RENTED', renewCount:0 },
    { rentalId:102, bookId:7,  bookTitle:'82년생 김지영', bookAuthor:'조남주',    memberId:1001, rentalDate:daysFromNow(-16), dueDate:daysFromNow(-2), returnDate:null, status:'RENTED', renewCount:0 },
    { rentalId:103, bookId:12, bookTitle:'아몬드',        bookAuthor:'손원평',    memberId:1001, rentalDate:daysFromNow(-20), dueDate:daysFromNow(-6), returnDate:null, status:'RENTED', renewCount:1 },
];
let nextId = 200;


// ── API HELPERS ───────────────────────────────────────
async function apiGet(url) {
    if (MOCK_MODE) return mockGet(url);
    const res = await fetch(API_BASE + url);
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

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

function mockGet(url) {
    if (url.startsWith('/member/')) {
        const id = parseInt(url.split('/member/')[1]);
        return Promise.resolve(
            mockRentals.filter(r => r.memberId === id && r.status === 'RENTED')
        );
    }
    return Promise.resolve([]);
}

function mockPost(url, body) {
    // POST /api/rentals/return  → 반납
    if (url === '/return') {
        const r = mockRentals.find(r => r.rentalId === body.rentalId);
        if (!r)                      return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.status === 'RETURNED') return Promise.reject(new Error('이미 반납된 도서입니다.'));
        r.status     = 'RETURNED';
        r.returnDate = new Date().toISOString().split('T')[0];
        return Promise.resolve('반납 완료');
    }
    // POST /api/rentals/renew/{id}  → 연장
    if (url.startsWith('/renew/')) {
        const r = mockRentals.find(r => r.rentalId === parseInt(url.split('/renew/')[1]));
        if (!r)                return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.renewCount >= 1) return Promise.reject(new Error('연장은 1회만 가능합니다.'));
        r.renewCount++;
        const due = new Date(r.dueDate);
        due.setDate(due.getDate() + 14);
        r.dueDate = due.toISOString().split('T')[0];
        return Promise.resolve('재대출 완료');
    }
}


// ── UI HELPERS ────────────────────────────────────────
function toast(msg, type = 'success') {
    const icons = { success: 'bi-check-circle-fill', error: 'bi-x-circle-fill', warn: 'bi-exclamation-circle-fill' };
    const el = document.createElement('div');
    el.className = `toast t-${type}`;
    el.innerHTML = `<i class="bi ${icons[type]}"></i> ${msg}`;
    document.getElementById('toast-wrap').appendChild(el);
    setTimeout(() => el.remove(), 3000);
}

function daysLeft(str) {
    const due = new Date(str), now = new Date();
    due.setHours(0,0,0,0); now.setHours(0,0,0,0);
    return Math.round((due - now) / 86400000);
}

function formatDate(str) {
    return str ? str.replace(/-/g, '.') : '—';
}


// ── 대출 목록 로드 & 렌더링 ───────────────────────────
let myRentals = [];

async function loadMyRentals() {
    const listEl = document.getElementById('rental-list');
    listEl.innerHTML = `<div class="empty-state">
    <span class="spinner" style="width:24px;height:24px;border-width:3px;color:var(--accent);"></span>
  </div>`;
    try {
        myRentals = await apiGet(`/member/${CURRENT_MEMBER_ID}`);
        renderRentals(myRentals);
        updateStats(myRentals);
    } catch (e) {
        listEl.innerHTML = `<div class="empty-state">
      <i class="bi bi-exclamation-circle"></i><p>${e.message}</p>
    </div>`;
    }
}

function renderRentals(list) {
    const el = document.getElementById('rental-list');
    if (!list.length) {
        el.innerHTML = `<div class="empty-state"><i class="bi bi-inbox"></i><p>현재 대출 중인 도서가 없어요</p></div>`;
        return;
    }
    el.innerHTML = list.map(r => {
        const days      = daysLeft(r.dueDate);
        const isOver    = days < 0;
        const isSoon    = !isOver && days <= 3;
        const cardCls   = isOver ? 'overdue' : isSoon ? 'due-soon' : '';
        const badgeText = isOver ? `${Math.abs(days)}일 연체` : days === 0 ? '오늘 마감' : `${days}일 남음`;
        const badgeCls  = isOver ? 'due-over' : isSoon ? 'due-warn' : 'due-ok';
        const elapsed   = 14 - Math.max(0, days);
        const pct       = Math.min(100, Math.round((elapsed / 14) * 100));
        const fillColor = isOver ? 'var(--danger)' : isSoon ? 'var(--warn)' : 'var(--accent2)';

        // 연장 버튼: [연장] → 클릭 시 [확인][취소] 로 전환
        const renewArea = r.renewCount < 1
            ? `<button class="btn-action ba-renew"
           onclick="event.stopPropagation();showRenewConfirm(${r.rentalId})">
           <i class="bi bi-arrow-repeat"></i> 연장
         </button>`
            : `<span style="font-size:0.72rem;color:var(--text-mid);">연장 완료</span>`;

        return `
    <div class="rental-card ${cardCls}" onclick="openModal(${r.rentalId})">
      <div class="book-icon"><i class="bi bi-book"></i></div>
      <div class="rental-info">
        <div class="rental-title">${r.bookTitle || '—'}</div>
        <div class="rental-author">${r.bookAuthor || '—'}</div>
        <div class="rental-dates">대출 ${formatDate(r.rentalDate)} → 반납 기한 ${formatDate(r.dueDate)}</div>
        <div class="due-progress">
          <div class="due-bar">
            <div class="due-fill" style="width:${pct}%;background:${fillColor};"></div>
          </div>
        </div>
      </div>
      <div class="rental-right">
        <div class="due-badge ${badgeCls}">${badgeText}</div>
        <div class="rental-actions" id="renew-area-${r.rentalId}" onclick="event.stopPropagation()">
          ${renewArea}
        </div>
      </div>
    </div>`;
    }).join('');
}

function updateStats(list) {
    document.getElementById('st-rented').textContent    = list.length;
    document.getElementById('st-overdue').textContent   = list.filter(r => daysLeft(r.dueDate) < 0).length;
    document.getElementById('st-renewable').textContent = list.filter(r => r.renewCount < 1).length;
}


// ── 연장 2단계 확인 ───────────────────────────────────

// 1단계: [연장] 클릭 → [확인][취소] 로 교체
function showRenewConfirm(rentalId) {
    const area = document.getElementById(`renew-area-${rentalId}`);
    if (!area) return;
    area.innerHTML = `
    <span style="font-size:0.72rem;color:var(--text-mid);margin-right:4px;">연장할까요?</span>
    <button class="btn-action ba-renew"
      onclick="event.stopPropagation();renewBook(${rentalId})">
      <i class="bi bi-check-lg"></i> 확인
    </button>
    <button class="btn-action" style="background:rgba(255,255,255,0.06);color:var(--text-mid);"
      onclick="event.stopPropagation();cancelRenew(${rentalId})">
      취소
    </button>
  `;
}

// 취소: 원래 [연장] 버튼으로 복원
function cancelRenew(rentalId) {
    const area = document.getElementById(`renew-area-${rentalId}`);
    if (!area) return;
    area.innerHTML = `
    <button class="btn-action ba-renew"
      onclick="event.stopPropagation();showRenewConfirm(${rentalId})">
      <i class="bi bi-arrow-repeat"></i> 연장
    </button>
  `;
}

// 2단계: [확인] 클릭 → POST /api/rentals/renew/{id}
async function renewBook(rentalId) {
    try {
        await apiPost(`/renew/${rentalId}`, {});
        toast('연장이 완료되었습니다! (+14일)', 'success');
        loadMyRentals();
    } catch (e) {
        toast(e.message, 'error');
        loadMyRentals();
    }
}


// ── 상세 모달 ─────────────────────────────────────────
function openModal(rentalId) {
    const r = myRentals.find(x => x.rentalId === rentalId);
    if (!r) return;
    const days        = daysLeft(r.dueDate);
    const daysDisplay = days < 0
        ? `<span style="color:var(--danger);font-weight:600;">${Math.abs(days)}일 연체</span>`
        : `${days}일 남음`;

    document.getElementById('modal-body').innerHTML = `
    <div class="modal-row"><span class="modal-key">도서명</span>    <span class="modal-val">${r.bookTitle  || '—'}</span></div>
    <div class="modal-row"><span class="modal-key">저자</span>      <span class="modal-val">${r.bookAuthor || '—'}</span></div>
    <div class="modal-row"><span class="modal-key">대출일</span>    <span class="modal-val" style="font-family:'IBM Plex Mono',monospace;font-size:0.8rem;">${formatDate(r.rentalDate)}</span></div>
    <div class="modal-row"><span class="modal-key">반납 기한</span> <span class="modal-val" style="font-family:'IBM Plex Mono',monospace;font-size:0.8rem;">${formatDate(r.dueDate)}</span></div>
    <div class="modal-row"><span class="modal-key">남은 일수</span> <span class="modal-val">${daysDisplay}</span></div>
    <div class="modal-row"><span class="modal-key">연장 횟수</span> <span class="modal-val" style="font-family:'IBM Plex Mono',monospace;">${r.renewCount} / 1회</span></div>
  `;
    document.getElementById('modal-footer').innerHTML = `
    <button class="btn-ghost" onclick="closeModalPanel()">닫기</button>
    ${r.renewCount < 1
        ? `<button class="btn-primary" onclick="renewBook(${r.rentalId});closeModalPanel()">
           <i class="bi bi-arrow-repeat"></i> 연장하기
         </button>`
        : `<span style="font-size:0.78rem;color:var(--text-mid);">이미 연장한 도서입니다</span>`}
  `;
    document.getElementById('modal-overlay').classList.add('show');
}

function closeModalPanel() {
    document.getElementById('modal-overlay').classList.remove('show');
}

function closeModal(e) {
    if (e.target === document.getElementById('modal-overlay')) closeModalPanel();
}


// ── INIT ──────────────────────────────────────────────
(function init() {
    document.getElementById('username-display').textContent = CURRENT_MEMBER_NAME;
    document.getElementById('greeting-name').textContent    = CURRENT_MEMBER_NAME;

    // ADMIN이면 관리자 페이지 링크 표시
    if (CURRENT_ROLE === 'ADMIN') {
        document.getElementById('admin-link').style.display = 'flex';
    }

    loadMyRentals();
})();