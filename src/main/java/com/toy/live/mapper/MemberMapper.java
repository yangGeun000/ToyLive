package com.toy.live.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.toy.live.domain.Member;

@Mapper
public interface MemberMapper {
    public List<Member> findAll();
    public void create(Member member);
    public Optional<Member> findByName(String name);
    public void delete(Long id);
}
