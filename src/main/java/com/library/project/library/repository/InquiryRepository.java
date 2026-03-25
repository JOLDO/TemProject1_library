package com.library.project.library.repository;

import com.library.project.library.domain.Inquiry;
import com.library.project.library.repository.search.InquirySearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquirySearch {

    /**
     * ❌ 기존 코드: Page<Inquiry> findByWriter(String writer, Pageable pageable);
     * * 📍 [수정 후]
     * Inquiry 엔티티 안에 있는 member 객체의 mid 필드를 찾아야 하므로
     * 메서드 이름을 아래와 같이 명시적으로 바꿔야 합니다. (중요: Member의 Mid)
     */
    Page<Inquiry> findByMember_Mid(String mid, Pageable pageable);

}