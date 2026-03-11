package cn.muzisheng.lebo.vo;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.entity.UserPoint;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class LoginVO {
    private String nickName;
    private String avatar;
    private Long AccumulatedPoint;
    private Integer gender;
    private String city;
    private String email;
    private Integer age;
    private String phone;
    private String birthday;
    private Integer statusCode;
    public static LoginVO of(User user, UserPoint userPoint){
        return LoginVO.builder()
                .nickName(user.getNickName())
                .avatar(user.getAvatar())
                .AccumulatedPoint(userPoint.getAccumulatedPoint())
                .gender(user.getGender())
                .city(user.getCity())
                .email(user.getEmail())
                .age(user.getAge())
                .phone(user.getPhone())
                .birthday(user.getBirthday())
                .statusCode(user.getStatus().getCode())
                .build();
    }
}
