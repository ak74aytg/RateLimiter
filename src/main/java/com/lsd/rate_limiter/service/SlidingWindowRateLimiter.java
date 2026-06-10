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
        HashMap<String, Long> blockList = model.getBlockUsers();
        long milliseconds = System.currentTimeMillis();
        if (blockList.containsKey(user)){
            if (blockList.get(user) + 60 * 1000 < milliseconds) {
                blockList.remove(user);
            }else{
                return false;
            }
        }

        if (!userList.containsKey(user)){
            userList.put(user, new ArrayDeque<>());
        }
        userList.get(user).add(milliseconds);

        long lastMinute = milliseconds - 60 * 1000;
        while (userList.get(user).peek() < lastMinute ) {
            userList.get(user).pop();
        }

        long requestCount = userList.get(user).size();
        if (requestCount > 100){
            blockList.putIfAbsent(user, milliseconds);
            return false;
        }
        return true;
    }
}
