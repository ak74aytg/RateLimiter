package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.factory.StrategyTypes;

public interface RateLimitStrategy {
    StrategyTypes getType();
    boolean allow(String key);
}
