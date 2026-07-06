package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.factory.StrategyTypes;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FixedWindowStrategy implements RateLimitStrategy {
    private final RedisTemplate<String, String> redisTemplate;
    private final int TTLInMinutes = 1;
    private final int REQUEST_COUNT = 100;

    public FixedWindowStrategy(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public StrategyTypes getType() {
        return StrategyTypes.FIXED_WINDOW;
    }

    @Override
    public boolean allow(String user) {
        String key = "rate_limit-fw:"+user;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1)
            redisTemplate.expire(key, Duration.ofMinutes(TTLInMinutes));

        return count <= REQUEST_COUNT;
    }
}
