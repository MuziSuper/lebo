package cn.muzisheng.lebo.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ExceptionResponse {
    private int status;
    private String message;
    private LocalDateTime time=LocalDateTime.now();
    public ExceptionResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
