package edu.pnu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
	//restController - > html view 형식이 아닌 json으로 반환하게 됨
	//user 로 로그인을 해야 인덱스 접근 가능해짐 

	@GetMapping("/")	public String index() { return "index"; }
	
	@GetMapping("/member") public String member() { return "member"; }
	
	@GetMapping("/manager") public String manager() { return "manager"; }
	
	@GetMapping("/admin") public String admin() { return "admin"; }
	
}
