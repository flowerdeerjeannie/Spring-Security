package edu.pnu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//이 클래스가 설정 클래스라는 정의-컨테이너에 로드.
@EnableWebSecurity
//스프링 시큐리티 적용에 필요한 객체들 자동생성
public class SecurityConfig {
	@Bean
	//이 메소드가 리턴하는 객체를 컨테이너에 등록해라.
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//접근 권한 설정하기
		http.authorizeHttpRequests(security->security
					.requestMatchers("/member/**").authenticated()
					.requestMatchers("/manager/**").hasAnyRole("MANAGER","ADMIN")
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.anyRequest().permitAll());
		
		//CSRF 보호 비활성화.
		http.csrf(cf->cf.disable());
		
		http.formLogin(form-> //SpringBoot가 제공해주는 로그인을 사용하겠다는 설정.
						form.loginPage("/login") //loginController의 GetMapping 호출
							.defaultSuccessUrl("/loginSuccess",true));
					//member를 호출해서 로그인에 성공했을 경우, /loginSuccess로 이동하겠다.
		
		http.exceptionHandling(ex->ex.accessDeniedPage("/accessDenied"));
		
		http.logout(logout->logout
			.invalidateHttpSession(true) //현재 브라우저와 연결된 세션 강제 종료
			.deleteCookies("JSESSIONID") //세션 아이디 저장된 쿠키를 삭제함
			.logoutSuccessUrl("/login") //로그아웃 후 이동할 url 지정함
		); 			
		
		http.headers(hr->hr.frameOptions(fo->fo.disable()));
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() { //암호화를 위한 빈 객체 등록 
		return new BCryptPasswordEncoder();
	}
	
//	@Autowired
//	public void authenticate(AuthenticationManagerBuilder auth) throws Exception {
//				//임시 테스트용 사용자 계정 권한에 알맞게 페이지 뜨는지 볼라고.
//				auth.inMemoryAuthentication()
//				.withUser("manager")
//				.password("{noop}abcd") //noop-들어온 패스워드가 암호화되어있지않고 그냥 비교한다.
//				.roles("MANAGER");
//				auth.inMemoryAuthentication()
//				.withUser("admin") 	//이걸로 user,password 하면 admin페이지 까지 접근 가능해야함.
//				.password("{noop}abcd")
//				.roles("ADMIN");
//	}
}
