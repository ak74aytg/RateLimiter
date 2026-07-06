package com.lsd.rate_limiter.factory;

import com.lsd.rate_limiter.exception.NotFoundException;
import com.lsd.rate_limiter.strategy.RateLimitStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimitStrategyFactory {
    private final Map<StrategyTypes, RateLimitStrategy> strategyMap;

    public RateLimitStrategyFactory(List<RateLimitStrategy> strategyList) {
        strategyMap = new HashMap<>();
        for (RateLimitStrategy strategy : strategyList){
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public RateLimitStrategy getStrategy(StrategyTypes type){
        RateLimitStrategy strategy = strategyMap.get(type);
        if (strategy == null){
            throw new NotFoundException("Policy not found!");
        }
        return strategy;
    }
}
