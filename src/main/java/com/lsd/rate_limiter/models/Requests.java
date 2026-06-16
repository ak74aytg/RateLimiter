package com.lsd.rate_limiter.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Setter
@Getter
@Component
public class Requests {
    Map<String, UserRequestState> users;

    Requests(){
        users = new ConcurrentHashMap<>();
    }
}


