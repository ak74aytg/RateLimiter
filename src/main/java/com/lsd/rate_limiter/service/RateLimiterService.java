package com.lsd.rate_limiter.service;

import com.lsd.rate_limiter.factory.RateLimitStrategyFactory;
import com.lsd.rate_limiter.factory.StrategyTypes;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    public final RateLimitStrategyFactory rateLimitStrategyFactory;

    public RateLimiterService(RateLimitStrategyFactory rateLimitStrategyFactory) {
        this.rateLimitStrategyFactory = rateLimitStrategyFactory;
    }


    public boolean check(String user, StrategyTypes policy){
        return rateLimitStrategyFactory.getStrategy(policy).allow(user);
    }
}
