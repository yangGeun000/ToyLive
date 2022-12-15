package com.toy.live.controller;

import java.security.Principal;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.toy.live.domain.Member;
import com.toy.live.dto.MemberCreateDto;
import com.toy.live.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
@Slf4j
public class MemberController {
	
	private final MemberService memberService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/sign-up")
	public String signUp(MemberCreateDto memberCreateDto) {
		return "sign-up";
	}
	
	@PostMapping("/sign-up") // 회원가입 요청
    public String signUp(@Valid MemberCreateDto memberCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        if (!memberCreateDto.getPassword1().equals(memberCreateDto.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "패스워드가 일치하지 않습니다.");
            return "sign-up";
        }
        
        try {
        	memberService.create(memberCreateDto);
        }
        catch(DataIntegrityViolationException e) { // name 중복에러
            e.printStackTrace();
            bindingResult.reject("signUpFailed", "이미 등록된 사용자입니다.");
            return "sign-up";
        }
        catch(Exception e) { // 나머지 예외오류
            e.printStackTrace();
            bindingResult.reject("signUpFailed", e.getMessage());
            return "sign-up";
        }

        return "redirect:/";
    }
	
	@GetMapping("/delete")
	public String delete(Principal principal) {
		Optional<Member> opMember = this.memberService.findByName(principal.getName());
		Member member = opMember.get();
		try {
			this.memberService.delete(member.getId());
        }
        catch(Exception e) {
        	log.error("멤버 삭제 실패 => " + principal.getName());
            e.printStackTrace();
        }
		
		return "redirect:/member/logout";
	}
	
}
