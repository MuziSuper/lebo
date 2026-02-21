package cn.muzisheng.lebo.exception;

import org.springframework.stereotype.Component;

/**
 * message: 微信异常
 * code: 415
 * @author 煲崽
 */
@Component
public class WXException extends RuntimeException{

    public WXException() {
        super();
    }
    public WXException(String message) {
        super(message);
    }
    public WXException(String message, Throwable cause) {
        super(message, cause);
    }
    public WXException(Throwable cause) {
        super(cause);
    }
}
