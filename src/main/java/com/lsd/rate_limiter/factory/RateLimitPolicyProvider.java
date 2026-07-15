package com.lsd.rate_limiter.factory;

import com.lsd.rate_limiter.configuration.RateLimitPolicy;
import org.springframework.stereotype.Component;

@Component
public class RateLimitPolicyProvider {
    public RateLimitPolicy getPolicy(UserPlan plan) {
        return switch (plan){
            case FREE ->  new RateLimitPolicy(100, 60 * 1000, 1);
            case PREMIUM -> new RateLimitPolicy(1000, 60 * 1000, 1);
            case ENTERPRISE -> new RateLimitPolicy(100000, 10* 60 * 1000, 10);
        };
    }
}
