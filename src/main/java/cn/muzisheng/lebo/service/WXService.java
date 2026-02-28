package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.User;
import cn.muzisheng.lebo.param.WXCodeSession;

public interface WXService {
    WXCodeSession code2Session(String code);
    String getStableAccessToken();
    boolean checkSession(User user);

    WXCodeSession resetSession(User user);
}
