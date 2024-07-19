package edu.pnu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//다른 부트랑 다른점 security니까!! 이 보안에 관한 부분을 작성할 config 가 필요함 
public class SecurityConfig {
	//authorize에 관한 내용을 명시한 메소드를 적고 그 객체를 리턴해줄건데
	//얘를 세큐리티 컨텍스트에 저장해다오-bean:등록해라!!
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//HttpSecurity라는 객체를 받아서 보안 필터 체인을 구성해 볼거야.
		http.authorizeHttpRequests(security->security
		//http의 요청에 관한 "접근 권한" 설정을 해볼건데 security 요청.역할 순서야
				.requestMatchers("/member/**").authenticated()
				//http요청 즉 주소가 /member/**로 시작하는건 "인증된 사용자"만 접근 가능해
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
				//manager/로 시작하는건 특정 "manager, admin" 역할 만 접근 가능해.
				.requestMatchers("/admin/**").hasRole("ADMIN")
				//admin주소는 "admin" 역할을 가진 사람만 접근 가능해.
				.anyRequest().permitAll());
				//요청.역할 에서 어떤 요청이든, 모든 역할 all permit한다.
		http.csrf(cf->cf.disable());
		//csrf-보호기능 메소드.개발할때만 비활성화 하는..
		http.formLogin(form->
		//로그인 설정
		//부트가 제공해주는 로그인을 사용하겠다는 설정을 해줘야 로그인이 가능해짐.
		//그리고 이 로그인 설정에서 보안 인증 등 자세한 메소드 말고 보여지는 내용을 작성해줘야함
						form.loginPage("/login") //디폴트 로그인 페이지
							.defaultSuccessUrl("/loginSuccess",true)
							//로그인이 성공했자나? 그럼 어떤 페이지 보여질지 이정도는 말해줘야지
							//그리고 /login으로 주소변동 즉 요청이 변화하는거니까
							//loginController를 만들어서 요청처리를 하게 해줘야됨
		);
		
		http.exceptionHandling(ex->ex.accessDeniedPage("/accessDenied"));
		//익셉션처리메소드 
		
		http.logout(logout->logout			//로그아웃하면 일어날일 
				.invalidateHttpSession(true) //세션 강제종료
				.deleteCookies("JSESSIONID") //쿠키삭제
				.logoutSuccessUrl("/login")); //로그아웃sucess하면 이동할 url 지정
		
		http.headers(hr->hr.frameOptions(fo->fo.disable()));

		return http.build();
	}
	
	@Bean
	PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
