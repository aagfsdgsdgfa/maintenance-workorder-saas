package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwInspectionPoint;
import java.util.List;

public interface IZwInspectionPointService {

    List<ZwInspectionPoint> selectZwInspectionPointList(ZwInspectionPoint inspectionPoint);

    ZwInspectionPoint selectZwInspectionPointById(Long pointId);

    List<ZwInspectionPoint> selectZwInspectionPointByAssetId(Long assetId);

    ZwInspectionPoint selectZwInspectionPointByCode(String pointCode);

    int insertZwInspectionPoint(ZwInspectionPoint inspectionPoint);

    int updateZwInspectionPoint(ZwInspectionPoint inspectionPoint);

    int deleteZwInspectionPointByIds(Long[] pointIds);

    /**
     * 启用/停用巡检点
     */
    int toggleStatus(Long pointId);

    /**
     * 巡检点扫码验证
     * @param pointId 巡检点ID
     * @param scanCode 扫码结果（QR码内容）
     * @return 验证结果
     */
    boolean verifyPoint(Long pointId, String scanCode);
}
