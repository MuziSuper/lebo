package cn.muzisheng.lebo.dto;

import lombok.Data;

/**
 * 用户列表查询参数
 */
@Data
public class UserListDTO {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 昵称（模糊查询）
     */
    private String nickName;

    /**
     * 手机号尾号四位
     */
    private String phoneSuffix;

    /**
     * 状态：0-正常，1-不活跃，2-暂停，3-封禁，4-注销
     */
    private Integer status;

    /**
     * 性别：0-保密，1-男，2-女
     */
    private Integer gender;
}
