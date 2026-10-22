package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwFormConfig;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwFormConfigMapper;
import com.ruoyi.zw.service.IZwFormConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwFormConfigServiceImpl implements IZwFormConfigService {

    @Autowired
    private ZwFormConfigMapper formConfigMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Override
    public List<ZwFormConfig> selectZwFormConfigList(ZwFormConfig formConfig) {
        return formConfigMapper.selectZwFormConfigList(formConfig);
    }

    @Override
    public ZwFormConfig selectZwFormConfigById(Long configId) {
        return formConfigMapper.selectZwFormConfigById(configId);
    }

    @Override
    public int insertZwFormConfig(ZwFormConfig formConfig) {
        formConfig.setDelFlag("0");
        return formConfigMapper.insertZwFormConfig(formConfig);
    }

    @Override
    public int updateZwFormConfig(ZwFormConfig formConfig) {
        return formConfigMapper.updateZwFormConfig(formConfig);
    }

    @Override
    public int deleteZwFormConfigByIds(Long[] configIds) {
        return formConfigMapper.deleteZwFormConfigByIds(configIds);
    }

    @Override
    public ZwFormConfig selectByAssetId(Long assetId) {
        ZwAsset asset = assetMapper.selectZwAssetById(assetId);
        if (asset == null || asset.getAssetType() == null) {
            return null;
        }
        return formConfigMapper.selectByAssetTypeAndBrand(asset.getAssetType(), asset.getBrand());
    }
}
