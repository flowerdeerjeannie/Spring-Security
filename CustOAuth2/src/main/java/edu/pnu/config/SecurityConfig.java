package edu.pnu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import edu.pnu.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final OAuth2SuccessHandler successHandler;
	//successHandler는 
	//로그인 성공했을때, 임의의 사용자를 생성해서 db에 저장하고. 성공했으니까 jwt토큰을 만들어서
	//응답헤더에 설정해주는 핸들러.
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.authorizeHttpRequests(authorize->authorize
						.anyRequest().permitAll()
						//실수: anyRequest를 authenticated한다고 해가지고 login 페이지도 구동이 안되게 해놓음
						//permitAll 해야 authen 안된 사용자 즉 아무것도 로그인 안한 상태에서 로그인이 가능해짐 
				)
		
				.oauth2Login(oauth2->oauth2
					.loginPage("/login")
					.successHandler(successHandler)
				);
		
		return http.build();
	}
}
