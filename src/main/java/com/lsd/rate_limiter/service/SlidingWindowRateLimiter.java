package com.lsd.rate_limiter.service;

import com.lsd.rate_limiter.models.Requests;
import com.lsd.rate_limiter.models.UserRequestState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlidingWindowRateLimiter {
    private final Requests model;

    public SlidingWindowRateLimiter(Requests model) {
        this.model = model;
    }


    public Boolean RateLimiter(String user){
        Map<String, UserRequestState> userList = model.getUsers();
        long milliseconds = System.currentTimeMillis();

        UserRequestState mapUser = userList.computeIfAbsent(user, k -> new UserRequestState());
        Queue<Long> timeStampQueue = mapUser.getTimestamp();

        long lastMinute = milliseconds - 60 * 1000;
        while (!timeStampQueue.isEmpty() && timeStampQueue.peek() < lastMinute ) {
            timeStampQueue.poll();
        }

        long requestCount = timeStampQueue.size();
        if (requestCount > 100) return false;


        mapUser.setLastSeenAt(milliseconds);

        synchronized (timeStampQueue) {
            timeStampQueue.add(milliseconds);
            requestCount = timeStampQueue.size();
        }

        return requestCount <= 100;
    }
}
