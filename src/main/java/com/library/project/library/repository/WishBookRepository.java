package com.library.project.library.repository;

import com.library.project.library.entity.WishBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishBookRepository extends JpaRepository<WishBookEntity, Long> {
    // 기본 save(), findAll(), findById() 등은 자동으로 생성됩니다.
}