package com.lsd.rate_limiter.service;

import com.lsd.rate_limiter.models.Requests;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

@Service
public class SlidingWindowRateLimiter {
    private final Requests model;

    public SlidingWindowRateLimiter(Requests model) {
        this.model = model;
    }


    public Boolean RateLimiter(String user){
        HashMap<String, Deque<Long>> userList = model.getUsers();
        long milliseconds = System.currentTimeMillis();

        if (!userList.containsKey(user)){
            userList.put(user, new ArrayDeque<>());
        }
        userList.get(user).add(milliseconds);

        long lastMinute = milliseconds - 60 * 1000;
        while (userList.get(user).peek() < lastMinute ) {
            userList.get(user).pop();
        }

        long requestCount = userList.get(user).size();
        return requestCount <= 100;
    }
}
