package com.lsd.rate_limiter.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RedisService {
    private final Integer REQUEST_COUNT;
    private final Integer EXPIRY_DURATION;
    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> addScript;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        EXPIRY_DURATION = 5 * 60;
        this.redisTemplate = redisTemplate;
        this.REQUEST_COUNT = 100;
        this.addScript = new DefaultRedisScript<>();
        addScript.setScriptText("""
            redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1])
            local count = redis.call('ZCARD', KEYS[1])
            if count >= tonumber(ARGV[2]) then
                return 0
            end
            redis.call('ZADD', KEYS[1], ARGV[3], ARGV[4])
            redis.call('EXPIRE', KEYS[1], ARGV[5])
            return 1
        """);
        addScript.setResultType(Long.class);
    }

    public boolean AddUser(String user, Long currTime){
        user = "rate_limit:"+user;
        String member = UUID.randomUUID().toString();
        long oneMinuteBefore = currTime - 60 * 1000;
        Long result = redisTemplate.execute(
                addScript,
                List.of(user),
                String.valueOf(oneMinuteBefore),
                String.valueOf(REQUEST_COUNT),
                String.valueOf(currTime),
                member,
                String.valueOf(EXPIRY_DURATION)
        );
        return result == 1;
    }
}
