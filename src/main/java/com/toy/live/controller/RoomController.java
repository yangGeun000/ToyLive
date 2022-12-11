package com.toy.live.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toy.live.domain.Room;
import com.toy.live.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/room")
@RequiredArgsConstructor
@Controller
@Slf4j
public class RoomController {
	
	private final RoomService roomService;
	
	@RequestMapping("/list")
	public String getRoomList(Model model) {
		List<Room> roomList = this.roomService.findAll();
		model.addAttribute("roomList", roomList);
		return "room-list";
	}
	
	@GetMapping("/create")
	public String create() {
		return "room-create";
	}
	
	@PostMapping("/create")
	@ResponseBody
	public Room create(@RequestParam String subject, Principal principal) {
		System.out.println(subject);
		Room room = this.roomService.create(subject, principal.getName());
		return room;
	}
	
	@RequestMapping("/delete")
	public String delete(Principal principal) {
		this.roomService.delete(principal.getName());
		return "redirect:/";
	}
	
	@RequestMapping("/{streamer}")
	public String getRoom(Model model, @PathVariable String streamer) {
		Room room = this.roomService.findByStreamer(streamer);
		model.addAttribute("room", room);
		return "room-detail";
	}
	
}
