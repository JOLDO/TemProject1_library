package com.library.project.library.repository;

import com.library.project.library.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Pageable을 파라미터로 받고 리턴은 Page로!
    Page<Event> findByCategory(String category, Pageable pageable);
}