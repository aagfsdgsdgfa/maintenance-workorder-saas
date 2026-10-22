package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwPart;
import java.util.List;

public interface IZwPartService {
    List<ZwPart> selectZwPartList(ZwPart part);
    ZwPart selectZwPartById(Long partId);
    int insertZwPart(ZwPart part);
    int updateZwPart(ZwPart part);
    int deleteZwPartByIds(Long[] partIds);
}
