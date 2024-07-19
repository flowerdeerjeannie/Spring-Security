package edu.pnu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//현재 user 비밀번호 만들어졌고 연결되었지만 http의 요청을 처리해 줄 컨트롤러가 없어서
//404가 뜨는 상황임. controller를 작성해서 요청 처리하고 반환해줘야 함

@Controller
public class SecurityController {

	@GetMapping({"/","/index"})
	public String index() {
		System.out.println("index 요청");
		return "index";
	} //return "index" 는 index.html 화면으로 호출된다는 말
	//string이라 return타입이 있어야 해서 이렇게 작성했고
	//나머지 void 메소드는 "/member" 했을때 이동될 member.html을 바로 말하는거임
	
	@GetMapping("/member")
	public void member() {
		//sysout은 콘솔에 출력될 내용
		System.out.println("member 요청");
	}
	
	@GetMapping("/manager")
	public void manager() {
		System.out.println("Manager 요청");		
	}
	
	@GetMapping("/admin")
	public void admin() {
		System.out.println("Admin 요청");		
	}

}
