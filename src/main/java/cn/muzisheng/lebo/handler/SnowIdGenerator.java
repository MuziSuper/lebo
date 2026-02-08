package cn.muzisheng.lebo.handler;

import cn.muzisheng.lebo.utils.SnowIdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class SnowIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        return SnowIdUtil.nextId();
    }
}
