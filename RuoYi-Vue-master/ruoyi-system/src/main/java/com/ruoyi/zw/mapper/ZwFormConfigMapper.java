package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwFormConfig;
import java.util.List;

public interface ZwFormConfigMapper {
    List<ZwFormConfig> selectZwFormConfigList(ZwFormConfig formConfig);
    ZwFormConfig selectZwFormConfigById(Long configId);
    int insertZwFormConfig(ZwFormConfig formConfig);
    int updateZwFormConfig(ZwFormConfig formConfig);
    int deleteZwFormConfigByIds(Long[] configIds);

    /**
     * 根据资产类型和品牌查找表单配置（优先精确匹配，其次类型匹配）
     */
    ZwFormConfig selectByAssetTypeAndBrand(String assetType, String brand);
}
