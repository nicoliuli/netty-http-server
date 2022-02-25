package com.nico.service;

import com.nico.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    static {
        log.info("init userService");
    }
    public User getUser(Integer id){
        User user = new User();
        user.setId(id);
        user.setName("张三:" + id);
        return user;
    }
}
