package com.library.project.library.repository;

import com.library.project.library.entity.Rental;
import com.library.project.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
