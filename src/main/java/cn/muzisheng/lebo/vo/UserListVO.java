package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.entity.UserPoint;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表响应VO
 */
@Builder
@Data
public class UserListVO {
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别：0-保密，1-男，2-女
     */
    private Integer gender;

    /**
     * 城市
     */
    private String city;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：0-正常，1-不活跃，2-暂停，3-封禁，4-注销
     */
    private Integer status;

    /**
     * 当前可用积分
     */
    private Long currentPoint;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;

    /**
     * 将User实体转换为UserListVO
     */
    public static UserListVO of(User user, UserPoint userPoint) {
        return UserListVO.builder()
                .nickName(user.getNickName())
                .gender(user.getGender())
                .city(user.getCity())
                .age(user.getAge())
                .birthday(user.getBirthday())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus() != null ? user.getStatus().getCode() : null)
                .currentPoint(userPoint != null ? userPoint.getCurrentPoint() : 0L)
                .lastLogin(user.getLastLogin())
                .gmtCreated(user.getGmtCreated())
                .build();
    }
}
