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

/*
 * ========== ApplyRepository 설명 ==========
 * - 역할: ApplyEntity(시설 대관 신청)의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: ApplyServiceImpl에서 사용
 * - JpaRepository 상속만으로 기본 CRUD (save, findById, findAll, deleteById) 사용 가능
 */