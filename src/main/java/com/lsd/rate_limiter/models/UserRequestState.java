package com.lsd.rate_limiter.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Deque;

@Setter
@Getter
public class UserRequestState {
    private Deque<Long> timestamp;
    private Long lastSeenAt;
}
