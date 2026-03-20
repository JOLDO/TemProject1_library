package com.library.project.library.repository;

import com.library.project.library.entity.ApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ApplyRepository extends JpaRepository<ApplyEntity, Long> {

    // 사용자 아이디(mid)로 신청 내역 조회
    // 최신순 정렬을 위해 OrderByRegDateDesc를 붙이는 것을 권장합니다.
    List<ApplyEntity> findByMid(String mid);


}