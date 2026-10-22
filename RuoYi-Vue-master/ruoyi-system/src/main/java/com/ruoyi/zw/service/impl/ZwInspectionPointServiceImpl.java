package com.ruoyi.zw.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.zw.domain.ZwInspectionPoint;
import com.ruoyi.zw.mapper.ZwInspectionPointMapper;
import com.ruoyi.zw.service.IZwInspectionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ZwInspectionPointServiceImpl implements IZwInspectionPointService {

    @Autowired
    private ZwInspectionPointMapper inspectionPointMapper;

    @Override
    public List<ZwInspectionPoint> selectZwInspectionPointList(ZwInspectionPoint inspectionPoint) {
        return inspectionPointMapper.selectZwInspectionPointList(inspectionPoint);
    }

    @Override
    public ZwInspectionPoint selectZwInspectionPointById(Long pointId) {
        return inspectionPointMapper.selectZwInspectionPointById(pointId);
    }

    @Override
    public List<ZwInspectionPoint> selectZwInspectionPointByAssetId(Long assetId) {
        return inspectionPointMapper.selectZwInspectionPointByAssetId(assetId);
    }

    @Override
    public ZwInspectionPoint selectZwInspectionPointByCode(String pointCode) {
        return inspectionPointMapper.selectZwInspectionPointByCode(pointCode);
    }

    @Override
    public int insertZwInspectionPoint(ZwInspectionPoint inspectionPoint) {
        inspectionPoint.setDelFlag("0");
        inspectionPoint.setStatus("0"); // 正常
        if (inspectionPoint.getScanRadius() == null) {
            inspectionPoint.setScanRadius(50); // 默认50米
        }
        return inspectionPointMapper.insertZwInspectionPoint(inspectionPoint);
    }

    @Override
    public int updateZwInspectionPoint(ZwInspectionPoint inspectionPoint) {
        return inspectionPointMapper.updateZwInspectionPoint(inspectionPoint);
    }

    @Override
    public int deleteZwInspectionPointByIds(Long[] pointIds) {
        return inspectionPointMapper.deleteZwInspectionPointByIds(pointIds);
    }

    @Override
    @Transactional
    public int toggleStatus(Long pointId) {
        ZwInspectionPoint point = inspectionPointMapper.selectZwInspectionPointById(pointId);
        if (point == null) {
            throw new ServiceException("巡检点不存在");
        }
        String newStatus = "0".equals(point.getStatus()) ? "1" : "0";
        return inspectionPointMapper.updateStatusById(pointId, newStatus);
    }

    @Override
    public boolean verifyPoint(Long pointId, String scanCode) {
        if (scanCode == null || scanCode.isEmpty()) {
            return false;
        }
        ZwInspectionPoint point = inspectionPointMapper.selectZwInspectionPointById(pointId);
        if (point == null) {
            return false;
        }
        return scanCode.equals(point.getPointCode());
    }
}
