package cn.muzisheng.lebo.exception;

import org.springframework.stereotype.Component;

/**
 * 传参错误
 * code: 400
 **/
@Component
public class OrderException extends RuntimeException{
    public OrderException(){
    }
    public OrderException(String message){
        super(message);
    }
    public OrderException(String message, Throwable cause){
        super(message, cause);
    }
}
