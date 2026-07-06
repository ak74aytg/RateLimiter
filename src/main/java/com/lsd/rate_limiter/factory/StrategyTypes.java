package com.lsd.rate_limiter.factory;

import com.lsd.rate_limiter.exception.InternalServerException;
import com.lsd.rate_limiter.exception.NotFoundException;

public enum StrategyTypes {
    FIXED_WINDOW,
    SLIDING_WINDOW,
    TOKEN_BUCKET;
}
