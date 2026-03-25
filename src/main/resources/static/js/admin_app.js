// ════════════════════════════════════════════════════
//  admin_app.js  —  도서관 대출/반납 관리 프론트엔드
// ════════════════════════════════════════════════════

// ── CONFIG ────────────────────────────────────────────
const API_BASE    = '/api/rentals';
const REQUEST_API = '/api/requests';

const MOCK_MODE = false;


// ════════════════════════════════════════════════════
//  MOCK DATA  (MOCK_MODE = true 일 때만 사용)
// ════════════════════════════════════════════════════
function daysFromNow(d) {
    const t = new Date();
    t.setDate(t.getDate() + d);
    return t.toISOString().split('T')[0];
}

let mockRentals = [
    { rentalId:101, bookId:3,  bookTitle:'채식주의자',    bookAuthor:'한강',        memberId:1001, rentalDate:daysFromNow(-10), dueDate:daysFromNow(4),  returnDate:null, status:'RENTED', renewCount:0 },
    { rentalId:102, bookId:7,  bookTitle:'82년생 김지영', bookAuthor:'조남주',      memberId:1001, rentalDate:daysFromNow(-16), dueDate:daysFromNow(-2), returnDate:null, status:'RENTED', renewCount:0 },
    { rentalId:103, bookId:12, bookTitle:'아몬드',        bookAuthor:'손원평',      memberId:1001, rentalDate:daysFromNow(-20), dueDate:daysFromNow(-6), returnDate:null, status:'RENTED', renewCount:1 },
    { rentalId:104, bookId:5,  bookTitle:'데미안',        bookAuthor:'헤르만 헤세', memberId:1002, rentalDate:daysFromNow(-8),  dueDate:daysFromNow(6),  returnDate:null, status:'RENTED', renewCount:0 },
    { rentalId:105, bookId:9,  bookTitle:'어린 왕자',     bookAuthor:'생텍쥐페리',  memberId:1002, rentalDate:daysFromNow(-14), dueDate:daysFromNow(0),  returnDate:null, status:'RENTED', renewCount:0 },
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
//  API HELPERS
// ════════════════════════════════════════════════════
async function apiGet(url) {
    if (MOCK_MODE) return mockGet(url);
    const res = await fetch(API_BASE + url);
    if (!res.ok) {
        const buffer = await res.arrayBuffer();
        const text = new TextDecoder('utf-8').decode(buffer);
        throw new Error(text);
    }
    return res.json();
}

async function apiPost(url, body) {
    if (MOCK_MODE) return mockPost(url, body);
    const res = await fetch(API_BASE + url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });
    if (!res.ok) {
        // ← 이렇게 변경! UTF-8로 디코딩
        const buffer = await res.arrayBuffer();
        const text = new TextDecoder('utf-8').decode(buffer);
        throw new Error(text);
    }
    return res.text();
}

function mockGet(url) {
    if (url.startsWith('/member/')) {
        const id = parseInt(url.split('/member/')[1]);
        return Promise.resolve(mockRentals.filter(r => r.memberId === id && r.status === 'RENTED'));
    }
    if (url === '/stats') return Promise.resolve([...mockStats]);
    return Promise.resolve([]);
}

function mockPost(url, body) {
    if (url === '') {
        const todayStr   = new Date().toISOString().split('T')[0];
        const todayCount = mockRentals.filter(r => r.memberId === body.memberId && r.rentalDate === todayStr).length;
        if (todayCount >= 3) return Promise.reject(new Error('하루 최대 3권까지 대출 가능합니다.'));
        if (mockRentals.find(r => r.bookId === body.bookId && r.status === 'RENTED'))
            return Promise.reject(new Error('이미 대출된 책입니다.'));
        const due = new Date(); due.setDate(due.getDate() + 14);
        mockRentals.push({ rentalId: nextRentalId++, bookId: body.bookId, bookTitle: `도서 #${body.bookId}`,
            bookAuthor: '저자명', memberId: body.memberId, rentalDate: todayStr,
            dueDate: due.toISOString().split('T')[0], returnDate: null, status: 'RENTED', renewCount: 0 });
        return Promise.resolve('대출 완료');
    }
    if (url === '/return') {
        const r = mockRentals.find(r => r.rentalId === body.rentalId);
        if (!r)                      return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.status === 'RETURNED') return Promise.reject(new Error('이미 반납된 도서입니다.'));
        r.status = 'RETURNED'; r.returnDate = new Date().toISOString().split('T')[0];
        return Promise.resolve('반납 완료');
    }
    if (url.startsWith('/renew/')) {
        const r = mockRentals.find(r => r.rentalId === parseInt(url.split('/renew/')[1]));
        if (!r)              return Promise.reject(new Error('대출 정보가 없습니다.'));
        if (r.renewCount >= 1) return Promise.reject(new Error('재대출은 1회만 가능합니다.'));
        r.renewCount++;
        const due = new Date(r.dueDate); due.setDate(due.getDate() + 14);
        r.dueDate = due.toISOString().split('T')[0];
        return Promise.resolve('재대출 완료');
    }
}


// ════════════════════════════════════════════════════
//  UI HELPERS
// ════════════════════════════════════════════════════
let currentPage = 'list';
const pageMeta = {
    list:    { title: '대출 목록 조회', bc: '대출 관리 / 목록' },
    rent:    { title: '대출 신청',      bc: '대출 관리 / 신청' },
    request: { title: '대출 예약',      bc: '대출 관리 / 예약' }, // ← 추가
    return:  { title: '반납 처리',      bc: '대출 관리 / 반납' },
    stats:   { title: '인기 도서 통계', bc: '통계 / 인기 도서' },
};

function showPage(name) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-' + name).classList.add('active');
    document.querySelectorAll('.sb-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.sb-btn')[['list','rent','request','return','stats'].indexOf(name)].classList.add('active');
    document.getElementById('topbar-title').textContent = pageMeta[name].title;
    document.getElementById('topbar-bc').textContent    = pageMeta[name].bc;
    currentPage = name;

    // 대출 예약 탭 이동 시 자동 로드
    if (name === 'request') loadRequests();
}

function refreshCurrentPage() {
    if (currentPage === 'list') {
        if (document.getElementById('list-member-id').value) loadRentals();
    } else if (currentPage === 'stats') {
        loadStats();
    } else if (currentPage === 'request') {
        loadRequests();
    }
}

function toast(msg, type = 'success') {
    const icons = { success: 'bi-check-circle-fill', error: 'bi-x-circle-fill', warn: 'bi-exclamation-circle-fill' };
    const el = document.createElement('div');
    el.className = `toast-msg t-${type}`;
    el.innerHTML = `<i class="bi ${icons[type]}"></i> ${msg}`;
    document.getElementById('toast-container').appendChild(el);
    setTimeout(() => el.remove(), 3200);
}

function showAlert(id, msg, type = 'success') {
    const icons = { success: 'bi-check-circle-fill', error: 'bi-x-circle-fill', warn: 'bi-exclamation-triangle-fill' };
    const el = document.getElementById(id);
    el.className = `alert-box show alert-${type}`;
    el.innerHTML = `<i class="bi ${icons[type]}"></i> ${msg}`;
    setTimeout(() => el.classList.remove('show'), 4500);
}

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

function setLoading(btnId, loading, label = '') {
    const btn = document.getElementById(btnId);
    btn.disabled = loading;
    btn.innerHTML = loading ? `<span class="spinner-sm"></span> 처리 중…` : label;
}


// ════════════════════════════════════════════════════
//  DATE / STATUS HELPERS
// ════════════════════════════════════════════════════
function daysLeft(dueDateStr) {
    const due = new Date(dueDateStr), now = new Date();
    due.setHours(0,0,0,0); now.setHours(0,0,0,0);
    return Math.round((due - now) / 86400000);
}

/** YYYY-MM-DD 또는 [YYYY, MM, DD] → YYYY.MM.DD */
function formatDate(str) {
    if (!str) return '—';

    // 배열 형태로 오는 경우 [2026, 3, 20]
    if (Array.isArray(str)) {
        const [y, m, d] = str;
        return `${y}.${String(m).padStart(2,'0')}.${String(d).padStart(2,'0')}`;
    }

    // 문자열 형태로 오는 경우 "2026-03-20"
    return str.replace(/-/g, '.');
}

function statusBadge(r) {
    const days = daysLeft(r.dueDate);
    if (r.status === 'RETURNED') return `<span class="badge-s bs-returned">반납 완료</span>`;
    if (days < 0)                return `<span class="badge-s bs-overdue">연체 ${Math.abs(days)}일</span>`;
    if (r.renewCount > 0)        return `<span class="badge-s bs-renewed">연장 대출</span>`;
    return `<span class="badge-s bs-rented">대출 중</span>`;
}


// ════════════════════════════════════════════════════
//  PAGE: 대출 목록 조회
// ════════════════════════════════════════════════════
let allRentals = [];

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
        const daysText = r.status === 'RETURNED' ? '—'
            : days < 0   ? `<span class="overdue-days">+${Math.abs(days)}일 연체</span>`
                : days === 0 ? `<span style="color:var(--amber);font-weight:600;font-family:'IBM Plex Mono',monospace;font-size:0.78rem;">오늘 마감</span>`
                    : `<span class="td-mono">${days}일 남음</span>`;
        const actionBtns = r.status !== 'RETURNED'
            ? `<button class="btn-sm-action bsa-return me-1" onclick="quickReturn(${r.rentalId})">
                   <i class="bi bi-box-arrow-up"></i> 반납
               </button>
               ${r.renewCount < 1
                ? `<button class="btn-sm-action bsa-renew" onclick="quickRenew(${r.rentalId})">
                          <i class="bi bi-arrow-repeat"></i> 연장
                      </button>` : ''}`
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

function filterTable() {
    const kw = document.getElementById('filter-keyword').value.toLowerCase();
    const st = document.getElementById('filter-status').value;
    renderRentals(allRentals.filter(r => {
        const matchKw = !kw || (r.bookTitle||'').toLowerCase().includes(kw) || (r.bookAuthor||'').toLowerCase().includes(kw);
        let status = r.status;
        if (status === 'RENTED' && daysLeft(r.dueDate) < 0) status = 'OVERDUE';
        return matchKw && (!st || status === st);
    }));
}

function updateStatCards(list) {
    document.getElementById('st-total').textContent    = list.length;
    document.getElementById('st-overdue').textContent  = list.filter(r => r.status === 'RENTED' && daysLeft(r.dueDate) < 0).length;
    document.getElementById('st-duetoday').textContent = list.filter(r => r.status === 'RENTED' && daysLeft(r.dueDate) === 0).length;
    document.getElementById('st-renewed').textContent  = list.filter(r => r.renewCount > 0).length;
}


// ════════════════════════════════════════════════════
//  인라인 반납 / 연장
// ════════════════════════════════════════════════════
async function quickReturn(rentalId) {
    if (!confirm(`대출 #${rentalId} 도서를 반납 처리하시겠습니까?`)) return;
    try { await apiPost('/return', { rentalId }); toast('반납 처리 완료!', 'success'); loadRentals(); }
    catch (e) { toast(e.message, 'error'); }
}

async function quickRenew(rentalId) {
    if (!confirm(`대출 #${rentalId} 도서를 연장(재대출)하시겠습니까?`)) return;
    try { await apiPost(`/renew/${rentalId}`, {}); toast('연장 완료! 14일 추가됩니다.', 'success'); loadRentals(); }
    catch (e) { toast(e.message, 'error'); }
}


// ════════════════════════════════════════════════════
//  대출 상세 모달
// ════════════════════════════════════════════════════
function openDetail(rentalId) {
    const r = allRentals.find(x => x.rentalId === rentalId);
    if (!r) return;
    const days = daysLeft(r.dueDate);
    const daysDisplay = r.status === 'RETURNED' ? '—'
        : days < 0 ? `<span style="color:var(--accent);font-weight:600;">${Math.abs(days)}일 연체</span>`
            : `${days}일 남음`;
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
                  </button>` : ''}
           <button class="btn-teal" onclick="quickReturn(${r.rentalId});closeDetailPanel()">
               <i class="bi bi-box-arrow-up"></i> 반납 처리
           </button>`
        : `<button class="btn-ink" onclick="closeDetailPanel()">닫기</button>`;
    document.getElementById('detail-overlay').classList.add('show');
}

function closeDetailPanel() { document.getElementById('detail-overlay').classList.remove('show'); }
function closeDetail(e) { if (e.target === document.getElementById('detail-overlay')) closeDetailPanel(); }


// ════════════════════════════════════════════════════
//  PAGE: 대출 신청
// ════════════════════════════════════════════════════
async function submitRent() {
    const memberId = parseInt(document.getElementById('rent-member-id').value);
    const bookId   = parseInt(document.getElementById('rent-book-id').value);
    if (!memberId || !bookId) { showAlert('rent-alert', '회원 ID와 도서 ID를 모두 입력하세요.', 'warn'); return; }
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
async function submitReturn() {
    const rentalId = parseInt(document.getElementById('return-rental-id').value);
    if (!rentalId) { showAlert('return-alert', '대출 ID를 입력하세요.', 'warn'); return; }
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
    } catch (e) { toast(e.message, 'error'); }
}


// ════════════════════════════════════════════════════
//  PAGE: 대출 예약
// ════════════════════════════════════════════════════

/** GET /api/requests — PENDING 예약 목록 조회 */
async function loadRequests() {
    const tbody = document.getElementById('request-tbody');
    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:30px;">
        <span class="spinner-sm" style="width:20px;height:20px;border-width:3px;"></span>
    </td></tr>`;
    try {
        const res  = await fetch(REQUEST_API);
        if (!res.ok) throw new Error(await res.text());
        const list = await res.json();

        if (!list.length) {
            tbody.innerHTML = `<tr><td colspan="8"><div class="empty-state">
                <i class="bi bi-bell"></i><p>대출 예약 신청이 없습니다</p>
            </div></td></tr>`;
            return;
        }
        tbody.innerHTML = list.map(r => `
        <tr>
            <td class="td-mono" style="color:var(--muted);">#${r.requestId}</td>
            <td class="td-mono">#${r.memberId}</td>
            <td>${r.memberName || '—'}</td>
            <td class="td-book">${r.bookTitle  || '—'}</td>
            <td class="td-muted">${r.bookAuthor || '—'}</td>
            <td class="td-mono">${formatDate(r.requestDate)}</td>
            <td><span class="badge-s bs-pending">대기 중</span></td>
            <td style="white-space:nowrap;">
                <button class="btn-sm-action bsa-return me-1"
                    onclick="approveRequest(${r.requestId})">
                    <i class="bi bi-check-lg"></i> 승인
                </button>
                <button class="btn-sm-action bsa-detail"
                    onclick="rejectRequest(${r.requestId})">
                    <i class="bi bi-x-lg"></i> 거절
                </button>
            </td>
        </tr>`).join('');
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8"><div class="empty-state">
            <i class="bi bi-exclamation-circle"></i><p>${e.message}</p>
        </div></td></tr>`;
        toast('예약 목록 로드 실패: ' + e.message, 'error');
    }
}

/** POST /api/requests/{id}/approve — 승인 */
async function approveRequest(requestId) {
    if (!confirm(`신청 #${requestId}을 승인하시겠습니까?\n승인 시 바로 대출 처리됩니다.`)) return;
    try {
        const res = await fetch(`${REQUEST_API}/${requestId}/approve`, { method: 'POST' });
        if (!res.ok) throw new Error(await res.text());
        toast('승인 완료! 대출 처리되었습니다.', 'success');
        loadRequests();
    } catch (e) {
        toast(e.message, 'error');
    }
}

/** POST /api/requests/{id}/reject — 거절 */
async function rejectRequest(requestId) {
    if (!confirm(`신청 #${requestId}을 거절하시겠습니까?`)) return;
    try {
        const res = await fetch(`${REQUEST_API}/${requestId}/reject`, { method: 'POST' });
        if (!res.ok) throw new Error(await res.text());
        toast('거절 처리되었습니다.', 'success');
        loadRequests();
    } catch (e) {
        toast(e.message, 'error');
    }
}


// ════════════════════════════════════════════════════
//  PAGE: 인기 도서 통계
// ════════════════════════════════════════════════════
async function loadStats() {
    const tbody = document.getElementById('stats-tbody');
    tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:30px;">
        <span class="spinner-sm" style="width:20px;height:20px;border-width:3px;"></span>
    </td></tr>`;
    try {
        const list = await apiGet('/stats');

        // 백엔드 구조 확인용
        console.log('stats data:', list[0]);

        if (!list.length) {
            tbody.innerHTML = `<tr><td colspan="4"><div class="empty-state">
                <i class="bi bi-inbox"></i><p>데이터가 없습니다</p>
            </div></td></tr>`;
            return;
        }
        const medals = ['🥇', '🥈', '🥉'];
        tbody.innerHTML = list.map((row, i) => {
            const title  = row[0];  // ← 도서명 (첫 번째)
            const count  = row[1];  // ← 대출 횟수 (두 번째)
            const medal  = medals[i] ?? `${i + 1}`;
            return `
    <tr>
        <td style="font-weight:700;font-size:0.9rem;text-align:center;">${medal}</td>
        <td style="font-weight:500;">${title || '—'}</td>
        <td>
            <span style="font-family:'IBM Plex Mono',monospace;font-size:0.9rem;font-weight:600;">${count}</span>
            <span style="font-size:0.72rem;color:var(--muted);margin-left:4px;">회</span>
        </td>
    </tr>`;
        }).join('');
    } catch (e) {
        toast('통계 로드 실패: ' + e.message, 'error');
    }
}

// ════════════════════════════════════════════════════
//  회원 검색
// ════════════════════════════════════════════════════
/** 회원 선택 → 대출 목록 조회 자동 입력 */
// ════════════════════════════════════════════════════
//  회원 검색 (수정된 버전)
// ════════════════════════════════════════════════════
async function searchMembers() {
    // 1. input에서 값을 가져올 때 trim()으로 앞뒤 공백 및 불필요한 문자 제거
    const keywordInput = document.getElementById('member-search-keyword');
    const keyword = keywordInput.value.trim();

    if (!keyword) {
        toast('아이디를 입력하세요', 'warn');
        return;
    }

    const resultWrap = document.getElementById('member-search-result');
    const tbody      = document.getElementById('member-search-tbody');

    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:20px;">
        <span class="spinner-sm" style="width:18px;height:18px;border-width:2px;"></span>
    </td></tr>`;
    resultWrap.style.display = 'block';

    try {
        // 주소를 조합할 때 백틱(`)을 사용하고, 경로 앞에 슬래시(/)가 하나만 있는지 확인
        const url = `/member/searchByMid?mid=${encodeURIComponent(keyword)}`;
        console.log("요청 URL:", url); // 브라우저 콘솔에서 :1이 붙는지 여기서 확인 가능

        const res = await fetch(url);

        if (!res.ok) {
            throw new Error(`서버 응답 오류 (상태코드: ${res.status})`);
        }

        const data = await res.json();

        if (!data || data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state" style="padding:24px;">
                <i class="bi bi-person-x"></i><p>해당 아이디의 회원이 없습니다</p>
            </div></td></tr>`;
            return;
        }

        tbody.innerHTML = data.map(m => `
        <tr>
            <td class="td-mono" style="color:var(--muted);">#${m.id}</td>
            <td class="td-mono">${m.mid   || '—'}</td>
            <td class="td-book">${m.mname || '—'}</td>
            <td class="td-muted">${m.email || '—'}</td>
            <td>
                <span class="badge-s ${m.role === 'ADMIN' ? 'bs-overdue' : 'bs-rented'}">
                    ${m.role || '—'}
                </span>
            </td>
            <td>
                <button class="btn-sm-action bsa-detail"
                    onclick="selectMember(${m.id}, '${m.mname}')">
                    <i class="bi bi-check2"></i> 선택
                </button>
            </td>
        </tr>`).join('');
    } catch (e) {
        console.error("상세 에러:", e);
        tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state">
            <i class="bi bi-exclamation-circle"></i><p>${e.message}</p>
        </div></td></tr>`;
        toast('검색 실패: ' + e.message, 'error');
    }
}


function selectMember(id, name) {
    // 검색된 회원의 ID를 대출 현황 조회창에 자동으로 넣어줍니다.
    document.getElementById('list-member-id').value = id;
    toast(`${name} 회원이 선택되었습니다.`, 'success');
    // 자동으로 목록 조회까지 실행
    loadRentals();
}
// ════════════════════════════════════════════════════
//  INIT
// ════════════════════════════════════════════════════
(function init() {
    const d = new Date();
    const days = ['일','월','화','수','목','금','토'];
    document.getElementById('topbar-date').textContent =
        `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')} ${days[d.getDay()]}요일`;

    // 관리자 이름 세팅
    if (typeof CURRENT_ADMIN_NAME !== 'undefined') {
        document.querySelector('.sb-uname').textContent = CURRENT_ADMIN_NAME;
    }
})();