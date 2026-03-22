// 세션 만료(401) 시 로그인 페이지로 이동
axios.interceptors.response.use(
    res => res,
    err => {
        if (err.response?.status === 401) {
            alert("로그인이 필요합니다.");
            location.reload();
        }
        return Promise.reject(err);
    }
);

const CHOSUNG = ['ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'];

function getChosung(char) {
    const code = char.charCodeAt(0) - 0xAC00; //예 나 는    45208-44032(한글 시작(가))
    if (code < 0 || code > 11171) return char;  //한글 범위가 아니면 그대로 반환
    return CHOSUNG[Math.floor(code / 588)]; //초성 하나당 588글자가 존재하는데 나눠서 초성을 뽑아냄
}

function isChosung(str) {
    return /^[ㄱ-ㅎ]+$/.test(str);    //ㄱ~ㅎ사이에 str이 존재하면 true 아니면 false
}

function highlightChosung(text, keyword) {
    if (!text || !keyword) return text || '';
    const textChosung = [...text].map(getChosung).join(''); //들어온 글자를 한굴자씩 배열로 담을때 [...a] 사용 이렇게 쓰면 이모지도 깔끔하게 하나로 뺄수 있음
    let result = '';
    let i = 0;
    while (i < text.length) {
        if (textChosung.slice(i).startsWith(keyword)) { //키워드와 겹치는 부분을 마크로 강조
            result += `<mark>${text.slice(i, i + keyword.length)}</mark>`;
            i += keyword.length;    //겹친 만큼 이후로 진행
        } else {
            result += text[i];
            i++;
        }
    }
    return result;
}

function highlight(text, keyword) {
    if (!keyword || !text) return text || '';
    if (isChosung(keyword)) return highlightChosung(text, keyword); //초성이면

    // 키워드의 각 글자 사이에 공백이 있어도 매칭되도록 패턴 생성
    // '배 가본드' → '배\s*가\s*본\s*드' → '배가본드'도 매칭
    const trimmed = keyword.replace(/\s+/g, ''); //키워드의 모든 공백을 제거(/g가 없으면 처음의 공백만 제거) (s+는 공백이 1개이상인것)
    const escaped = trimmed.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // 특수문자 이스케이프(특수 문자 앞에는 백슬래시가 있어야 문자로 인식되어서 $&(매칭된 문자) 앞에 백슬래시 붙임
    const pattern = escaped.split('').join('\\s*'); // 공백 제거되고 정규식에서 특수문자로 읽을수 있게한 것의 글자 사이 공백 허용(s*는 0개이상)
    const regex = new RegExp(pattern, 'gi');    //g:global(한번 발견해도 다음것도 계속 발견하게), i(대소문자 구분x)
    return text.replace(regex, '<mark>$&</mark>');
}

// =============================================
// 페이지 로드 시 리스트 검색어 하이라이트
// =============================================
const listKeyword = new URLSearchParams(window.location.search).get('keyword') || '';
if (listKeyword) {
    document.querySelectorAll('.list-group-item').forEach(item => {
        const h5 = item.querySelector('h5');
        const p = item.querySelector('p');
        const small = item.querySelector('small');
        if (h5) h5.innerHTML = highlight(h5.textContent, listKeyword);
        if (p) p.innerHTML = highlight(p.textContent, listKeyword);
        if (small) small.innerHTML = highlight(small.textContent, listKeyword);
    });
}

// =============================================
// 리스트 클릭 이벤트 (추천 버튼 + 상세 모달)
//
// 이벤트 위임(Event Delegation) 방식:
// 리스트 항목이 axios로 동적으로 교체되므로
// 각 항목에 직접 이벤트를 붙이면 교체 후 이벤트가 사라짐
// → 부모(.list-group)에 이벤트를 한 번만 붙이고
//   클릭된 대상이 추천 버튼인지 리스트 항목인지를 내부에서 구분
// =============================================

let currentBookId;

document.querySelector('.list-group').addEventListener('click', function (e) {
    // 리스트 아이템이나 추천 버튼이 아니면 기본 동작 허용 (전체 목록 보기 링크 등)
    if (!e.target.closest('.list-group-item') && !e.target.closest('.recommend-btn')) return;
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
            deleteRecomm(bookId).then(result => {
                btn.classList.replace('btn-danger', 'btn-outline-danger');
                btn.textContent = '♡ 추천하기';

                // 확인 모달 내용 설정 후 표시
                document.getElementById('modal-title-text').textContent = '추천 취소 ♡';
                document.getElementById('modal-body-text').textContent = `"${bookTitle}" 추천을 취소했습니다.`;
                modal.show();
            }).catch(e => {
                if (e.response?.status !== 401) alert("오류가 발생했습니다.");
            })
            // 버튼 스타일을 '미추천' 상태로 변경

        } else {
            // 추천 안 한 상태 → POST 요청으로 추천 등록
            insertRecomm(bookId).then(result => {
                // 버튼 스타일을 '추천됨' 상태로 변경
                btn.classList.replace('btn-outline-danger', 'btn-danger');
                btn.textContent = '♥ 추천됨';

                // 확인 모달 내용 설정 후 표시
                document.getElementById('modal-title-text').textContent = '추천 완료 ♥';
                document.getElementById('modal-body-text').textContent = `"${bookTitle}" 을(를) 추천했습니다.`;
                modal.show();
            }).catch(e => {
                if (e.response?.status !== 401) alert("오류가 발생했습니다.");
            })

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

    if (!target.getAttribute('data-id')) return;
    currentBookId = target.getAttribute('data-id');
    selectBookDetail(currentBookId).then(result => {
        if (loginInfo && result.recommended == null) {
            alert("세션이 만료되었습니다.");
            location.reload();
            return;
        }
        // ── 상세 모달 데이터 채우기 ──────────────────────
        // API를 다시 호출하지 않고, 리스트 항목에 심어둔 data-* 속성값을 읽어서 바로 표시
        // dataset.title = data-title 속성값, dataset.image = data-image 속성값 ... 이런 방식
        const recommendBtn = target.querySelector('.recommend-btn');
        const isRecommended = recommendBtn && recommendBtn.classList.contains('btn-danger');
        const detailTitle = document.getElementById('detail-book-title');
        // detailTitle.textContent = (result.bookTitle || '') + (isRecommended ? ' ♥' : ' ♡');
        detailTitle.innerHTML = highlight(result.bookTitle || '', listKeyword) + (loginInfo ? (isRecommended ? '<span style="color:red"> ♥</span>' : '<span style="color:red"> ♡</span>') : '');
        detailTitle.style.cursor = 'pointer';
        document.getElementById('detail-book-image').src = result.bookImage;
        // document.getElementById('detail-author').textContent = result.author || '-';
        document.getElementById('detail-author').innerHTML = highlight(result.author || '-', listKeyword);
        // document.getElementById('detail-publisher').textContent = result.publisher || '-';
        document.getElementById('detail-publisher').innerHTML = highlight(result.publisher || '-', listKeyword);
        document.getElementById('detail-pubdate').textContent = result.pubdate || '-';
        document.getElementById('detail-isbn').textContent = result.isbn || '-';
        // document.getElementById('detail-description').textContent = result.description || '책 소개가 없습니다.';
        document.getElementById('detail-description').innerHTML = highlight(result.description || '책 소개가 없습니다.', listKeyword);


        // 대여 상태에 따라 뱃지 색상과 버튼 텍스트/스타일을 다르게 표시
        const statusEl = document.getElementById('detail-status');
        const actionBtn = document.getElementById('detail-action-btn');
        if(loginInfo) {
            if (result.status === 'AVAILABLE') {
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
        }

        // 상세 모달 표시
        new bootstrap.Modal(document.getElementById('bookDetailModal')).show();
    }).catch(e => {
        if (e.response?.status !== 401) alert("오류가 발생했습니다.");
        document.querySelectorAll('.list-group-item').forEach(item => item.classList.remove('active'));
    })
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
// 상세 모달 제목 클릭 → 추천 토글
// =============================================
document.getElementById('detail-book-title').addEventListener('click', function () {
    if(!loginInfo)   return;
    // 현재 추천 상태 확인 (제목 끝에 ♥가 있으면 추천된 상태)
    const isRecommended = this.textContent.includes('♥');

    // 리스트에서 해당 책의 추천 버튼 찾기
    const listBtn = document.querySelector(`.recommend-btn[data-id="${currentBookId}"]`);


    if (isRecommended) {
        // 추천 취소
        deleteRecomm(currentBookId).then(result => {
            this.innerHTML = this.innerHTML.replace(' ♥', ' ♡');
            if (listBtn) {
                listBtn.classList.replace('btn-danger', 'btn-outline-danger');
                listBtn.textContent = '♡ 추천하기';
            }
        }).catch(e => {
            if (e.response?.status !== 401) alert("오류가 발생했습니다.");
        })
    } else {
        // 추천 등록
        insertRecomm(currentBookId).then(result => {
            this.innerHTML = this.innerHTML.replace(' ♡', ' ♥');
            if (listBtn) {
                listBtn.classList.replace('btn-outline-danger', 'btn-danger');
                listBtn.textContent = '♥ 추천됨';
            }
        }).catch(e => {
            if (e.response?.status !== 401) alert("오류가 발생했습니다.");
        })
    }
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

    clearSearch()
});

function clearSearch() {
    // 검색어 초기화
    document.querySelector('input[name="keyword"]').value = '';
    document.querySelector('.dropdown-toggle').textContent = '정렬 기준';

    // 검색어 없이 1페이지로 서버에 요청
    window.location.href = '/book/booklist';
}

async function insertRecomm(bookId) {
    await axios.post(`/book/recommend/${bookId}`);
}

async function deleteRecomm(bookId) {
    await axios.delete(`/book/recommend/${bookId}`);
}

async function selectBookDetail(bookId) {
    const result = await axios.get(`/book/${bookId}`);
    return result.data;
}