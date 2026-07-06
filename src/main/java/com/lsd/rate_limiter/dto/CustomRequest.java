package com.lsd.rate_limiter.dto;

import com.lsd.rate_limiter.factory.StrategyTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomRequest {
    private String userId;
    private StrategyTypes policy;
}
