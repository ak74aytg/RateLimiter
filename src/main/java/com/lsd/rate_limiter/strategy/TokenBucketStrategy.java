package com.lsd.rate_limiter.strategy;
import com.lsd.rate_limiter.factory.StrategyTypes;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketStrategy implements RateLimitStrategy {
    @Override
    public StrategyTypes getType() {
        return StrategyTypes.TOKEN_BUCKET;
    }

    @Override
    public boolean allow(String user) {
        return false;
    }
}
