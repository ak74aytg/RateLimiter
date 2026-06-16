package com.lsd.rate_limiter.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Queue;

@Setter
@Getter
public class UserRequestState {
    private Queue<Long> timestamp;
    private Long lastSeenAt;

    public UserRequestState(){
        this.timestamp = new ArrayDeque<>();
    }
}
