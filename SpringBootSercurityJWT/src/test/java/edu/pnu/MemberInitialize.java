package edu.pnu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.pnu.domain.Member;
import edu.pnu.domain.Role;
import edu.pnu.persistence.MemberRepository;

@SpringBootTest
public class MemberInitialize {

	
	@Autowired
	MemberRepository memRepo;
	
	//부트가 제공해주는 암호화 인터페이스임 encode 되도록
	PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	//memRepo에 데이터 넣기 위한 코드 
	
	@Test
	public void doWork() {
		memRepo.save(Member.builder()
				.username("member")
				.password(encoder.encode("abcd"))
				.role(Role.ROLE_MEMBER)
				.enabled(true).build());
		memRepo.save(Member.builder()
				.username("manager")
				.password(encoder.encode("abcd"))
				.role(Role.ROLE_MANAGER)
				.enabled(true).build());
		memRepo.save(Member.builder()
				.username("admin")
				.password(encoder.encode("abcd"))
				.role(Role.ROLE_ADMIN)
				.enabled(true).build());
	}
}