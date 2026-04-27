package cn.muzisheng.lebo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserOpenIdsDTO {
    /**
     * 用户 openId 列表
     */
    private List<String> openIds;
}
