package com.library.project.library.repository;

import com.library.project.library.entity.EventApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventApplyRepository extends JpaRepository<EventApply, Long> {

    // 현재 로그인한 사용자의 이메일로 신청 내역 조회 (이벤트 정보까지 한 번에 가져오기)
    @Query("select ea from EventApply ea " +
            "join fetch ea.event " +
            "where ea.member.email = :email " +
            "order by ea.id desc")
    List<EventApply> findByMemberEmail(@Param("email") String email);

    // 중복 신청 방지 체크 (이미 신청했는지 확인)
    boolean existsByEventIdAndMemberEmail(Long eventId, String email);
}
