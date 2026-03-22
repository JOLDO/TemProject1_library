package com.library.project.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 생성 시간, 수정 시간, 작성자 등을 자동으로 관리해주는 JPA Auditing 기능을 활성화하는 어노테이션
@EnableJpaAuditing
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}


}
