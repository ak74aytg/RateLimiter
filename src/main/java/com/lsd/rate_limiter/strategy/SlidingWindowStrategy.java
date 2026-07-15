package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.configuration.RateLimitPolicy;
import com.lsd.rate_limiter.factory.RateLimitPolicyProvider;
import com.lsd.rate_limiter.factory.StrategyTypes;
import com.lsd.rate_limiter.factory.UserPlan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SlidingWindowStrategy implements RateLimitStrategy {
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitPolicyProvider policyProvider;
    private final DefaultRedisScript<Long> addScript;


    public SlidingWindowStrategy(RedisTemplate<String, String> redisTemplate, RateLimitPolicyProvider policyProvider) {
        this.redisTemplate = redisTemplate;
        this.policyProvider = policyProvider;
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
    public boolean allow(String key, UserPlan plan) {
        RateLimitPolicy policy = policyProvider.getPolicy(plan);
        int ttl = policy.getTTL();
        int requestCount = policy.getREQUEST_COUNT();
        int expiryDuration = policy.getEXPIRY_DURATION();

        Long currTime = System.currentTimeMillis();
        key = "rate_limit-sw:"+key;
        String member = UUID.randomUUID().toString();
        long startTime = currTime - ttl;
        Long result = redisTemplate.execute(
                addScript,
                List.of(key),
                String.valueOf(startTime),
                String.valueOf(requestCount),
                String.valueOf(currTime),
                member,
                String.valueOf(expiryDuration)
        );
        return result == 1;
    }
}
