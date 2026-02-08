package cn.muzisheng.lebo.exception;

import org.springframework.stereotype.Component;
/**
 * 传参错误
 * code: 502
 **/
@Component
public class SQLException extends RuntimeException {
    public SQLException(String message) {
        super(message);
    }
    public SQLException(String message, Throwable cause) {
        super(message, cause);
    }
}
