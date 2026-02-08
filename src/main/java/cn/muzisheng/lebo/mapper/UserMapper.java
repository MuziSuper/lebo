package cn.muzisheng.lebo.mapper;

import cn.muzisheng.lebo.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
