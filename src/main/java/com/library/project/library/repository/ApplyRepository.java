package com.library.project.library.repository;

import com.library.project.library.entity.ApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends JpaRepository<ApplyEntity, Long> {
    /**
     * JpaRepository를 상속받는 것만으로도 기본적인 CRUD가 가능합니다.
     * <ApplyEntity, Long> 은 <엔티티 클래스명, PK의 타입>을 의미합니다.
     * * - 저장: save(entity)
     * - 조회: findById(id)
     * - 전체 조회: findAll()
     * - 삭제: deleteById(id)
     */
}

