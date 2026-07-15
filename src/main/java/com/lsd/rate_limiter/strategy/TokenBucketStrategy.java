package com.lsd.rate_limiter.strategy;
import com.lsd.rate_limiter.configuration.RateLimitPolicy;
import com.lsd.rate_limiter.factory.RateLimitPolicyProvider;
import com.lsd.rate_limiter.factory.StrategyTypes;
import com.lsd.rate_limiter.factory.UserPlan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBucketStrategy implements RateLimitStrategy {
    private int BUCKET_SIZE;
    private final RateLimitPolicyProvider  rateLimitPolicyProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenBucketStrategy(
            RateLimitPolicyProvider rateLimitPolicyProvider,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.rateLimitPolicyProvider = rateLimitPolicyProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public StrategyTypes getType() {
        return StrategyTypes.TOKEN_BUCKET;
    }

    @Override
    public boolean allow(String key, UserPlan plan) {
        RateLimitPolicy rateLimitPolicy = rateLimitPolicyProvider.getPolicy(plan);
        int expiryDuration = rateLimitPolicy.getEXPIRY_DURATION();
        int requestCount = rateLimitPolicy.getREQUEST_COUNT();
        double refillRatePerMilli = (double) requestCount / (60 * 1000);
        long currentTime = System.currentTimeMillis();
        switch (plan){
            case FREE -> BUCKET_SIZE = 100;
            case PREMIUM -> BUCKET_SIZE = 1000;
            case ENTERPRISE -> BUCKET_SIZE = 200000;
        }
        key = "rate_limit-tb:"+key;
        double tokens = BUCKET_SIZE;
        long lastRequestTime = System.currentTimeMillis();


        Object tokenObj = redisTemplate.opsForHash().get(key, "token");
        if (tokenObj != null){
            tokens = Double.parseDouble(tokenObj.toString());
            lastRequestTime = Long.parseLong(redisTemplate.opsForHash().get(key, "lastRequestTime").toString());
        }

        tokens = tokens + (currentTime - lastRequestTime) * refillRatePerMilli;
        tokens = Math.min(tokens, BUCKET_SIZE);

        if (tokens < 1) {
            return false;
        }
        tokens--;

        redisTemplate.opsForHash().put(key, "token", String.valueOf(tokens));
        redisTemplate.opsForHash().put(key, "lastRequestTime", String.valueOf(currentTime));
        redisTemplate.expire(key, Duration.ofMinutes(expiryDuration));
        return true;
    }
}
