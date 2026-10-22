package com.ruoyi.zw.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.zw.domain.ZwRepairApply;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwRepairApplyMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwRepairApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class ZwRepairApplyServiceImpl implements IZwRepairApplyService {

    private static final Logger log = LoggerFactory.getLogger(ZwRepairApplyServiceImpl.class);

    @Autowired
    private ZwRepairApplyMapper repairApplyMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Override
    public List<ZwRepairApply> selectZwRepairApplyList(ZwRepairApply repairApply) {
        return repairApplyMapper.selectZwRepairApplyList(repairApply);
    }

    @Override
    public List<ZwRepairApply> selectZwRepairApplyListByCustomerId(Long customerId) {
        return repairApplyMapper.selectZwRepairApplyListByCustomerId(customerId);
    }

    @Override
    public ZwRepairApply selectZwRepairApplyById(Long applyId) {
        return repairApplyMapper.selectZwRepairApplyById(applyId);
    }

    @Override
    public int insertZwRepairApply(ZwRepairApply repairApply) {
        repairApply.setDelFlag("0");
        repairApply.setStatus("0"); // 待受理
        return repairApplyMapper.insertZwRepairApply(repairApply);
    }

    @Override
    public int updateZwRepairApply(ZwRepairApply repairApply) {
        return repairApplyMapper.updateZwRepairApply(repairApply);
    }

    @Override
    public int deleteZwRepairApplyByIds(Long[] applyIds) {
        return repairApplyMapper.deleteZwRepairApplyByIds(applyIds);
    }

    @Override
    @Transactional
    public int acceptRepairApply(Long applyId) {
        ZwRepairApply repairApply = repairApplyMapper.selectZwRepairApplyById(applyId);
        if (repairApply == null) {
            throw new ServiceException("报修申请不存在");
        }
        if (!"0".equals(repairApply.getStatus())) {
            throw new ServiceException("当前状态不允许受理");
        }
        return repairApplyMapper.updateStatusById(applyId, "1"); // 已受理
    }

    @Override
    @Transactional
    public int acceptAndTransfer(Long applyId, Long assignedTo) {
        ZwRepairApply repairApply = repairApplyMapper.selectZwRepairApplyById(applyId);
        if (repairApply == null) {
            throw new ServiceException("报修申请不存在");
        }
        if (!"0".equals(repairApply.getStatus())) {
            throw new ServiceException("当前状态不允许受理");
        }
        // 创建工单
        ZwWorkOrder workOrder = new ZwWorkOrder();
        workOrder.setCustomerId(repairApply.getCustomerId());
        workOrder.setAssetId(repairApply.getAssetId());
        workOrder.setOrderType("2"); // 维修
        workOrder.setTitle(repairApply.getApplyTitle());
        workOrder.setDescription(repairApply.getFaultDesc());
        workOrder.setPriority("2"); // 默认中
        workOrder.setAssignedTo(assignedTo);
        workOrder.setContactName(repairApply.getContactName());
        workOrder.setContactPhone(repairApply.getContactPhone());
        workOrder.setSlaDeadline(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));
        workOrder.setSlaWarningSent("N");
        workOrder.setOrderNo(generateOrderNo());

        int rows = workOrderMapper.insertZwWorkOrder(workOrder);
        if (rows > 0) {
            repairApplyMapper.updateStatusById(applyId, "2");
            ZwRepairApply update = new ZwRepairApply();
            update.setApplyId(applyId);
            update.setWorkOrderId(workOrder.getWorkOrderId());
            repairApplyMapper.updateZwRepairApply(update);
        }
        return rows;
    }

    @Override
    @Transactional
    public int acceptAndTransferBatch(List<Long> applyIds, Long assignedTo) {
        int count = 0;
        for (Long applyId : applyIds) {
            try {
                int rows = acceptAndTransfer(applyId, assignedTo);
                if (rows > 0) {
                    count++;
                }
            } catch (RuntimeException e) {
                // 单条失败不影响其他，继续处理
                log.warn("批量受理派发中某条失败 applyId={}: {}", applyId, e.getMessage());
            }
        }
        return count;
    }

    private String generateOrderNo() {
        String datePrefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxNo = workOrderMapper.selectMaxOrderNo(datePrefix + "%");
        int seq = 1;
        if (maxNo != null && maxNo.length() == datePrefix.length() + 6) {
            try {
                seq = Integer.parseInt(maxNo.substring(datePrefix.length())) + 1;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return datePrefix + String.format("%06d", seq);
    }

    @Override
    @Transactional
    public int transferToWorkOrder(Long applyId, Long assignedTo, String priority) {
        ZwRepairApply repairApply = repairApplyMapper.selectZwRepairApplyById(applyId);
        if (repairApply == null) {
            throw new ServiceException("报修申请不存在");
        }
        if (!"1".equals(repairApply.getStatus())) {
            throw new ServiceException("当前状态不允许转工单，请先受理");
        }

        // 创建工单
        ZwWorkOrder workOrder = new ZwWorkOrder();
        workOrder.setCustomerId(repairApply.getCustomerId());
        workOrder.setAssetId(repairApply.getAssetId());
        workOrder.setOrderType("2"); // 维修
        workOrder.setTitle(repairApply.getApplyTitle());
        workOrder.setDescription(repairApply.getFaultDesc());
        workOrder.setPriority(priority != null ? priority : "2"); // 默认中
        workOrder.setAssignedTo(assignedTo);
        workOrder.setContactName(repairApply.getContactName());
        workOrder.setContactPhone(repairApply.getContactPhone());
        workOrder.setSlaDeadline(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));
        workOrder.setSlaWarningSent("N");
        workOrder.setOrderNo(generateOrderNo());

        // 复用工单Service的逻辑生成编号和默认值
        int rows = workOrderMapper.insertZwWorkOrder(workOrder);
        if (rows > 0) {
            // 更新报修申请状态为处理中，关联工单ID
            repairApplyMapper.updateStatusById(applyId, "2");
            // 更新workOrderId字段
            ZwRepairApply update = new ZwRepairApply();
            update.setApplyId(applyId);
            update.setWorkOrderId(workOrder.getWorkOrderId());
            repairApplyMapper.updateZwRepairApply(update);
        }
        return rows;
    }

    @Override
    public List<ZwRepairApply> selectZwRepairApplyListForExport(ZwRepairApply repairApply) {
        return repairApplyMapper.selectZwRepairApplyListForExport(repairApply);
    }
}
