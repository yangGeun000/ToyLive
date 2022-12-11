package com.toy.live.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.toy.live.domain.Member;
import com.toy.live.domain.Room;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoomService {
	private Map<String,Room> rooms;
	
	@PostConstruct
    private void init() {
        rooms = new LinkedHashMap<>();
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
    
    public Room findByStreamer(String streamer) {
        return rooms.get(streamer);
    }
	
	public Room create(String subject, String streamer) {
		Room room = Room.builder()
				.streamer(streamer)
				.subject(subject)
				.startDate(LocalDateTime.now())
				.build();
		
		rooms.put(streamer, room);
		return room;
	}
	
	public void delete(String streamer) {
		rooms.remove(streamer);
	}
}
