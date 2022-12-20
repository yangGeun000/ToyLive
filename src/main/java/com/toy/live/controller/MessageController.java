package com.toy.live.controller;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.toy.live.dto.MessageDto;
import com.toy.live.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MessageController {

	private final SimpMessageSendingOperations template;
	private final RoomService roomSerivce;

	// 소켓 연결되었을 때 클라이언트가 메세지 발행
	@MessageMapping("/enter")
	public void enter(MessageDto message, SimpMessageHeaderAccessor accessor) {
		// 방에 시청자수 증가, views에 멤버 추가하고 UUID 반환
		String memberUUID = this.roomSerivce.EnterRoom(message.getStreamer(), message.getSender());

		// socket sesstion에 주입
		accessor.getSessionAttributes().put("memberUUID", memberUUID);
		accessor.getSessionAttributes().put("streamer", message.getStreamer());

		if (message.getStreamer().equals(message.getSender())) {
			message.setContent("방송을 시작합니다.");
		} else {
			message.setContent(message.getSender() + "님이 입장했습니다.");
		}
		message.setSendDate(LocalDateTime.now());
		template.convertAndSend("/sub/" + message.getStreamer(), message);
	}

	// 클라이언트가 보낸 메세지를 타겟에게 다시 보내기
	@MessageMapping("/send/{streamer}")
	public void send(MessageDto message, @DestinationVariable String streamer) {
		log.info("sendMessage : " + message.toString());
		message.setSendDate(LocalDateTime.now());
		template.convertAndSend("/sub/" + streamer, message);
	}

	@MessageMapping("/webrtc/{name}")
	public void webrtc(@Payload String message, @DestinationVariable String name) {
		log.info("sendMessage : " + message);
		template.convertAndSend("/sub/webrtc/" + name, message);
	}

	// 연걸이 끊기는 이벤트를 캐치
	@EventListener
	public void leave(SessionDisconnectEvent event) {
		log.info("leave");
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

		// socket sesstion에서 필요한 정보 얻기
		String memberUUID = (String) accessor.getSessionAttributes().get("memberUUID");
		String streamer = (String) accessor.getSessionAttributes().get("streamer");

		// 방 시청자수 감소, views 멤버 삭제
		String memberName = this.roomSerivce.LeaveRoom(streamer, memberUUID);

		MessageDto message = MessageDto.builder().type(MessageDto.Type.LEAVE).sender(memberName)
				.content(memberName + "님이 퇴장했습니다.").build();

		if (streamer.equals(memberName)) {
			this.roomSerivce.delete(memberName);
			message.setContent("방송이 종료되었습니다.");
		}
		template.convertAndSend("/sub/" + streamer, message);
	}

}
