package com.toy.live.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.toy.live.MemberRole;
import com.toy.live.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberSecurityService implements UserDetailsService{

	private final MemberService memberService;
    
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<Member> opMember = this.memberService.findByName(name);
        if (opMember.isEmpty()) {
        	log.error("사용자를 찾을 수 없습니다 => " + name);
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        Member member = opMember.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(name)) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
        }
    	
        return new User(member.getName(), member.getPassword(), authorities);
    }
}
