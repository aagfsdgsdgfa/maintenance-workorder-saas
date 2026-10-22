package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwKbCategory;
import java.util.List;

public interface IZwKbCategoryService {
    List<ZwKbCategory> selectZwKbCategoryList(ZwKbCategory category);
    ZwKbCategory selectZwKbCategoryById(Long categoryId);
    List<ZwKbCategory> selectAllKbCategory();
    int insertZwKbCategory(ZwKbCategory category);
    int updateZwKbCategory(ZwKbCategory category);
    int deleteZwKbCategoryByIds(Long[] categoryIds);
    boolean checkCategoryCodeUnique(ZwKbCategory category);
}
