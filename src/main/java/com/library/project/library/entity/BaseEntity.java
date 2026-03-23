package com.library.project.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
abstract class BaseEntity { // 설계 클래스 목적으로 사용 할 예정

    // 생성 시간 필드
    @CreatedDate
    @Column(name = "regDate", updatable = false)
    private LocalDateTime regDate;

    // 수정 시간 필드
    // 주의, 수정할 때, 자동으로 시간이 기록이 되는게 목적인데, 옵션에서 수정을 못하게 막으면, 모순. !!!
    // updatable = false , 제외 했음.
    @LastModifiedDate
    @Column(name = "modDate")
    private LocalDateTime modDate;
}

/*
 * ========== BaseEntity 설명 ==========
 * - 역할: 모든 엔티티의 공통 필드(생성일, 수정일)를 관리하는 추상 클래스
 * - 쓰이는 곳: Book, Member, Rental, Recommend 등 대부분의 엔티티가 상속
 *
 * [필드]
 * - regDate: 엔티티 최초 생성 시간 (자동 기록, 수정 불가)
 * - modDate: 엔티티 수정 시간 (수정 시 자동 갱신)
 *
 * [핵심 어노테이션]
 * - @MappedSuperclass: 테이블을 따로 만들지 않고 자식 엔티티에 필드만 상속
 * - @EntityListeners(AuditingEntityListener): JPA Auditing으로 시간 자동 기록
 */
