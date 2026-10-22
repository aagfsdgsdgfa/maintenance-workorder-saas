package com.ruoyi.zw.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwPart;
import com.ruoyi.zw.mapper.ZwPartMapper;
import com.ruoyi.zw.service.IZwPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwPartServiceImpl implements IZwPartService {

    @Autowired
    private ZwPartMapper partMapper;

    @Override
    public List<ZwPart> selectZwPartList(ZwPart part) {
        return partMapper.selectZwPartList(part);
    }

    @Override
    public ZwPart selectZwPartById(Long partId) {
        return partMapper.selectZwPartById(partId);
    }

    @Override
    public int insertZwPart(ZwPart part) {
        part.setCreateBy(SecurityUtils.getUsername());
        return partMapper.insertZwPart(part);
    }

    @Override
    public int updateZwPart(ZwPart part) {
        part.setUpdateBy(SecurityUtils.getUsername());
        return partMapper.updateZwPart(part);
    }

    @Override
    public int deleteZwPartByIds(Long[] partIds) {
        return partMapper.deleteZwPartByIds(partIds);
    }
}
