package cn.muzisheng.lebo.exception;
/**
 * 用户异常
 * code: 503
 */
public class UserException extends RuntimeException{
    public UserException() {
    }
    public UserException(String message) {
        super(message);
    }
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
