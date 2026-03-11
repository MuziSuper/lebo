package cn.muzisheng.lebo.exception;
/**
 * 用户积分异常
 * code: 501
 **/
public class UserPointException extends RuntimeException{
    public UserPointException() {
    }
    public UserPointException(String message) {
        super(message);
    }
    public UserPointException(String message, Throwable cause) {
        super(message, cause);
    }

}
