package com.library.project.library.repository;


import com.library.project.library.entity.LibraryStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryStatsRepository extends JpaRepository<LibraryStatsEntity, Long> {
    // 상속만으로 findAll(), save(), findById() 등을 바로 사용할 수 있습니다.
}
