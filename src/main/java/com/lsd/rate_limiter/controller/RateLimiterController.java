package com.lsd.rate_limiter.controller;

import com.lsd.rate_limiter.dto.CustomRequest;
import com.lsd.rate_limiter.dto.CustomResponse;
import com.lsd.rate_limiter.service.RateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RateLimiterController {
    private final RateLimiterService rateLimiterService;

    RateLimiterController(RateLimiterService rateLimiterService){
        this.rateLimiterService = rateLimiterService;
    }


    @PostMapping("/allow")
    public ResponseEntity<CustomResponse> allow(@RequestBody CustomRequest request) {
        boolean allowed = rateLimiterService.check(request.getUserId(), request.getPolicy(), request.getPlan());
        CustomResponse response = new CustomResponse();
        response.setAllowed(allowed);
        if (allowed) return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
