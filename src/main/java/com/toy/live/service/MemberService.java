package com.toy.live.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.toy.live.domain.Member;
import com.toy.live.dto.MemberCreateDto;
import com.toy.live.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public List<Member> findAll() {
        return memberMapper.findAll();
    }
    
    public Optional<Member> findByName(String name) {
        return memberMapper.findByName(name);
    }
    
    public void create(MemberCreateDto memberCreateDto) {
    	Member member = new Member();
    	member.setName(memberCreateDto.getName());
    	member.setPassword(passwordEncoder.encode(memberCreateDto.getPassword1()));
		this.memberMapper.create(member);
    }
    
    public void delete(int id) {
    	this.memberMapper.delete(id);
    }
}
