package com.lsd.rate_limiter.service;

import com.lsd.rate_limiter.models.Requests;
import com.lsd.rate_limiter.models.UserRequestState;
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
        HashMap<String, UserRequestState> userList = model.getUsers();
        long milliseconds = System.currentTimeMillis();

        if (!userList.containsKey(user)){
            UserRequestState userRequestState = new UserRequestState();
            userRequestState.getTimestamp().add(milliseconds);
            userRequestState.setLastSeenAt(milliseconds);

            userList.put(user, userRequestState);
        }
        userList.get(user).getTimestamp().add(milliseconds);
        userList.get(user).setLastSeenAt(milliseconds);

        long lastMinute = milliseconds - 60 * 1000;
        while (userList.get(user).getTimestamp().peek() < lastMinute ) {
            userList.get(user).getTimestamp().pop();
        }

        long requestCount = userList.get(user).getTimestamp().size();
        return requestCount <= 100;
    }
}
