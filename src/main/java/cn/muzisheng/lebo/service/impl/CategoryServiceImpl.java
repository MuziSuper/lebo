package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.mapper.CategoryMapper;
import cn.muzisheng.lebo.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
