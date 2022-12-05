package com.toy.live.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toy.live.dto.MemberDto;
import com.toy.live.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {
	
	private final MemberService userService;
	
	@RequestMapping("/")
	@ResponseBody
	public List<MemberDto> getMemberList() {
		return userService.getMemberList();
	}
	
	@RequestMapping("/create")
	public String createUser() {
		MemberDto member = new MemberDto();
		member.setName("mybatis");
		member.setPassword("1234");
		userService.createMember(member);
		return "redirect:/";
	}
	
}
