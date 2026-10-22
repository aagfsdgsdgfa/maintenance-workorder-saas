package com.ruoyi.zw.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwFaultRecord;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwFaultRecordMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwFaultRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class ZwFaultRecordServiceImpl implements IZwFaultRecordService {

    @Autowired
    private ZwFaultRecordMapper faultRecordMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Override
    public List<ZwFaultRecord> selectZwFaultRecordList(ZwFaultRecord faultRecord) {
        return faultRecordMapper.selectZwFaultRecordList(faultRecord);
    }

    @Override
    public ZwFaultRecord selectZwFaultRecordById(Long faultId) {
        return faultRecordMapper.selectZwFaultRecordById(faultId);
    }

    @Override
    public int insertZwFaultRecord(ZwFaultRecord faultRecord) {
        faultRecord.setDelFlag("0");
        if (faultRecord.getFaultTime() == null) {
            faultRecord.setFaultTime(new Date());
        }
        return faultRecordMapper.insertZwFaultRecord(faultRecord);
    }

    @Override
    public int updateZwFaultRecord(ZwFaultRecord faultRecord) {
        return faultRecordMapper.updateZwFaultRecord(faultRecord);
    }

    @Override
    public int deleteZwFaultRecordByIds(Long[] faultIds) {
        return faultRecordMapper.deleteZwFaultRecordByIds(faultIds);
    }

    @Override
    @Transactional
    public Long faultToWorkOrder(Long faultId) {
        // 查询故障信息
        ZwFaultRecord fault = faultRecordMapper.selectZwFaultRecordById(faultId);
        if (fault == null) {
            throw new ServiceException("故障记录不存在");
        }
        // 获取客户ID（如果故障没有，从资产获取）
        Long customerId = fault.getCustomerId();
        if (customerId == null && fault.getAssetId() != null) {
            ZwAsset asset = assetMapper.selectZwAssetById(fault.getAssetId());
            if (asset != null) {
                customerId = asset.getCustomerId();
            }
        }
        // 创建工单，复制故障信息
        ZwWorkOrder workOrder = new ZwWorkOrder();
        workOrder.setCustomerId(customerId);
        workOrder.setAssetId(fault.getAssetId());
        workOrder.setOrderType("2"); // 维修类型
        String title = fault.getFaultDesc() != null && fault.getFaultDesc().length() > 0
            ? fault.getFaultDesc().substring(0, Math.min(50, fault.getFaultDesc().length()))
            : "故障维修";
        workOrder.setTitle("【故障转工单】" + title);
        workOrder.setDescription(fault.getFaultDesc());
        workOrder.setPriority(fault.getFaultLevel() != null ? fault.getFaultLevel() : "2");
        workOrder.setCreateBy(SecurityUtils.getUsername());
        // 插入工单
        workOrderMapper.insertZwWorkOrder(workOrder);
        // 更新故障记录关联工单ID
        fault.setWorkOrderId(workOrder.getWorkOrderId());
        faultRecordMapper.updateZwFaultRecord(fault);
        return workOrder.getWorkOrderId();
    }

    @Override
    public List<ZwFaultRecord> selectFaultRecordsByWorkOrderId(Long workOrderId) {
        return faultRecordMapper.selectFaultRecordsByWorkOrderId(workOrderId);
    }

    @Override
    public int resolveFault(Long faultId, String resolution) {
        ZwFaultRecord fault = new ZwFaultRecord();
        fault.setFaultId(faultId);
        fault.setFaultStatus("1");
        fault.setResolveTime(new Date());
        if (resolution != null && !resolution.isEmpty()) {
            fault.setResolution(resolution);
        }
        return faultRecordMapper.updateZwFaultRecord(fault);
    }

    @Override
    @Transactional
    public Long createFaultWithWorkOrder(ZwFaultRecord faultRecord, Long assignedTo) {
        // 设置默认值
        faultRecord.setDelFlag("0");
        faultRecord.setFaultStatus("0"); // 已上报
        if (faultRecord.getFaultTime() == null) {
            faultRecord.setFaultTime(new Date());
        }
        // 插入故障记录
        faultRecordMapper.insertZwFaultRecord(faultRecord);

        if (assignedTo != null) {
            // 获取客户ID（如果故障没有，从资产获取）
            Long customerId = faultRecord.getCustomerId();
            if (customerId == null && faultRecord.getAssetId() != null) {
                ZwAsset asset = assetMapper.selectZwAssetById(faultRecord.getAssetId());
                if (asset != null) {
                    customerId = asset.getCustomerId();
                }
            }
            // 创建工单并派发
            ZwWorkOrder workOrder = new ZwWorkOrder();
            workOrder.setCustomerId(customerId);
            workOrder.setAssetId(faultRecord.getAssetId());
            workOrder.setOrderType("2"); // 维修类型
            String title = faultRecord.getFaultDesc() != null && !faultRecord.getFaultDesc().isEmpty()
                ? faultRecord.getFaultDesc().substring(0, Math.min(50, faultRecord.getFaultDesc().length()))
                : "故障维修";
            workOrder.setTitle("【故障】" + title);
            workOrder.setDescription(faultRecord.getFaultDesc());
            workOrder.setPriority(faultRecord.getFaultLevel() != null ? faultRecord.getFaultLevel() : "2");
            workOrder.setAssignedTo(assignedTo);
            workOrder.setStatus("1"); // 处理中（已派发）
            workOrder.setCreateBy(SecurityUtils.getUsername());
            workOrderMapper.insertZwWorkOrder(workOrder);

            // 关联工单ID到故障记录
            ZwFaultRecord update = new ZwFaultRecord();
            update.setFaultId(faultRecord.getFaultId());
            update.setWorkOrderId(workOrder.getWorkOrderId());
            update.setFaultStatus("1"); // 处理中
            faultRecordMapper.updateZwFaultRecord(update);

            return workOrder.getWorkOrderId();
        }
        return null;
    }
}
