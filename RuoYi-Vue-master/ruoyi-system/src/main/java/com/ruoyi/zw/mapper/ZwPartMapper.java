package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwPart;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ZwPartMapper {
    List<ZwPart> selectZwPartList(ZwPart part);
    ZwPart selectZwPartById(Long partId);
    int insertZwPart(ZwPart part);
    int updateZwPart(ZwPart part);
    int deleteZwPartByIds(Long[] partIds);
}
