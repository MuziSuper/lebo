package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateVO {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别,0-保密，1-男，2-女
     */
    private Integer gender;
    /**
     * 城市,默认省级行政区
     */
    private String city;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 生日,2023-01-01格式
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
     * 状态,0-正常，1-不活跃，2-暂停，3-封禁，4-注销
     */
    private AccountStatusEnum status;
    /**
     * 将UserUpdateVO转换为User
     * @param userUpdateVO 用户更新信息
     * @return User
     */
    public static User of(UserUpdateVO userUpdateVO){
        return User.builder()
                .nickName(userUpdateVO.getNickName())
                .avatar(userUpdateVO.getAvatar())
                .gender(userUpdateVO.getGender())
                .city(userUpdateVO.getCity())
                .age(userUpdateVO.getAge())
                .birthday(userUpdateVO.getBirthday())
                .email(userUpdateVO.getEmail())
                .phone(userUpdateVO.getPhone())
                .status(userUpdateVO.getStatus())
                .build();
    }
}
