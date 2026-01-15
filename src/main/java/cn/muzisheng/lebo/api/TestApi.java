package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.entity.UserPoint;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestApi {
    @GetMapping("user/info")
    public String getUserInfo() {
        User user = new User();
        user.setNickName("muzisheng");
        user.setWxId("123456789");
        user.setStatus(AccountStatusEnum.ACTIVE);
        user.setGender(1);
        user.setCity("上海");
        user.setGrade(1);
        user.setBirthday("1990-01-01");
        user.setEmail("1124372834@qq.com");
        user.setPhone("15568454132");
        UserPoint userPoint = new UserPoint();
        userPoint.setUserId("1");
        userPoint.setTotalPoint(123456L);
        userPoint.setAccumulatedPoint(1234567L);
        userPoint.setId(33L);
        user.setPoint(userPoint);
        return user.toString();
    }

}