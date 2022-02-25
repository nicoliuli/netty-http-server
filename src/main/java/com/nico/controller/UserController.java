package com.nico.controller;

import com.nico.annotation.Router;
import com.nico.model.User;
import com.nico.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

//@Router(name = "userController",value = "")
@Controller(value = "/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Router(name = "/getUser")
    public User getUser(Integer id) {
        log.info("getUser id = {}", id);
        return userService.getUser(id);
    }

    @Router(name = "/getName")
    public String getName(Integer id){
        return "name:"+id;
    }

}
