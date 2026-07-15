package com.lsd.rate_limiter.strategy;

import com.lsd.rate_limiter.factory.StrategyTypes;
import com.lsd.rate_limiter.factory.UserPlan;

public interface RateLimitStrategy {
    StrategyTypes getType();
    boolean allow(String key, UserPlan plan);
}
