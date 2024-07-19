package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.persistance.MemberRepository;

@Service
public class BoardUserDetailsService implements UserDetailsService {

	//인증과 권한을 위한 UserDetails 인터페이스, 그리고 그걸 구현한 User 클래스.
	//세큐리티에서 사용자의 인증.권한 처리를 위하여 생성되는 객체 User
	//member에서 정보를 받아가지고 그 내용을 바탕으로 User 객체를 형성한다고 보면 됨
	
	//사용자 정보는 UserDetails 라는 객체에 저장되는데
	//그 userDetails에다가 검색한 사용자 정보를 저장하는 사이 계층 UserDetailsService(접근용)
	//검색하고 가져오고 하는 사이계층 userDetailsService가 있어야 접근, 저장이 가능함.
	//그거를 implements로 받아가지고 이 클래스에서 자세하게 구현한다고 보면 됨
	
	@Autowired
	private MemberRepository memRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//객체 UserDetails 타입으로 반환됨
		//근데 User는 UserDetails 인터페이스를 구현한 거기 때문에 다형성에 의해서
		//반환 타입이 new User, User 객체가 UserDetails 타입으로 반환될 수 있는거임! 상속받기 때문에 
		Member member = memRepo.findById(username).orElseThrow(()->new UsernameNotFoundException("Not Found"));
		
		System.out.println(member);
		
		//public class User implements UserDetails 이므로 다형성에 의해서
		//new User 객체가 세 가지 파라미터를 받는 거임-멤버 이름, 패스워드, 권한 목록
		return new User(member.getUsername(), member.getPassword(),
				//이렇게 되면 User 클래스에 이 내용이 들어가서 그 또 반환되는거임
				AuthorityUtils.createAuthorityList(member.getRole().toString()));
				//member객체에서 역할을 가져와서 권한 리스트로 바까줌 
		//*내가 놓친 거: User 객체가 생성되면 ( != member) 
	}
}
