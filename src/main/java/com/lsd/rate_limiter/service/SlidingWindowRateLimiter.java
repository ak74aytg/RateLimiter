package com.lsd.rate_limiter.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlidingWindowRateLimiter {
    private final RedisService redis;

    public SlidingWindowRateLimiter(RedisService redis) {
        this.redis = redis;
    }


    public Boolean RateLimiter(String user){
        long currTime = System.currentTimeMillis();
        return redis.AddUser(user, currTime);
    }
}
