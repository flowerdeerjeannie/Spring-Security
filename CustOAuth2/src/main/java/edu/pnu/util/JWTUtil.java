package edu.pnu.util;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;



public class JWTUtil {
	//JWT 토큰을 생성하고 검증하는 클래스임
	//JWT 토큰을 생성하고, 클레임 추출하고, 만료되었는지 확인하는 기능
	private static final long ACCESS_TOKEN_MSEC = 10* (60 * 1000); //설정시간-10분
	private static final String JWT_KEY = "edu.pnu.jwtkey"; //인코딩을 위한 키
	private static final String claimName = "username"; //토큰에 담을 정보의 키값
	private static final String prefix = "Bearer "; //jWT토큰 헤더 문자열 Bearer
	
	private static String getJWTSource(String token) {
		if (token.startsWith(prefix)) return token.replace(prefix, "");
		return token;
	}
	
	public static String getJWT(String username) {
		String src = JWT.create()
						.withClaim(claimName, username)
						.withExpiresAt(new Date(System.currentTimeMillis()+ACCESS_TOKEN_MSEC))
						.sign(Algorithm.HMAC256(JWT_KEY));
		return prefix + src;
	}
	
	public static String getClaim(String token) {
		//토큰에 담긴 정보 중에서 key가 "username"인 데이터 가져온다.
		String tok = getJWTSource(token);
		return JWT.require(Algorithm.HMAC256(JWT_KEY)).build().verify(tok).getClaim(claimName).asString();
		
	}
	
	public static boolean isExpired(String token) {
		String tok = getJWTSource(token);
		//유효기간 만료 여부.
		return JWT.require(Algorithm.HMAC256(JWT_KEY)).build().verify(tok).getExpiresAt().before(new Date());
	}
}
