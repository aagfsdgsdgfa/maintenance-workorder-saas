package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwFormConfig;
import java.util.List;

public interface IZwFormConfigService {
    List<ZwFormConfig> selectZwFormConfigList(ZwFormConfig formConfig);
    ZwFormConfig selectZwFormConfigById(Long configId);
    int insertZwFormConfig(ZwFormConfig formConfig);
    int updateZwFormConfig(ZwFormConfig formConfig);
    int deleteZwFormConfigByIds(Long[] configIds);

    /**
     * 根据资产ID匹配表单配置
     */
    ZwFormConfig selectByAssetId(Long assetId);
}
