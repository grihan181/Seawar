package org.example.seawar.controller;

import org.example.seawar.AssistClasses.RoomUsers;
import org.example.seawar.model.Rooms;
import org.example.seawar.model.Users;
import org.example.seawar.service.RoomService;
import org.example.seawar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "api/v1/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @GetMapping("{roomNumber}")
    public boolean checkRoom(@PathVariable String roomNumber, Model model) {
        List<Rooms> rooms = roomService.getRoomsByRoomNumber(roomNumber);
        if(rooms.size() > 0) {
            model.addAttribute("roomNumber", rooms.get(0));
            return true;
        }
        return false;
    }
    @GetMapping("{roomNumber}/{username}")
    public boolean checkUser(@PathVariable String roomNumber,
                             @PathVariable String username,
                             @RequestParam String password, Model model) {
        List<Rooms> rooms = roomService.getRoomsByRoomNumber(roomNumber);
        Rooms room = rooms.get(0);
        List<Users> users = userService.getUsersByRoomsId(room);
        if(users.size() > 0) {
            for(Users user: users) {
                if(user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PostMapping
    public RoomUsers makeRoom(Model model) {
        RoomUsers roomUsers;
        Users user1;
        Users user2;
        Rooms room;

        String roomNumber = "";
        boolean flag = false;

        while (!flag) {
            roomNumber = getRandomNumberString(6);
            if (roomService.getRoomsByRoomNumber(roomNumber).size() == 0) {
                flag = true;
            }
        }
        room = new Rooms(roomNumber);
        roomService.addRoom(room);

        user1 = new Users("user1", getRandomNumberString(4), room);
        userService.addUser(user1);
        user2 = new Users("user2", getRandomNumberString(4), room);
        userService.addUser(user2);
        roomUsers = new RoomUsers(room, user1, user2);


        return roomUsers;
    }



    public static String getRandomNumberString(int length) {
        Random rnd = new Random();
        int number;
        if (length == 6) {
            number  = rnd.nextInt(999999);
            return String.format("%06d", number);
        } else if  (length == 4) {
            number  = rnd.nextInt(9999);
            return String.format("%04d", number);
        }
        return "0";
    }
}
