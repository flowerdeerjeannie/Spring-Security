package edu.pnu.config.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthorizatioFilter extends OncePerRequestFilter {
	//OncePerRequest필터를 상속받게 되면, 하나의 요청에 단 한번만 필터를 거치게 됨.
	//forwarding되어 다른 페이지로 이동하게 되더라도 이 필터 안 거침.

	private final MemberRepository memberRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String srcToken = request.getHeader("Authorization"); //요청 헤더에게서 Authorization를 얻어와서 srcToken에 저장함
		if (srcToken == null || !srcToken.startsWith("Bearer ")) { //토큰 널값이거나 Bearer로 시작 아니라면
			filterChain.doFilter(request, response); //filter를 그냥 통과
			return;
		}
		String jwtToken = srcToken.replace("Bearer ", ""); //Bearer를 공백으로 바꾸고 ->즉 제거하고 jwtToken에 저장
		
		String username = JWT.require(Algorithm.HMAC256("edu.pnu.jwt")).build().verify(jwtToken).getClaim("username").asString();
		//JWT에서 이름얻어가지고 username에 넣어놓고
		
		Optional<Member> opt = memberRepository.findById(username); //토큰에서 가져온 그 username으로 검색해서 opt에 넣음
		if(!opt.isPresent()) {	//opt가 존재하지 않는다면 
			filterChain.doFilter(request, response); //filter를 그냥 통과
			return;
		}
		Member findmember = opt.get();
		//DB에서 UserDetailsService 를 통해 사용자 정보를 읽은걸 가지고 userDetails가 뱉은 user에다가 그 정보를 저장해둠 
		User user = new User(findmember.getUsername(), findmember.getPassword(),
						AuthorityUtils.createAuthorityList(findmember.getRole().toString()));
		
		//Authentication 객체 생성함 - 사용자명과 권한 관리를 위한 정보를 입력함, 암호 필요 X
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		
		//Security세션에다가 그 사용자명이랑 권한들어가있는 auth객체를 입력해줌
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		filterChain.doFilter(request, response);
	}
}

