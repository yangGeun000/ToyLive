package com.toy.live.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.toy.live.dto.MemberDto;

@Mapper
public interface MemberMapper {
    public List<MemberDto> getMemberList();
    public void createMember(MemberDto member);
}
