package com.library.project.library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.project.library.config.KoreanDecomposer;
import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.Recommend;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final RecommendRepository recommendRepository;
    private final ModelMapper modelMapper;
    private final KoreanDecomposer koreanDecomposer;

    @Override
    public PageResponseDTO<BookDTO> list(PageRequestDTO pageRequestDTO) {
        String keyword = pageRequestDTO.getKeyword();

        // 한글 검색 지원을 위해 검색어를 두 가지 형태로 변환
        // keywordNor: 자모 분리 정규화 (예: "스프링" → "ㅅㅡㅍㄹㅣㅇ")
        // keywordCho: 초성만 추출       (예: "스프링" → "ㅅㅍㄹ")
        String keywordNor = koreanDecomposer.toNormal(pageRequestDTO.getKeyword());
        String keywordCho = koreanDecomposer.toChosung(pageRequestDTO.getKeyword());

        // Pageable: 페이지 번호, 페이지 크기, 기본 정렬 기준을 담은 객체
        // "id" 기준 내림차순 = 최신 등록순 (정렬 드롭다운 기본값과는 별개로 페이징 처리에 사용)
        Pageable pageable = pageRequestDTO.getPageable("id");

        // 정렬 드롭다운에서 선택한 sort 값 (id / pubdate / bookTitle / recommend / rental)
        String sort = pageRequestDTO.getSort();

        // QueryDSL로 구현된 커스텀 메서드: isbn 중복 제거 + 검색 + 정렬 + 페이징을 한 번에 처리
        Page<Book> result = bookRepository.searchDistinctAll(keyword, keywordNor, keywordCho, sort, pageable);

        // 현재 페이지의 책 목록
        List<Book> books = result.getContent();

        // ── 배치 조회: 책 목록 전체의 isbn/id를 한꺼번에 IN 쿼리로 조회 ──────────
        // stream().map()으로 books 리스트에서 isbn/id만 추출해서 리스트로 만듦
        List<String> isbns = books.stream().map(book -> book.getIsbn()).collect(Collectors.toList());
        List<Long> bookIds = books.stream().map(book -> book.getId()).collect(Collectors.toList());

        // isbn 목록 중 AVAILABLE 상태인 isbn만 Set으로 저장
        // Set을 쓰는 이유: contains() 조회가 O(1)로 빠름 (List는 O(n))
        Set<String> availableIsbns = isbns.isEmpty() ? new HashSet<>()
                : new HashSet<>(bookRepository.findAvailableIsbnIn(isbns, BookStatus.AVAILABLE));   //isbn이 있으면 빌릴수 있는 책있는지 찾아서 중복 제거해서 넣어줌

        // bookId 목록 중 추천 기록이 있는 bookId만 Set으로 저장
        Set<Long> recommendedBookIds = bookIds.isEmpty() ? new HashSet<>()
                : new HashSet<>(recommendRepository.findBookIdsByBookIdIn(bookIds));    //id가 있으면 추천한 책을 찾는다. 중복 제거 해서

        // ── Book → BookDTO 변환 ────────────────────────────────────────────────
        List<BookDTO> dtoList = books.stream()
                .map(book -> {
                    // ModelMapper: Book 엔티티의 필드를 BookDTO에 자동으로 복사
                    BookDTO dto = modelMapper.map(book, BookDTO.class);

                    // 대여 가능 여부: 같은 isbn을 가진 책 중 AVAILABLE인 것이 있으면 대여 가능
                    // Set.contains()로 O(1) 조회 (DB 추가 쿼리 없음)
                    dto.setStatus(availableIsbns.contains(book.getIsbn())
                            ? BookStatus.AVAILABLE
                            : BookStatus.RENTED);

                    // 추천 여부: 이 bookId에 추천 기록이 있으면 true
                    // → 프론트에서 추천 버튼 초기 상태(♥ 추천됨 / ♡ 추천하기) 결정에 사용
                    dto.setRecommended(recommendedBookIds.contains(book.getId()));

                    return dto;
                })
                .collect(Collectors.toList());

        // 전체 결과 수 (페이지네이션 계산에 사용)
        int total = (int) result.getTotalElements();

        // 빌더 패턴으로 페이지 응답 객체 생성
        // withAll(): 페이지 범위(start/end), prev/next 여부 등을 내부에서 자동 계산
        return PageResponseDTO.<BookDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    // 책 단건 조회 (모달 상세정보용)
    @Override
    public BookDTO getBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        BookDTO dto = modelMapper.map(book, BookDTO.class);
        dto.setStatus(bookRepository.existsByIsbnAndStatus(book.getIsbn(), BookStatus.AVAILABLE)
                ? BookStatus.AVAILABLE
                : BookStatus.RENTED);
        dto.setRecommended(recommendRepository.existsByBook_Id(book.getId()));
        return dto;
    }

    // 추천하기: RecommendHistory에 row 추가
    // bookId는 isbn 대표 row(min id)의 id
    @Override
    public void recommend(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        recommendRepository.save(Recommend.builder().book(book).build());
    }

    // 추천 해제: RecommendHistory에서 해당 bookId row 삭제
    // deleteBy~ 메서드는 @Transactional 필수
    @Override
    @Transactional
    public void unrecommend(Long bookId) {
        recommendRepository.deleteByBook_Id(bookId);
    }


    //-----------------------api관련(사용x)

    // 네이버 API 검색 키워드 목록
    /*public static final String[] SEARCH_KEYWORDS = {
            // [한국어 100권]
            "나루토1", "1984", "남쪽으로 튀어", "삼국지 1", "동물의 세계", "인생을 위한 최소한의 생각", "정답은 있다", "완벽한 원시인", "괴테는 모든 것을 말했다", "나의 완벽한 장례식",
            "해커스 토익 기출 VOCA(보카)", "인생 마지막에 쓰는 주식투자 교과서", "죽은 왕녀를 위한 파반느(양장 특별판)", "자몽살구클럽", "프로젝트 헤일메리(영화 특별판)",

            // [외국어 100권]
            "Claire Lombardo Saints for All Occasions", "Octavia E. Butler Adulthood Rites", "Ann Patchett The Patron Saint of Liars", "The Boy Who Lost His Face", "Henry and Mudge ", "The Tiger Rising", "The Hundred Dresses", "배가본드 1", "위대한 개츠비", "월간 디자인",
            "National Geographic", "해리포터 1", "무소유", "외로우니까 사람이다", "호밀밭의 파수꾼"
    };*/

    // API 호출 완료 여부 (로딩 화면 → 리스트 화면 전환 타이밍에 사용)
    // private boolean responseStatus = false;

    // 앱 시작 시 @PostConstruct로 호출됨
    // DB가 비어있을 때만 네이버 API에서 책 데이터를 가져와 저장
    // @Async: 백그라운드에서 실행 (API 호출이 오래 걸리므로 메인 스레드 블로킹 방지)
    /*@Async
    public void printApiResponse() {
        responseStatus = true;
        if (!bookRepository.existsByBooks() && responseStatus) {
            responseStatus = false;
            String clientId = "8KdNgUdpwIYVPrI3OINv";
            String clientSecret = "nUeGUYzYdc";

            RestTemplate restTemplate = new RestTemplate();

            for (String book : SEARCH_KEYWORDS) {
                try {
                    // display=1: 검색 결과 1개만 가져옴
                    String apiUrl = "https://openapi.naver.com/v1/search/book_adv.json?display=1&d_titl=" + book;

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-Naver-Client-Id", clientId);
                    headers.set("X-Naver-Client-Secret", clientSecret);
                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode itemsNode = root.path("items");

                    List<BookApiDTO> list = objectMapper.treeToValue(itemsNode, new TypeReference<>() {});
                    if (!list.isEmpty()) {
                        for (BookApiDTO item : list) {
                            // stack: 이 책을 몇 권 보유할지 랜덤(1~3)으로 결정
                            // stack 수만큼 Book row를 생성 → 각 row가 물리적 한 권을 의미
                            int stack = new Random().nextInt(3) + 1;
                            for (int i = 0; i < stack; i++) {
                                bookRepository.save(Book.builder()
                                    .isbn(item.getIsbn())
                                    .bookTitle(StringEscapeUtils.unescapeHtml4(item.getBookTitle()))
                                    .bookTitleNormal(koreanDecomposer.toNormal(StringEscapeUtils.unescapeHtml4(item.getBookTitle())))
                                    .bookTitleChosung(koreanDecomposer.toChosung(StringEscapeUtils.unescapeHtml4(item.getBookTitle())))
                                    .bookImage(item.getBookImage())
                                    .author(item.getAuthor())
                                    .publisher(item.getPublisher())
                                    .pubdate(formatPubdate(item.getPubdate()))
                                    .description(item.getDescription())
                                    .status(Book.Status.AVAILABLE) // 처음엔 모두 대여 가능
                                    .build());
                            }
                        }
                    }
                    System.out.println("성공: " + response.getBody());
                    Thread.sleep(200); // 네이버 API 호출 간격 (rate limit 방지)

                } catch (Exception e) {
                    log.error("처리 실패 (검색어: {}): {}", book, e.getMessage());
                }
            }
        }
        responseStatus = true;
    }*/

    // 네이버 API의 pubdate는 "yyyyMMdd" 형식 문자열 → LocalDate로 변환
    /*private LocalDate formatPubdate(String pubdate) {
        if (pubdate == null || pubdate.isBlank()) return null;
        try {
            return LocalDate.parse(pubdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }*/

    // 로딩 화면에서 2초마다 폴링하여 API 완료 여부 확인
    /*public boolean isReady() {
        return responseStatus;
    }*/

    // 책 목록 조회 (isbn 기준 중복 제거 + 검색 + 페이징)
    //
    // [status 세팅 방식]
    // 각 Book row는 한 권의 상태를 가지지만,
    // 리스트에서는 isbn 전체 기준으로 대여 가능 여부를 표시해야 함
    // → isbn 중 AVAILABLE인 row가 하나라도 있으면 dto.status = AVAILABLE
    // → 모두 RENTED면 dto.status = RENTED
    //
    // [recommended 세팅]
    // RecommendHistory에 해당 bookId가 있으면 이미 추천한 책
    // → 페이지 로드 시 추천하기 버튼 초기 상태 결정에 사용
    // ─────────────────────────────────────────────────────────────────
    // 책 목록 페이징 조회
    //
    // [전체 흐름]
    // 1. 검색어를 일반/초성 형태로 변환
    // 2. isbn 기준 중복 제거 + 검색 + 정렬된 책 목록을 DB에서 페이징 조회
    // 3. 조회된 책 목록의 isbn/id를 한 번에 배치 조회 (쿼리 최소화)
    // 4. 각 Book → BookDTO 변환 후 status/recommended 값 세팅
    // 5. 페이지 응답 객체(_PageResponseDTO)로 포장해서 반환
    //
    // [쿼리 최적화]
    // 이전: 책 1권당 쿼리 2개 (status 조회 + 추천 여부 조회)
    //       → 10권이면 1 + 10*2 = 21개 쿼리
    // 현재: isbn 목록 배치 조회 1번 + bookId 목록 배치 조회 1번
    //       → 10권이어도 1 + 1 + 1 = 3개 쿼리
    // ─────────────────────────────────────────────────────────────────

}