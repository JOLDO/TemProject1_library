// =============================================
// booklist.js
//
// [전체 동작 흐름 요약]
// 1. 최초 페이지 진입 / 새로고침
//    → 서버(Thymeleaf)가 HTML을 완성해서 내려줌 (깜빡임 없음)
//    → JS는 검색어만 입력창에 복원하고 추가 서버 호출 없음
//
// 2. 페이지 전환 / 검색 / 정렬
//    → loadList() 함수가 axios로 서버에 데이터 요청
//    → 응답받은 데이터로 리스트/페이지네이션 영역만 교체 (전체 페이지 새로고침 없음)
//    → URL도 ?keyword=...&page=... 형태로 변경 (새로고침해도 상태 유지)
//
// 3. 책 상세 모달
//    → 리스트 항목에 data-* 속성으로 책 데이터가 이미 심어져 있음
//    → 클릭 시 별도 API 호출 없이 data-* 속성을 읽어서 모달에 바로 표시
// =============================================


// =============================================
// 뒤로가기 시 브라우저가 자동으로 스크롤 위치를 복원하는 기능을 끔
// 'manual'로 설정하면 스크롤 위치를 JS에서 직접 제어할 수 있음
// =============================================
history.scrollRestoration = 'manual';


// =============================================
// 뒤로가기 / 앞으로가기 처리
//
// 브라우저의 뒤로가기/앞으로가기 버튼을 누르면 'popstate' 이벤트가 발생함
// history.pushState()로 URL을 바꿀 때는 실제 페이지 이동이 아니라서
// popstate로 직접 감지해서 리스트를 다시 불러와야 함
// =============================================
window.addEventListener('popstate', function() {
    // 현재 URL의 쿼리스트링을 파싱 (예: ?keyword=스프링&page=2)
    const params = new URLSearchParams(window.location.search);

    // page 파라미터가 없으면 기본값 1
    const page = parseInt(params.get('page')) || 1;

    // keyword 파라미터가 없으면 빈 문자열
    currentKeyword = params.get('keyword') || '';

    // sort 파라미터가 없으면 기본값 id
    currentSort = params.get('sort') || 'id';

    // 검색 입력창에도 복원된 키워드 표시
    document.querySelector('input[name="keyword"]').value = currentKeyword;

    // 해당 페이지/키워드/정렬로 리스트 다시 로드
    loadList(page);
});


// =============================================
// 전역 상태 변수
// =============================================

// 페이지 최초 진입 시 URL 쿼리스트링에서 파라미터를 읽어옴
const _initParams = new URLSearchParams(window.location.search);
let currentKeyword = _initParams.get('keyword') || '';
let currentPage = parseInt(_initParams.get('page')) || 1;
let currentSort = _initParams.get('sort') || 'id';


// =============================================
// 1. 리스트 렌더링
//
// 서버에서 받아온 책 목록 배열(list)을 HTML로 변환해서
// .list-group 컨테이너 안에 통째로 교체하는 함수
//
// [data-* 속성 설명]
// 각 리스트 항목(<a> 태그)에 책의 모든 정보를 data-* 속성으로 심어둠
// 클릭 시 별도 API 호출 없이 이 data-* 값을 읽어서 상세 모달에 바로 표시함
// =============================================
function renderList(list) {
    const container = document.querySelector('.list-group');

    // 목록이 비어있으면 안내 문구 표시 후 종료
    if (!list || list.length === 0) {
        container.innerHTML = '<div class="text-center text-muted py-4">검색 결과가 없습니다.</div>';
        return;
    }

    // 배열의 각 dto를 HTML 문자열로 변환한 뒤 join('')으로 하나로 합쳐서 삽입
    // || '' 는 값이 null/undefined일 때 빈 문자열로 대체하는 방어 코드
    container.innerHTML = list.map(dto => `
        <a href="#" class="list-group-item list-group-item-action"
            data-id="${dto.id}"
            data-title="${dto.bookTitle || ''}"
            data-image="${dto.bookImage || ''}"
            data-author="${dto.author || ''}"
            data-publisher="${dto.publisher || ''}"
            data-pubdate="${dto.pubdate || ''}"
            data-isbn="${dto.isbn || ''}"
            data-description="${dto.description || ''}"
            data-status="${dto.status || ''}">
            <div class="d-flex w-100 gap-3 align-items-start">

                <!-- 왼쪽: 책 표지 이미지 -->
                <div class="book-image-wrap">
                    <img src="${dto.bookImage || ''}" alt="책 표지" style="width:150px; height:auto;">
                </div>

                <!-- 가운데: 제목 / 저자 / 출판사 -->
                <div class="flex-grow-1">
                    <h5 class="mb-1">${dto.bookTitle}</h5>
                    <p class="mb-1 text-muted">${dto.author || ''}</p>
                    <small class="text-muted">${dto.publisher || ''}</small>
                </div>

                <!-- 오른쪽: 대여 상태 뱃지 / 추천 버튼 / 날짜 -->
                <div class="d-flex flex-column align-items-end justify-content-between book-actions" style="flex-shrink:0;">
                    <div class="d-flex flex-column align-items-end gap-1">

                        <!-- 대여 가능 여부: AVAILABLE이면 초록, 아니면 회색 -->
                        <span class="badge ${dto.status === 'AVAILABLE' ? 'bg-success' : 'bg-secondary'}">
                            ${dto.status === 'AVAILABLE' ? '대여 가능' : '대여 불가'}
                        </span>

                        <!-- 추천 버튼: 이미 추천한 책이면 빨간색(btn-danger), 아니면 테두리만(btn-outline-danger) -->
                        <button class="btn btn-sm recommend-btn ${dto.recommended ? 'btn-danger' : 'btn-outline-danger'}"
                                data-id="${dto.id}">
                            ${dto.recommended ? '♥ 추천됨' : '♡ 추천하기'}
                        </button>
                    </div>

                    <!-- 발행일 / 등록일 -->
                    <div class="text-end text-muted" style="font-size:0.8rem;">
                        <div>발행일 ${dto.pubdate || '-'}</div>
                        <!-- regDate는 'yyyy-MM-ddTHH:mm:ss' 형태이므로 앞 10자리만 잘라서 날짜만 표시 -->
                        <div>등록일 ${dto.regDate ? dto.regDate.substring(0, 10) : '-'}</div>
                    </div>
                </div>
            </div>
        </a>
    `).join('');
}


// =============================================
// 2. 페이지네이션 렌더링
//
// 서버 응답(data)의 페이지 정보를 읽어서
// .pagination 영역에 페이지 버튼들을 생성하는 함수
//
// [서버 응답의 페이지 관련 필드]
// data.prev  : 이전 페이지 그룹이 있는지 여부 (true/false)
// data.next  : 다음 페이지 그룹이 있는지 여부 (true/false)
// data.start : 현재 페이지 그룹의 시작 번호 (예: 1)
// data.end   : 현재 페이지 그룹의 끝 번호   (예: 10)
// data.page  : 현재 선택된 페이지 번호
// =============================================
function renderPagination(data) {
    const pagination = document.querySelector('.pagination');

    // 데이터가 없으면 페이지네이션 비우고 종료
    if (!data || !data.dtoList) {
        pagination.innerHTML = '';
        return;
    }

    let html = '';

    // [이전] 버튼: 이전 페이지 그룹이 있을 때만 표시
    // data.start - 1 = 이전 그룹의 마지막 페이지
    if (data.prev) {
        html += `<li class="page-item"><a class="page-link" href="#" onclick="loadList(${data.start - 1}); return false;">Previous</a></li>`;
    }

    // 페이지 번호 버튼들: start부터 end까지 순서대로 생성
    // 현재 페이지(data.page)와 같으면 'active' 클래스 추가 (강조 표시)
    for (let i = data.start; i <= data.end; i++) {
        html += `<li class="page-item ${data.page === i ? 'active' : ''}">
            <a class="page-link" href="#" onclick="loadList(${i}); return false;">${i}</a>
        </li>`;
    }

    // [다음] 버튼: 다음 페이지 그룹이 있을 때만 표시
    // data.end + 1 = 다음 그룹의 첫 번째 페이지
    if (data.next) {
        html += `<li class="page-item"><a class="page-link" href="#" onclick="loadList(${data.end + 1}); return false;">Next</a></li>`;
    }

    pagination.innerHTML = html;
}


// =============================================
// 3. 서버에서 책 목록 데이터 가져오기 (비동기)
//
// axios.get()으로 REST API를 호출해서 JSON 데이터를 받아옴
// 응답이 오면 renderList()와 renderPagination()으로 화면을 업데이트함
//
// [URL 파라미터]
// page    : 요청할 페이지 번호
// sort    : 정렬 기준 (id / pubdate / bookTitle / recommend / rental)
// keyword : 검색어 (없으면 빈 문자열)
// =============================================
async function loadList(page, sort) {
    // sort 파라미터가 넘어오면 전역 정렬 상태 업데이트
    if (sort) currentSort = sort;

    // 현재 페이지 상태 업데이트
    currentPage = page;

    const response = await axios.get(`/book/list?page=${page}&sort=${currentSort}&keyword=${currentKeyword}`);
    const data = response.data;

    // 리스트 영역 교체
    renderList(data ? data.dtoList : null);

    // 페이지네이션 영역 교체
    renderPagination(data);

    // URL을 현재 페이지/키워드 상태로 업데이트
    // history.pushState: 실제 페이지 이동 없이 URL만 바꿈
    // → 브라우저 뒤로가기/앞으로가기 기록에도 쌓임
    // encodeURIComponent: 한글 등 특수문자를 URL에 안전하게 인코딩
    const newSearch = `?keyword=${encodeURIComponent(currentKeyword)}&page=${page}&sort=${currentSort}`;
    if (location.search !== newSearch) {
        history.pushState(null, '', newSearch);
    }

    // 페이지 전환 시 스크롤을 맨 위로 이동
    // 'instant': 애니메이션 없이 즉시 이동
    window.scrollTo({ top: 0, behavior: 'instant' });
}


// =============================================
// 정렬 드롭다운 클릭 이벤트
//
// .sort-item 클래스가 붙은 드롭다운 항목을 클릭하면
// data-sort 속성값으로 정렬 기준을 바꾸고 1페이지부터 다시 로드
// =============================================
document.querySelectorAll('.sort-item').forEach(item => {
    item.addEventListener('click', function (e) {
        // a 태그 기본 동작(페이지 이동) 막기
        e.preventDefault();

        // 드롭다운 버튼 텍스트를 선택한 항목 이름으로 변경
        document.querySelector('.dropdown-toggle').textContent = this.textContent;

        // data-sort 속성에 정의된 정렬 기준으로 현재 페이지를 유지하며 다시 로드
        loadList(currentPage, this.getAttribute('data-sort'));
    });
});


// =============================================
// 리스트 클릭 이벤트 (추천 버튼 + 상세 모달)
//
// 이벤트 위임(Event Delegation) 방식:
// 리스트 항목이 axios로 동적으로 교체되므로
// 각 항목에 직접 이벤트를 붙이면 교체 후 이벤트가 사라짐
// → 부모(.list-group)에 이벤트를 한 번만 붙이고
//   클릭된 대상이 추천 버튼인지 리스트 항목인지를 내부에서 구분
// =============================================
document.querySelector('.list-group').addEventListener('click', async function (e) {
    // a 태그 기본 동작(페이지 이동) 막기
    e.preventDefault();

    // ── 추천 버튼 클릭 처리 ──────────────────────
    // e.target: 실제 클릭된 요소
    // .closest(): 클릭된 요소 자신 또는 가장 가까운 조상 중 .recommend-btn을 찾음
    const btn = e.target.closest('.recommend-btn');
    if (btn) {
        const bookId = btn.getAttribute('data-id');

        // btn-danger 클래스가 있으면 이미 추천한 상태 → 추천 취소
        const isActive = btn.classList.contains('btn-danger');

        // 모달에 표시할 책 제목 (버튼 주변 h5 태그에서 읽어옴)
        const bookTitle = btn.closest('.list-group-item').querySelector('h5').textContent;

        // 추천 완료/취소 확인 모달 객체 생성
        const modal = new bootstrap.Modal(document.getElementById('recommendModal'));

        if (isActive) {
            // 이미 추천한 상태 → DELETE 요청으로 추천 취소
            await axios.delete(`/book/recommend/${bookId}`);

            // 버튼 스타일을 '미추천' 상태로 변경
            btn.classList.replace('btn-danger', 'btn-outline-danger');
            btn.textContent = '♡ 추천하기';

            // 확인 모달 내용 설정 후 표시
            document.getElementById('modal-title-text').textContent = '추천 취소 ♡';
            document.getElementById('modal-body-text').textContent = `"${bookTitle}" 추천을 취소했습니다.`;
            modal.show();
        } else {
            // 추천 안 한 상태 → POST 요청으로 추천 등록
            await axios.post(`/book/recommend/${bookId}`);

            // 버튼 스타일을 '추천됨' 상태로 변경
            btn.classList.replace('btn-outline-danger', 'btn-danger');
            btn.textContent = '♥ 추천됨';

            // 확인 모달 내용 설정 후 표시
            document.getElementById('modal-title-text').textContent = '추천 완료 ♥';
            document.getElementById('modal-body-text').textContent = `"${bookTitle}" 을(를) 추천했습니다.`;
            modal.show();
        }
        // 추천 버튼 처리 후 아래 상세 모달 코드는 실행하지 않고 종료
        return;
    }

    // ── 리스트 항목 클릭 처리 (상세 모달) ──────────
    // 클릭된 요소나 그 조상 중 .list-group-item을 찾음
    const target = e.target.closest('.list-group-item');
    if (!target) return;

    // 이전에 선택된 항목의 active 강조를 모두 제거하고 현재 항목만 강조
    document.querySelectorAll('.list-group-item').forEach(item => item.classList.remove('active'));
    target.classList.add('active');

    // data-id가 없는 항목이면 종료 (안전 처리)
    if (!target.getAttribute('data-id')) return;

    // ── 상세 모달 데이터 채우기 ──────────────────────
    // API를 다시 호출하지 않고, 리스트 항목에 심어둔 data-* 속성값을 읽어서 바로 표시
    // dataset.title = data-title 속성값, dataset.image = data-image 속성값 ... 이런 방식
    document.getElementById('detail-book-title').textContent = target.dataset.title;
    document.getElementById('detail-book-image').src = target.dataset.image;
    document.getElementById('detail-author').textContent = target.dataset.author || '-';
    document.getElementById('detail-publisher').textContent = target.dataset.publisher || '-';
    document.getElementById('detail-pubdate').textContent = target.dataset.pubdate || '-';
    document.getElementById('detail-isbn').textContent = target.dataset.isbn || '-';
    document.getElementById('detail-description').textContent = target.dataset.description || '책 소개가 없습니다.';

    // 대여 상태에 따라 뱃지 색상과 버튼 텍스트/스타일을 다르게 표시
    const statusEl = document.getElementById('detail-status');
    const actionBtn = document.getElementById('detail-action-btn');
    if (target.dataset.status === 'AVAILABLE') {
        statusEl.textContent = '대여 가능';
        statusEl.className = 'badge bg-success';    // 초록색 뱃지
        actionBtn.textContent = '대여하기';
        actionBtn.className = 'btn btn-success';
    } else {
        statusEl.textContent = '대여 불가';
        statusEl.className = 'badge bg-secondary';  // 회색 뱃지
        actionBtn.textContent = '닫기';
        actionBtn.className = 'btn btn-secondary';
    }

    // 상세 모달 표시
    new bootstrap.Modal(document.getElementById('bookDetailModal')).show();
});


// =============================================
// 상세 모달이 닫힐 때 active 강조 해제
//
// 'hide.bs.modal': Bootstrap 모달이 닫히기 시작할 때 발생하는 이벤트
// 모달이 닫히면 리스트 항목의 active 강조를 모두 제거
// =============================================
document.getElementById('bookDetailModal').addEventListener('hide.bs.modal', function () {
    document.querySelectorAll('.list-group-item').forEach(item => item.classList.remove('active'));
});


// =============================================
// 검색 폼 submit 이벤트
//
// form의 기본 GET 제출을 막고 axios 비동기 방식으로 처리
// 검색어를 currentKeyword에 저장한 뒤 1페이지부터 다시 로드
// =============================================
document.querySelector('form').addEventListener('submit', function(e) {
    // form 기본 동작(페이지 이동) 막기
    e.preventDefault();

    // 입력창에서 검색어 읽어서 전역 변수에 저장
    currentKeyword = document.querySelector('input[name="keyword"]').value;

    // 1페이지부터 검색
    loadList(1);
});


// =============================================
// 초기화 버튼 클릭 이벤트
//
// 검색어를 비우고 1페이지 전체 목록으로 돌아감
// stopPropagation(): form 이벤트로 이벤트가 버블링되지 않게 막음
// =============================================
document.querySelector('.clearBtn').addEventListener('click', function (e) {
    e.preventDefault();
    e.stopPropagation(); // 이 클릭이 form submit으로 전파되지 않도록 차단

    // 검색어 초기화
    currentKeyword = '';
    currentSort = 'id';
    document.querySelector('input[name="keyword"]').value = '';
    document.querySelector('.dropdown-toggle').textContent = '정렬 기준';

    // 1페이지 전체 목록 로드
    loadList(1);
});


// =============================================
// 페이지 최초 진입 시 초기화
//
// [초기 렌더링 전략]
// 최초 진입과 새로고침은 서버(Thymeleaf)가 HTML을 완성해서 내려줌
// → JS에서 loadList()를 다시 호출하면 화면이 두 번 그려져서 깜빡임 발생
// → 그래서 여기서는 loadList()를 호출하지 않음
//
// 검색어(currentKeyword)와 정렬(currentSort)을 UI에 복원해서 상태를 맞춰줌
// (서버가 이미 올바른 keyword/page/sort로 렌더링했기 때문에 리스트는 건드리지 않아도 됨)
// =============================================
document.querySelector('input[name="keyword"]').value = currentKeyword;

// 새로고침 시 URL의 sort 값에 맞게 드롭다운 버튼 텍스트 복원
const sortItem = document.querySelector(`.sort-item[data-sort="${currentSort}"]`);
if (sortItem) {
    document.querySelector('.dropdown-toggle').textContent = sortItem.textContent;
}