package cn.muzisheng.lebo.exception;

/**
 * 存储异常
 * code: 511
 **/
public class StorageException extends RuntimeException{
    public StorageException() {
    }
    public StorageException(String message) {
        super(message);
    }
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
