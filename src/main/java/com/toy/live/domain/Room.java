package com.toy.live.domain;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Room {
	private String streamer; // 스트리머
	private String subject; // 방송제목
	private int count; // 시청인원 
	private Map<String, String> views = new LinkedHashMap<>(); // 시청자들
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd HH:MM", timezone="Asia/Seoul")
	private LocalDateTime startDate; // 방송시작시간
	
}
