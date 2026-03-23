package com.library.project.library.repository;

import com.library.project.library.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Long> {

    // 중복 신청 방지용: 해당 회원이 이 행사에 이미 신청했는지 확인
    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);
}