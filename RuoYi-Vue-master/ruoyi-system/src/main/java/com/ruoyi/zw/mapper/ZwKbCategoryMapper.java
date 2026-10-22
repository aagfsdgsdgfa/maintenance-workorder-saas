package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwKbCategory;
import java.util.List;

public interface ZwKbCategoryMapper {
    List<ZwKbCategory> selectZwKbCategoryList(ZwKbCategory category);
    ZwKbCategory selectZwKbCategoryById(Long categoryId);
    List<ZwKbCategory> selectAllKbCategory();
    int insertZwKbCategory(ZwKbCategory category);
    int updateZwKbCategory(ZwKbCategory category);
    int deleteZwKbCategoryByIds(Long[] categoryIds);
    int checkCategoryCodeUnique(ZwKbCategory category);
}
