package com.library.project.library.repository;


import com.library.project.library.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query("select r from Reply r where r.inquiry.ino = :ino")
    Page<Reply> listOfInquiry(@Param("ino") Long ino, Pageable pageable);
}