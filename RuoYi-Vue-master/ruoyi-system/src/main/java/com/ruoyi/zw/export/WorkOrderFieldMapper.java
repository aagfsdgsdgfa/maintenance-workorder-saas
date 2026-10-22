package com.ruoyi.zw.export;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.enums.OrderTypeEnum;
import com.ruoyi.zw.enums.PriorityEnum;
import com.ruoyi.zw.enums.WorkOrderStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 工单字段映射器：{@link ZwWorkOrder} → Word模板占位符Map。
 *
 * <p>所有格式化逻辑（日期/金额/枚举标签/复选框）在此聚合，便于Word模板字段集中维护。
 */
public class WorkOrderFieldMapper {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderFieldMapper.class);

    private static final SimpleDateFormat DATE_FORMAT_CN = new SimpleDateFormat("yyyy年MM月dd日");
    private static final SimpleDateFormat DATETIME_FORMAT_CN = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private static final String YUAN_SYMBOL = "¥";

    private final ISysUserService sysUserService;

    public WorkOrderFieldMapper(ISysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    public Map<String, String> build(ZwWorkOrder wo) {
        Map<String, String> map = new HashMap<>();

        map.put("orderNo", nullToStr(wo.getOrderNo()));
        map.put("createTime", formatDate(wo.getCreateTime()));
        map.put("customerName", nullToStr(wo.getCustomerName(), "客户"));
        map.put("contactName", nullToStr(wo.getContactName(), "-"));
        map.put("address", nullToStr(wo.getContactAddress(), "-"));
        map.put("contactPhone", nullToStr(wo.getContactPhone(), "-"));
        map.put("completeTime", formatDate(wo.getCompleteTime()));
        map.put("title", nullToStr(wo.getTitle()));
        map.put("description", nullToStr(wo.getDescription()));
        map.put("assetName", nullToStr(wo.getAssetName(), "-"));
        map.put("serialNumber", nullToStr(wo.getSerialNumber(), "-"));
        map.put("orderType", formatOrderType(wo.getOrderType()));
        map.put("priority", formatPriority(wo.getPriority()));
        map.put("status", formatStatus(wo.getStatus()));
        map.put("engineerName", lookupEngineerName(wo.getAssignedTo()));
        map.put("slaDeadline", formatDate(wo.getSlaDeadline()));
        map.put("partsList", "（见系统记录）");

        map.put("acceptTime", formatDateTime(wo.getAcceptTime()));
        map.put("serviceTime", formatDateTime(wo.getServiceTime()));
        map.put("workOrderType", nullToStr(wo.getWorkOrderType()));
        map.put("faultDescription", nullToStr(wo.getFaultDescription()));
        map.put("faultCause", nullToStr(wo.getFaultCause()));

        map.put("visitFee", formatMoney(wo.getVisitFee()));
        map.put("serviceFee", formatMoney(wo.getServiceFee()));
        map.put("totalAmount", formatMoney(wo.getTotalAmount()));
        map.put("chargeStatus", formatCheckbox(wo.getChargeStatus()));
        map.put("testStatus", nullToStr(wo.getTestStatus()));

        // 签字图片URL占位符（图片实际由WorkOrderPhotoCollector插入）
        map.put("customerSignUrl", "");
        map.put("engineerSignUrl", "");

        map.put("remark", nullToStr(wo.getRemark()));
        map.put("contractStartDate", formatDate(wo.getContractStartDate()));
        map.put("contractEndDate", formatDate(wo.getContractEndDate()));
        map.put("purchaseDate", formatDate(wo.getPurchaseDate()));
        map.put("brand", nullToStr(wo.getBrand()));
        map.put("model", nullToStr(wo.getModel()));

        return map;
    }

    String formatDate(Date date) {
        return date == null ? "-" : DATE_FORMAT_CN.format(date);
    }

    String formatDateTime(Date date) {
        return date == null ? "-" : DATETIME_FORMAT_CN.format(date);
    }

    String formatMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return String.format("%s%.2f", YUAN_SYMBOL, amount);
    }

    String formatCheckbox(String status) {
        return "是".equals(status) || "Y".equalsIgnoreCase(status) ? "☑" : "☐";
    }

    String formatOrderType(String orderType) {
        if (orderType == null) return "-";
        OrderTypeEnum e = OrderTypeEnum.of(orderType);
        return e != null ? e.getLabel() : orderType;
    }

    String formatPriority(String priority) {
        if (priority == null) return "-";
        PriorityEnum e = PriorityEnum.of(priority);
        return e != null ? e.getLabel() : priority;
    }

    String formatStatus(String status) {
        if (status == null) return "-";
        WorkOrderStatusEnum e = WorkOrderStatusEnum.of(status);
        return e != null ? e.getLabel() : status;
    }

    String lookupEngineerName(Long assignedTo) {
        if (assignedTo == null) return "-";
        try {
            SysUser user = sysUserService.selectUserById(assignedTo);
            return user != null ? nullToStr(user.getNickName(), user.getUserName()) : "-";
        } catch (Exception e) {
            log.warn("查询工程师姓名失败: assignedTo={}", assignedTo, e);
            return "-";
        }
    }

    static String nullToStr(String s) {
        return (s != null && !s.isBlank()) ? s : "";
    }

    static String nullToStr(String s, String defaultVal) {
        return (s != null && !s.isBlank()) ? s : defaultVal;
    }
}