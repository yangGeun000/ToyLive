package com.toy.live.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
	
	public enum Type{
		ENTER, MESSAGE, LEAVE;
	}
	
	private Type type; // 메세지 타입
	private String streamer; // 현재 스트리머
	private String sender; // 보낸 사람
	private String content; // 메세지 내용
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="HH:mm", timezone="Asia/Seoul")
	private LocalDateTime sendDate; // 보낸 시간
	
}
