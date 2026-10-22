package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwInspectionPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ZwInspectionPointMapper {

    List<ZwInspectionPoint> selectZwInspectionPointList(ZwInspectionPoint inspectionPoint);

    ZwInspectionPoint selectZwInspectionPointById(Long pointId);

    List<ZwInspectionPoint> selectZwInspectionPointByAssetId(Long assetId);

    ZwInspectionPoint selectZwInspectionPointByCode(@Param("pointCode") String pointCode);

    int insertZwInspectionPoint(ZwInspectionPoint inspectionPoint);

    int updateZwInspectionPoint(ZwInspectionPoint inspectionPoint);

    int updateStatusById(@Param("pointId") Long pointId, @Param("status") String status);

    int deleteZwInspectionPointByIds(Long[] pointIds);
}
