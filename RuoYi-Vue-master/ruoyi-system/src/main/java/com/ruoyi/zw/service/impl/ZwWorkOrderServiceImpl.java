package com.ruoyi.zw.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.domain.ZwFaultRecord;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.domain.ZwWorkOrderDetail;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwWorkOrderService;
import com.ruoyi.zw.service.IZwContractService;
import com.ruoyi.zw.service.IZwAssetService;
import com.ruoyi.zw.service.IZwAssetLogsService;
import com.ruoyi.zw.service.IZwCheckInService;
import com.ruoyi.zw.service.IZwFaultRecordService;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import com.ruoyi.zw.service.IZwNotificationService;
import com.ruoyi.zw.service.IZwSatisfactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZwWorkOrderServiceImpl implements IZwWorkOrderService {

    private static final Logger log = LoggerFactory.getLogger(ZwWorkOrderServiceImpl.class);

    private static final String STATUS_PENDING = "0";
    private static final String STATUS_PROCESSING = "1";
    private static final String STATUS_COMPLETE = "3";
    private static final String STATUS_CLOSED = "4";
    private static final String ORDER_TYPE_INSPECTION = "1";
    private static final String ORDER_TYPE_REPAIR = "2";
    private static final String NOTIF_TYPE_WORKORDER = "1";

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private IZwInspectionTaskService inspectionTaskService;

    @Autowired
    private IZwCheckInService checkInService;

    @Autowired
    private IZwFaultRecordService faultRecordService;

    @Autowired
    private IZwSatisfactionService satisfactionService;

    @Autowired
    private IZwAssetLogsService assetLogsService;

    @Autowired
    private IZwNotificationService notificationService;

    @Autowired
    private IZwContractService contractService;

    @Autowired
    private IZwAssetService assetService;

    @Override
    public List<ZwWorkOrder> selectZwWorkOrderList(ZwWorkOrder workOrder) {
        return workOrderMapper.selectZwWorkOrderList(workOrder);
    }

    @Override
    public ZwWorkOrder selectZwWorkOrderById(Long workOrderId) {
        return workOrderMapper.selectZwWorkOrderById(workOrderId);
    }

    @Override
    public ZwWorkOrderDetail selectZwWorkOrderDetailById(Long workOrderId) {
        ZwWorkOrder workOrder = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (workOrder == null) {
            return null;
        }
        ZwWorkOrderDetail detail = new ZwWorkOrderDetail();
        detail.setWorkOrderId(workOrder.getWorkOrderId());
        detail.setOrderNo(workOrder.getOrderNo());
        detail.setCustomerId(workOrder.getCustomerId());
        detail.setAssetId(workOrder.getAssetId());
        detail.setOrderType(workOrder.getOrderType());
        detail.setTitle(workOrder.getTitle());
        detail.setDescription(workOrder.getDescription());
        detail.setPriority(workOrder.getPriority());
        detail.setAssignedTo(workOrder.getAssignedTo());
        detail.setAssignedBy(workOrder.getAssignedBy());
        detail.setStatus(workOrder.getStatus());
        detail.setSlaDeadline(workOrder.getSlaDeadline());
        detail.setSlaWarningSent(workOrder.getSlaWarningSent());
        detail.setContactName(workOrder.getContactName());
        detail.setContactPhone(workOrder.getContactPhone());
        detail.setContactAddress(workOrder.getContactAddress());
        detail.setCompleteTime(workOrder.getCompleteTime());
        detail.setSignImage(workOrder.getSignImage());
        detail.setDelFlag(workOrder.getDelFlag());
        detail.setCreateBy(workOrder.getCreateBy());
        detail.setCreateTime(workOrder.getCreateTime());
        detail.setUpdateBy(workOrder.getUpdateBy());
        detail.setUpdateTime(workOrder.getUpdateTime());
        detail.setIsOverdue(computeIsOverdue(workOrder));
        detail.setInspectionTasks(inspectionTaskService.selectTasksByWorkOrderId(workOrderId));
        detail.setCheckInRecords(checkInService.selectCheckInByWorkOrderId(workOrderId));
        detail.setFaultRecords(faultRecordService.selectFaultRecordsByWorkOrderId(workOrderId));
        detail.setSatisfaction(satisfactionService.selectSatisfactionByWorkOrderId(workOrderId));
        return detail;
    }

    private boolean computeIsOverdue(ZwWorkOrder workOrder) {
        if (workOrder.getSlaDeadline() == null) {
            return false;
        }
        String status = workOrder.getStatus();
        if (STATUS_COMPLETE.equals(status) || STATUS_CLOSED.equals(status)) {
            return false;
        }
        return new Date().after(workOrder.getSlaDeadline());
    }

    @Override
    @Transactional
    public int insertZwWorkOrder(ZwWorkOrder workOrder) {
        workOrder.setDelFlag("0");
        workOrder.setStatus(STATUS_PENDING);
        workOrder.setSlaWarningSent("N");
        workOrder.setOrderNo(generateOrderNo());
        if (workOrder.getAssignedBy() == null) {
            workOrder.setAssignedBy(SecurityUtils.getUserId());
        }
        if (workOrder.getSlaDeadline() == null) {
            workOrder.setSlaDeadline(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L));
        }
        return workOrderMapper.insertZwWorkOrder(workOrder);
    }

    @Override
    public int updateZwWorkOrder(ZwWorkOrder workOrder) {
        return workOrderMapper.updateZwWorkOrder(workOrder);
    }

    @Override
    public int deleteZwWorkOrderByIds(Long[] workOrderIds) {
        return workOrderMapper.deleteZwWorkOrderByIds(workOrderIds);
    }

    @Override
    @Transactional
    public int changeStatus(Long workOrderId, String status) {
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            throw new ServiceException("工单不存在");
        }
        String currentStatus = wo.getStatus();

        // 状态机校验：限制合法的状态跳转
        boolean valid = switch (status) {
            case "0" -> false; // 不允许手动改回待接单
            case "1" -> STATUS_PENDING.equals(currentStatus); // 只有待接单才能变为处理中
            case "2" -> STATUS_PROCESSING.equals(currentStatus); // 只有处理中才能变为待确认
            case "3" -> "2".equals(currentStatus); // 只有待确认才能完工
            case "4" -> STATUS_PENDING.equals(currentStatus) || STATUS_PROCESSING.equals(currentStatus); // 待接单或处理中可取消
            default -> false;
        };
        if (!valid) {
            throw new ServiceException("不允许从状态 [" + currentStatus + "] 直接跳转到 [" + status + "]");
        }

        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setStatus(status);
        if (STATUS_COMPLETE.equals(status)) {
            update.setCompleteTime(new Date());
        }
        int result = workOrderMapper.updateZwWorkOrder(update);

        if (result > 0 && STATUS_COMPLETE.equals(status)) {
            contractService.deductContractUsage(workOrderId, wo.getOrderType(), wo.getAssetId());
            if (ORDER_TYPE_REPAIR.equals(wo.getOrderType())) {
                assetService.updateAssetStatus(wo.getAssetId(), "0");
            }
            createSatisfactionIfNotExists(workOrderId, wo.getCustomerId());
            if (wo.getCustomerId() != null) {
                notificationService.sendNotification(
                        wo.getCustomerId(), wo.getCustomerId(),
                        "工单已完成",
                        "工单号 " + wo.getOrderNo() + " 已完成，请验收评价",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
            if (wo.getAssignedTo() != null) {
                notificationService.sendNotification(
                        wo.getAssignedTo(), wo.getCustomerId(),
                        "工单已完成",
                        "工单号 " + wo.getOrderNo() + " 已完工，等待客户验收",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
        }
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSatisfactionIfNotExists(Long workOrderId, Long customerId) {
        if (customerId == null) return;
        ZwSatisfaction existing = satisfactionService.selectSatisfactionByWorkOrderId(workOrderId);
        if (existing != null) return;
        ZwSatisfaction sat = new ZwSatisfaction();
        sat.setWorkOrderId(workOrderId);
        sat.setCustomerId(customerId);
        sat.setResponseScore(0);
        sat.setTechScore(0);
        sat.setOverallScore(0);
        sat.setComment("");
        satisfactionService.insertZwSatisfaction(sat);
    }

    @Override
    @Transactional
    public int assignEngineer(Long workOrderId, Long engineerId, Long assignedBy) {
        ZwWorkOrder workOrder = new ZwWorkOrder();
        workOrder.setWorkOrderId(workOrderId);
        workOrder.setAssignedTo(engineerId);
        workOrder.setAssignedBy(assignedBy);
        int result = workOrderMapper.updateZwWorkOrder(workOrder);
        if (result > 0) {
            ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
            if (wo != null) {
                if (wo.getCustomerId() != null) {
                    notificationService.sendNotification(
                            wo.getCustomerId(), wo.getCustomerId(),
                            "您有新工单",
                            "工单号 " + wo.getOrderNo() + " 已派发，请及时处理",
                            NOTIF_TYPE_WORKORDER,
                            workOrderId
                    );
                }
                notificationService.sendNotification(
                        engineerId, wo.getCustomerId(),
                        "您有新工单",
                        "工单号 " + wo.getOrderNo() + " 已派发给您，请及时接单处理",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
        }
        return result;
    }

    @Override
    public List<ZwWorkOrder> selectMyTasks(Long engineerId, String status) {
        return workOrderMapper.selectMyTasks(engineerId, status);
    }

    @Override
    public Map<String, Long> selectMyTaskStats(Long engineerId) {
        List<ZwWorkOrder> allTasks = workOrderMapper.selectMyTasks(engineerId, null);
        Map<String, Long> stats = new HashMap<>();
        stats.put("waiting", 0L);
        stats.put("processing", 0L);
        stats.put("completed", 0L);
        stats.put("inspection", 0L);
        for (ZwWorkOrder wo : allTasks) {
            String s = wo.getStatus();
            if ("0".equals(s)) {
                stats.put("waiting", stats.get("waiting") + 1);
            } else if ("1".equals(s) || "2".equals(s)) {
                stats.put("processing", stats.get("processing") + 1);
            } else if ("3".equals(s)) {
                stats.put("completed", stats.get("completed") + 1);
            }
        }
        return stats;
    }

    @Override
    @Transactional
    public int accept(Long workOrderId, Long engineerId) {
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            throw new ServiceException("工单不存在");
        }
        if (!STATUS_PENDING.equals(wo.getStatus())) {
            throw new ServiceException("当前状态不允许接单");
        }
        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setAssignedTo(engineerId);
        update.setStatus(STATUS_PROCESSING);
        int result = workOrderMapper.updateZwWorkOrder(update);
        if (result > 0) {
            // 通知客户
            if (wo.getCustomerId() != null) {
                notificationService.sendNotification(
                        wo.getCustomerId(), wo.getCustomerId(),
                        "工单已接单",
                        "工单号 " + wo.getOrderNo() + " 已由工程师接单，请知悉",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
            // 通知工程师
            notificationService.sendNotification(
                    engineerId, wo.getCustomerId(),
                    "您已接单",
                    "工单号 " + wo.getOrderNo() + " 已由您接单，请前往处理",
                    NOTIF_TYPE_WORKORDER,
                    workOrderId
            );
        }
        return result;
    }

    @Override
    @Transactional
    public int reject(Long workOrderId, Long engineerId, String reason) {
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            throw new ServiceException("工单不存在");
        }
        if (!STATUS_PENDING.equals(wo.getStatus())) {
            throw new ServiceException("当前状态不允许拒单");
        }
        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setStatus(STATUS_CLOSED);
        update.setDescription(wo.getDescription() + "\n【拒单原因】" + reason);
        return workOrderMapper.updateZwWorkOrder(update);
    }

    @Override
    @Transactional
    public int start(Long workOrderId) {
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            throw new ServiceException("工单不存在");
        }
        if (!STATUS_PROCESSING.equals(wo.getStatus())) {
            throw new ServiceException("当前状态不允许开始处理");
        }
        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setStatus("2");
        return workOrderMapper.updateZwWorkOrder(update);
    }

    @Override
    @Transactional
    public int complete(Long workOrderId, String resultDesc, String signImage, String photos, String parts, String nextVisitTime) {
        log.info("完工确认开始 workOrderId={}, resultDesc={}, signImage={}", workOrderId, resultDesc, signImage);
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            throw new ServiceException("工单不存在");
        }
        log.info("工单当前状态 status={}", wo.getStatus());
        if (!"2".equals(wo.getStatus())) {
            throw new ServiceException("当前状态不允许完工确认，当前状态=" + wo.getStatus());
        }
        // 巡检类工单完工前检查关联的巡检任务是否全部完成
        if (ORDER_TYPE_INSPECTION.equals(wo.getOrderType())) {
            List<ZwInspectionTask> tasks = inspectionTaskService.selectTasksByWorkOrderId(workOrderId);
            for (ZwInspectionTask task : tasks) {
                String ts = task.getStatus() != null ? task.getStatus() : "";
                if (!"3".equals(ts) && !"4".equals(ts)) {
                    throw new ServiceException("存在未完成的巡检任务，请先完成所有巡检后再完工");
                }
            }
        }
        ZwWorkOrder update = new ZwWorkOrder();
        update.setWorkOrderId(workOrderId);
        update.setStatus(STATUS_COMPLETE);
        update.setCompleteTime(new Date());
        update.setSignImage(signImage);
        if (resultDesc != null && !resultDesc.isEmpty()) {
            update.setDescription(wo.getDescription() + "\n【完工描述】" + resultDesc);
        }
        int result = workOrderMapper.updateZwWorkOrder(update);
        if (result > 0) {
            if (ORDER_TYPE_REPAIR.equals(wo.getOrderType()) && wo.getAssetId() != null) {
                assetService.updateAssetStatus(wo.getAssetId(), "0");
            }
            createSatisfactionIfNotExists(workOrderId, wo.getCustomerId());
            if (wo.getCustomerId() != null) {
                notificationService.sendNotification(
                        wo.getCustomerId(), wo.getCustomerId(),
                        "工单已完成",
                        "工单号 " + wo.getOrderNo() + " 已完工，请验收确认",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
            if (wo.getAssignedTo() != null) {
                notificationService.sendNotification(
                        wo.getAssignedTo(), wo.getCustomerId(),
                        "工单已完成",
                        "工单号 " + wo.getOrderNo() + " 已完工，等待客户验收",
                        NOTIF_TYPE_WORKORDER,
                        workOrderId
                );
            }
        }
        return result;
    }

    private String generateOrderNo() {
        String datePrefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxNo = workOrderMapper.selectMaxOrderNo(datePrefix + "%");
        int seq = 1;
        if (maxNo != null && maxNo.length() == datePrefix.length() + 6) {
            try {
                seq = Integer.parseInt(maxNo.substring(datePrefix.length())) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        if (seq > 999999) {
            throw new IllegalStateException("当日工单序号已达上限999999，请联系管理员");
        }
        return datePrefix + String.format("%06d", seq);
    }
}
