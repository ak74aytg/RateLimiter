package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.factory.StrategyTypes;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SlidingWindowStrategy implements RateLimitStrategy {
    private final RedisTemplate<String, String> redisTemplate;
    private final int EXPIRY_DURATION = 1;
    private final int REQUEST_COUNT = 100;
    private final DefaultRedisScript<Long> addScript;


    public SlidingWindowStrategy(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.addScript = new DefaultRedisScript<>();
        addScript.setScriptText("""
            redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1])
            local count = redis.call('ZCARD', KEYS[1])
            if count > tonumber(ARGV[2]) then
                return 0
            end
            redis.call('ZADD', KEYS[1], ARGV[3], ARGV[4])
            redis.call('EXPIRE', KEYS[1], ARGV[5])
            return 1
        """);
        addScript.setResultType(Long.class);
    }


    @Override
    public StrategyTypes getType() {
        return StrategyTypes.SLIDING_WINDOW;
    }

    @Override
    public boolean allow(String key) {
        Long currTime = System.currentTimeMillis();
        key = "rate_limit-sw:"+key;
        String member = UUID.randomUUID().toString();
        long oneMinuteBefore = currTime - 60 * 1000;
        Long result = redisTemplate.execute(
                addScript,
                List.of(key),
                String.valueOf(oneMinuteBefore),
                String.valueOf(REQUEST_COUNT),
                String.valueOf(currTime),
                member,
                String.valueOf(EXPIRY_DURATION)
        );
        return result == 1;
    }
}
