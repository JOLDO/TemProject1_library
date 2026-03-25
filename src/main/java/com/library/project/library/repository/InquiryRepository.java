package com.library.project.library.repository;


import com.library.project.library.domain.Inquiry;
import com.library.project.library.repository.search.InquirySearch;
import org.springframework.data.jpa.repository.JpaRepository;



// 💡 여기에 InquirySearch가 추가되어 있어야 searchAll을 쓸 수 있습니다!
public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquirySearch {
}
