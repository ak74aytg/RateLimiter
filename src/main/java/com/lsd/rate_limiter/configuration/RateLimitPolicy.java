package com.lsd.rate_limiter.configuration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimitPolicy {
    private final int REQUEST_COUNT;
    private final int TTL;
    private final int EXPIRY_DURATION;


    public RateLimitPolicy(int requestCount, int ttl, int expiryDuration) {
        REQUEST_COUNT = requestCount;
        TTL = ttl;
        EXPIRY_DURATION = expiryDuration;
    }
}
