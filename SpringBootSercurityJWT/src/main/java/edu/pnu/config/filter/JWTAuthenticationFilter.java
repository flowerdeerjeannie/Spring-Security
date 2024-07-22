package edu.pnu.config.filter;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pnu.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//클라이언트 요청이 오면 거기에 들어있는 jwt토큰을 검증해서
//사용자가 유효한지 확인하고 검증하는 필터 클래스

//log 사용하게 해주는 라이브러리
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	//인증 객체
	private final AuthenticationManager authenticationManager;
	
	//사용자가 /login 으로 보낸 인증 요청을 처리하는 메소드 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
			
		
			//ObjectMapper는 json을 java객체로 변환시켜줌
			//request에 들어있는 json을(username, password)를 java인 member로 바꾸어서 갖다준다.
			ObjectMapper mapper = new ObjectMapper();
			
		try {
			//mapper에서 읽어와서 Security에게 자격 증명을 요청하는데 필요한 member 객체를 생성함
			Member member = mapper.readValue(request.getInputStream(), Member.class);
			//인증 진행->userDetailsService를 통해 db로부터 사용자 정보를 읽어옴
			//사용자가 입력한 정보와 db정보 비교 후
			//자격증명 성공하면 Authentication 객체 만들어서 리턴.
			Authentication authToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
	        
			//디버깅 용도로 username을 로그에 기록한다.
			log.info(member.getUsername());

			return authenticationManager.authenticate(authToken);
		
		} catch (Exception e) {
			log.info(e.getMessage()); // 'log' 객체를 사용하여 오류 메시지를 기록
		}
		response.setStatus(HttpStatus.UNAUTHORIZED.value()); //자격증명 실패시 응답코드 리턴 
		return null;
	}
	
	//인증이 성공했을 때 실행되는 후처리 메소드-successfulAuthentication 호출되어 JWT 토큰을 생성하고 응답에 포함시킴
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		
		//자격증명 성공 후 authResult.getPrincipal();를 통해 인증된 사용자의 세부 정보를 가져와서 user객체에 넣어놈
		User user = (User)authResult.getPrincipal();
		
		//authResult에 기록된내용 보기 위하여 log.info를 통해 콘솔에 출력되도록 함
	    log.info(user.getUsername());
	    log.info(authResult.toString());
	    
		//JWT 라이브러리 사용하여 토큰 생성시킴
		String token = JWT.create()
							.withExpiresAt(new Date(System.currentTimeMillis()+1000*60*10000)) // 만료시간 함께
							.withClaim("username", user.getUsername()) //토큰에 사용자 이름 포함
							.sign(Algorithm.HMAC256("edu.pnu.jwt")); //JWT 비밀 키를 사용하여 토큰에 서명함
		response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token); //생성된 JWT를 authorization 헤더에 추가함
		response.setStatus(HttpStatus.OK.value());	//응답 상태 코드를 ok로 설정함 	
	}
}
