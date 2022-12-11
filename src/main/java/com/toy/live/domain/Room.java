package com.toy.live.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {
	private String streamer; // 스트리머
	private String subject; // 방송제목
	private Set<WebSocketSession> sessions = new HashSet<>(); // 시청자들
	private LocalDateTime startDate; // 방송시작시간
	
}
