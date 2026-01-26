package com.polywave.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.polywave.userservice.domain")
@EnableJpaRepositories("com.polywave.userservice.repository")
public class UserServiceApplication {
	public static void main(String[] args) {
		// JVM 타임존을 대한민국(KST) 기준으로 설정
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
