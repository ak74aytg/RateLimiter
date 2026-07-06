package com.lsd.rate_limiter.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(){
        super("Server encountered an error. Please contact customer support");
    }
}
