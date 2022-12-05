package com.toy.live.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.toy.live.dto.MemberDto;
import com.toy.live.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapper userMapper;

    public List<MemberDto> getMemberList() {
        return userMapper.getMemberList();
    }
    
    public void createMember(MemberDto member) {
    	userMapper.createMember(member);
    }
    
}
