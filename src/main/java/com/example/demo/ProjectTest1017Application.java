package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // JPA 리스너 기능 활성화
public class ProjectTest1017Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjectTest1017Application.class, args);
	}

}
