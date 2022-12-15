package com.toy.live.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.toy.live.domain.Member;
import com.toy.live.domain.Room;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoomService {
	// 현재 생성된 방들
	private Map<String,Room> rooms;
	
	@PostConstruct
    private void init() {
        rooms = new LinkedHashMap<>();
    }
	
	// 방송중인 방 전부 조회
    public List<Room> findAll() {
    	List<Room> roomList = new ArrayList<>(rooms.values());
    	// 최근순으로 정렬
    	Collections.reverse(roomList);
        return roomList;
    }
    
    // 스트리머 기준 방 찾기
    public Room findByStreamer(String streamer) {
        return rooms.get(streamer);
    }
    
    // 시청자가 입장하면 시청자수 증가하고 방 views에 멤버 추가
    public String EnterRoom(String streamer, String memberName) {
    	Room room = rooms.get(streamer);
    	String memberUUID = UUID.randomUUID().toString();
    	System.out.println(room.getViews().toString());
    	room.getViews().put(memberUUID, memberName);
    	room.setCount(room.getCount()+1);
    	return memberUUID;
    }
    
    // 시청자가 나가면 시청자수 감소하고 방 views에 멤버 삭제
    public String LeaveRoom(String streamer, String memberUUID) {
    	Room room = rooms.get(streamer);
    	String memberName = streamer;
    	if(room != null) {
    	memberName = room.getViews().get(memberUUID);
    	room.getViews().remove(memberUUID);
    	room.setCount(room.getCount()-1);
    	}
    	return memberName;
    }
	
    // 방 생성
	public Room create(String subject, String streamer) {
		Room room = new Room();
		room.setStreamer(streamer);
		room.setCount(0);
		room.setSubject(subject);
		room.setStartDate(LocalDateTime.now());
		rooms.put(streamer, room);
		return room;
	}
	
	// 방 삭제
	public void delete(String streamer) {
		rooms.remove(streamer);
	}
}
