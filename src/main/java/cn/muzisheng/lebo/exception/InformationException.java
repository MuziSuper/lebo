package cn.muzisheng.lebo.exception;

import org.springframework.stereotype.Component;

@Component
public class InformationException extends RuntimeException {
    public InformationException() {
    }
    
    public InformationException(String message) {
        super(message);
    }
    
    public InformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
