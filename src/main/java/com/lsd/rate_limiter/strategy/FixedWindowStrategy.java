package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.configuration.RateLimitPolicy;
import com.lsd.rate_limiter.factory.RateLimitPolicyProvider;
import com.lsd.rate_limiter.factory.StrategyTypes;
import com.lsd.rate_limiter.factory.UserPlan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FixedWindowStrategy implements RateLimitStrategy {
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitPolicyProvider policyProvider;

    public FixedWindowStrategy(RedisTemplate<String, String> redisTemplate, RateLimitPolicyProvider policyProvider) {
        this.redisTemplate = redisTemplate;
        this.policyProvider = policyProvider;
    }

    @Override
    public StrategyTypes getType() {
        return StrategyTypes.FIXED_WINDOW;
    }

    @Override
    public boolean allow(String key, UserPlan plan) {
        RateLimitPolicy policy = policyProvider.getPolicy(plan);

        int ttl = policy.getTTL();
        int requestCount = policy.getREQUEST_COUNT();

        key = "rate_limit-fw:"+key;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1)
            redisTemplate.expire(key, Duration.ofMillis(ttl));

        return count <= requestCount;
    }
}
