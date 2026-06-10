package com.lsd.rate_limiter.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.HashMap;


@Setter
@Getter
@Component
public class Requests {
    HashMap<String, Deque<Long>> users;

    Requests(){
        users = new HashMap<>();
    }
}
