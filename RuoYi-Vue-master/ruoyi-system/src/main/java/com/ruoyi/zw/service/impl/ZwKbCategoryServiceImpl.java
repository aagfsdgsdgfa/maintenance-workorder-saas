package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwKbCategory;
import com.ruoyi.zw.mapper.ZwKbCategoryMapper;
import com.ruoyi.zw.service.IZwKbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwKbCategoryServiceImpl implements IZwKbCategoryService {

    @Autowired
    private ZwKbCategoryMapper kbCategoryMapper;

    @Override
    public List<ZwKbCategory> selectZwKbCategoryList(ZwKbCategory category) {
        return kbCategoryMapper.selectZwKbCategoryList(category);
    }

    @Override
    public ZwKbCategory selectZwKbCategoryById(Long categoryId) {
        return kbCategoryMapper.selectZwKbCategoryById(categoryId);
    }

    @Override
    public List<ZwKbCategory> selectAllKbCategory() {
        return kbCategoryMapper.selectAllKbCategory();
    }

    @Override
    public int insertZwKbCategory(ZwKbCategory category) {
        category.setDelFlag("0");
        return kbCategoryMapper.insertZwKbCategory(category);
    }

    @Override
    public int updateZwKbCategory(ZwKbCategory category) {
        return kbCategoryMapper.updateZwKbCategory(category);
    }

    @Override
    public int deleteZwKbCategoryByIds(Long[] categoryIds) {
        return kbCategoryMapper.deleteZwKbCategoryByIds(categoryIds);
    }

    @Override
    public boolean checkCategoryCodeUnique(ZwKbCategory category) {
        return kbCategoryMapper.checkCategoryCodeUnique(category) == 0;
    }
}
