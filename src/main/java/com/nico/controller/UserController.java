package com.nico.controller;

import com.alibaba.fastjson.JSON;
import com.nico.annotation.Router;
import com.nico.model.User;
import com.nico.model.UserDTO;
import com.nico.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller(value = "/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Router(name = "/getUser")
    public User getUser(UserDTO userDTO) {
        log.info("getUser {}", JSON.toJSON(userDTO));
        return userService.getUser(userDTO);
    }

    @Router(name = "/getName")
    public String getName(UserDTO userDTO) {
        return "name = " + JSON.toJSON(userDTO);
    }

    @Router(name = "/list")
    public List<User> list() {
        List<User> list = new ArrayList<>();
        User u = new User();
        u.setName("222");
        u.setId(2);
        list.add(u);
        list.add(u);
        return list;
    }

}
