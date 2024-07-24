package edu.pnu.handler;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import edu.pnu.domain.Member;
import edu.pnu.domain.Role;
import edu.pnu.persistence.MemberRepository;
import edu.pnu.util.CustomMyUtil;
import edu.pnu.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	//Handler: 특정 이벤트가 발생했을 때 수행되는 작업 작성하는 클래스 ex) 로그인 성공 시의 작업처리 
	//이 핸들러는 로그인성공해서 db에 저장하고, 토큰만들어가지고 헤더에 넘겨주는 핸들러임
	//db랑 비교하는건 추가적인 과정이므로 그거 생각하지말고..
	//일단 로그인 성공해서 객체 만들고 토큰 반환해주고 헤더에 넘겨주는걸로.
	
	private final MemberRepository memRepo;
	private final PasswordEncoder encoder;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		log.info("OAuth2SuccessHandler:OAuthenticationSuccess");
		OAuth2User user = (OAuth2User)authentication.getPrincipal();
		//oauth가 로그인해서 만들어진 user 객체
		//임의의 사용자로서 서버에 저장됨
		
		String username = CustomMyUtil.getUsernameFromOAuth2User(user);
		
		if(username == null) {
			log.error("onAuthenticationSuccess:Cannot generate username from oauth2user!");
			throw new ServletException("can't generate");
			
		}
		
		log.info("onAuthenticationSuccess:" + username);
		memRepo.save(Member.builder()
						.username(username)
						.password(encoder.encode("1a2s3d4f")) //인코더가 1a2s3d4f를 암호화(encode)해서 member객체생성함
		                .role(Role.ROLE_MEMBER) 
						.enabled(true).build()); //계정 활성화하고 객체생성끝냄
		
		String jwtToken = JWTUtil.getJWT(username);
		//응답 헤더에 추가하는 방식으로 헤더를 요청해서
		//만들어진 jwt토큰을 클라이언트에게 보내고,
		//클라이언트는 서버로부터 받아낸 이 jwt토큰을 로컬스토리지나 세션스토리지에 저장해서
		//서버가 다시 인증을 요청하면은 이 토큰을 사용하게됨 
		response.addHeader(HttpHeaders.AUTHORIZATION, jwtToken);
		response.sendRedirect("/loginSuccess");
	}

}
