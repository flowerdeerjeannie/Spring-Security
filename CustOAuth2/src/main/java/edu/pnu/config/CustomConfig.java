package edu.pnu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CustomConfig {
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	//순환 참조로 계속 서로 자기도 하겠따고 하는 바람에 무한루프 돌아가지고
	//패스워드 인코더만 따로 빼서 다른 configuration을 만들어줌
	
}
