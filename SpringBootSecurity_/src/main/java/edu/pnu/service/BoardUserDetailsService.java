package edu.pnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.pnu.domain.Member;
import edu.pnu.persistence.MemberRepository;

@Service
public class BoardUserDetailsService implements UserDetailsService {

	@Autowired
	private MemberRepository memRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//memRepo에서 사용자 정보를 검색해서
		Member member = memRepo.findById(username)
						.orElseThrow(()->new UsernameNotFoundException("Not Found"));
		System.out.println(member);
		
		//여기에서 리턴된 user객체와 로그인 요청 정보를 비교한다. 
		//User는 userDatils(인터페이스)를 상속받은 클래스인데
		//그 클래스의 객체 중 하나 user객체 형태로 return하는거임!! 그래서 new User
		return new User(member.getUsername(), member.getPassword(),
				AuthorityUtils.createAuthorityList(member.getRole().toString()));
		
		//로그인을하잔아? 그럼 그 USER객체가 session 안에 있는 security context에 저장이 되어서
		//객체로서 비교하게 되는거임 
	}
}
