package edu.pnu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import edu.pnu.config.filter.JWTAuthenticationFilter;
import edu.pnu.config.filter.JWTAuthorizatioFilter;
import edu.pnu.persistence.MemberRepository;

//설정-configuration

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	//encoder해줄 메소드에 리턴으로 encoder 뱉기
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

	//SecurityfilterChain에 대한 메소드 작성
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf->csrf.disable()); //테스트 할거니까 csrf 보호 비활성화
		
		http.authorizeHttpRequests(auth->auth
				.requestMatchers("/member/**").authenticated()
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().permitAll());
				//저 위에 /member, manager, admin 등 말고 나머지 url 요청은 모두
				//어떤거든 다 role 없어도 permit 해줌 -> 그래야 로그인 창도 권한없이 누구에게나 뜸
		
		http.formLogin(frmLogin->frmLogin.disable()); //form을 이용한 로그인 설정하지 않겟습니다 -> login 페이지 안나옴 
		
		http.httpBasic(basic->basic.disable()); //http basic 인증 방식 사용하지 않겠습니다. 
		
		//세션을 사용하지 않고, 토큰을 기반으로 한 인증 방식을 통해 클라이언트와 서버 간의 상태를 관리하는 방식
		
		http.sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		//세션메니지먼트야 듣거라. creationPolicy 설정-stateless:세션을 유지하지 않겠다. url호출 뒤 응답할때까지는 유지되지만 응답 후 삭제하걸아.
		//톰캣은 세션을 가지고 있지만 "SpringSecurity 안의 session"은 토큰을 프론트에 넘겨주고 삭제하걸아. 
		//로그인 기반 세션 유지가 안되는거-> 그렇다면 어떻게 인증을 처리하냐?
		//토큰 기반 인증: JWT(JSON Web Token) 같은 토큰을 사용하여 클라이언트가 서버에 인증 정보를 전달합니다.

		//사용자가 로그인하면 서버는 JWT를 발급합니다. 
		//클라이언트는 이후의 모든 요청에 이 JWT를 포함시켜 서버에 전송합니다. -> 그리고 세션은 삭제된다고!! 
		//서버는 각 요청마다 이 토큰을 검증하여 사용자를 인증합니다.
		
		http.addFilter(new JWTAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()));
		
		http.addFilterBefore(new JWTAuthorizatioFilter(memberRepository), AuthorizationFilter.class);
		
		return http.build();
	}

}
