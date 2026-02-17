package cn.muzisheng.lebo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<D> implements Serializable {

    private D data;
    private String error;
    private LocalDateTime time=LocalDateTime.now();
}