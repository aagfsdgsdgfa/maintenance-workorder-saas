package com.ruoyi.zw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.enums.OrderTypeEnum;
import com.ruoyi.zw.enums.PriorityEnum;
import com.ruoyi.zw.enums.WorkOrderStatusEnum;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwCheckInMapper;
import com.ruoyi.zw.mapper.ZwSatisfactionMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwCustomerWorkOrderService;
import com.ruoyi.zw.service.IZwSatisfactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户工单Service实现
 *
 * @author ruoyi
 */
@Service
public class ZwCustomerWorkOrderServiceImpl implements IZwCustomerWorkOrderService {

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Autowired
    private ZwCheckInMapper checkInMapper;

    @Autowired
    private ZwSatisfactionMapper satisfactionMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> selectWorkOrderList(Long customerId, String status, String orderType,
                                                    String startDate, String endDate,
                                                    int pageNum, int pageSize) {
        ZwWorkOrder query = new ZwWorkOrder();
        query.setCustomerId(customerId);
        query.setDelFlag("0");
        if (status != null && !status.isBlank()) {
            query.setStatus(status);
        }
        if (orderType != null && !orderType.isBlank()) {
            query.setOrderType(orderType);
        }
        if (startDate != null && !startDate.isBlank()) {
            query.setCreateTimeStart(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            query.setCreateTimeEnd(endDate);
        }

        // 分页下推到 SQL 端，SQL 同时按日期过滤；selectZwWorkOrderVo 已 LEFT JOIN zw_asset，assetName 直接读
        PageHelper.startPage(pageNum, pageSize);
        List<ZwWorkOrder> pageList = workOrderMapper.selectZwWorkOrderList(query);
        PageInfo<ZwWorkOrder> pageInfo = new PageInfo<>(pageList);

        Map<Long, SysUser> userCache = new HashMap<>();
        List<Map<String, Object>> rows = pageList.stream().map(wo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("workOrderId", wo.getWorkOrderId());
            map.put("orderNo", wo.getOrderNo());
            map.put("orderType", wo.getOrderType());
            map.put("orderTypeName", convertOrderTypeName(wo.getOrderType()));
            map.put("title", wo.getTitle());
            map.put("faultDesc", wo.getDescription());
            map.put("priority", wo.getPriority());
            map.put("priorityName", convertPriorityName(wo.getPriority()));
            map.put("status", wo.getStatus());
            map.put("statusName", convertStatusName(wo.getStatus()));
            map.put("assetId", wo.getAssetId());
            // assetName 由 SQL 端 LEFT JOIN zw_asset 直接返回，避免 N+1
            map.put("assetName", wo.getAssetName());
            if (wo.getAssignedTo() != null) {
                SysUser engineer = userCache.computeIfAbsent(wo.getAssignedTo(), sysUserMapper::selectUserById);
                if (engineer != null) {
                    map.put("engineerName", engineer.getNickName());
                    map.put("engineerPhone", engineer.getPhonenumber());
                } else {
                    map.put("engineerName", null);
                    map.put("engineerPhone", null);
                }
            } else {
                map.put("engineerName", null);
                map.put("engineerPhone", null);
            }
            map.put("createTime", wo.getCreateTime());
            map.put("completeTime", wo.getCompleteTime());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("rows", rows);
        return result;
    }

    @Override
    public Map<String, Object> selectWorkOrderDetail(Long workOrderId) {
        ZwWorkOrder wo = workOrderMapper.selectZwWorkOrderById(workOrderId);
        if (wo == null) {
            return null;
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("workOrderId", wo.getWorkOrderId());
        detail.put("orderNo", wo.getOrderNo());
        detail.put("orderType", wo.getOrderType());
        detail.put("orderTypeName", convertOrderTypeName(wo.getOrderType()));
        detail.put("title", wo.getTitle());
        detail.put("faultDesc", wo.getDescription());
        detail.put("priority", wo.getPriority());
        detail.put("priorityName", convertPriorityName(wo.getPriority()));
        detail.put("status", wo.getStatus());
        detail.put("statusName", convertStatusName(wo.getStatus()));
        detail.put("contactName", wo.getContactName());
        detail.put("contactPhone", wo.getContactPhone());
        detail.put("assetId", wo.getAssetId());
        if (wo.getAssetId() != null) {
            ZwAsset asset = assetMapper.selectZwAssetById(wo.getAssetId());
            if (asset != null) {
                Map<String, Object> assetMap = new HashMap<>();
                assetMap.put("assetId", asset.getAssetId());
                assetMap.put("assetName", asset.getAssetName());
                assetMap.put("assetCode", asset.getAssetCode());
                detail.put("assetName", asset.getAssetName());
                detail.put("assetCode", asset.getAssetCode());
            }
        }
        SysUser engUser = wo.getAssignedTo() != null ? sysUserMapper.selectUserById(wo.getAssignedTo()) : null;
        detail.put("engineerName", engUser != null ? engUser.getNickName() : null);
        detail.put("engineerPhone", engUser != null ? engUser.getPhonenumber() : null);
        detail.put("engineerLatitude", null);
        detail.put("engineerLongitude", null);
        detail.put("createTime", wo.getCreateTime());
        detail.put("completeTime", wo.getCompleteTime());
        detail.put("signImage", wo.getSignImage());
        detail.put("repairNotes", wo.getDescription());

        // 打卡记录（同时用于填充时间节点）
        List<ZwCheckIn> checkIns = checkInMapper.selectCheckInByWorkOrderId(workOrderId);
        List<Map<String, Object>> checkinList = new java.util.ArrayList<>();
        Date acceptTime = null, departTime = null, arriveTime = null;
        for (ZwCheckIn ci : checkIns) {
            if ("accept".equals(ci.getCheckType())) acceptTime = ci.getCheckTime();
            if ("depart".equals(ci.getCheckType())) departTime = ci.getCheckTime();
            if ("arrive".equals(ci.getCheckType())) arriveTime = ci.getCheckTime();
            Map<String, Object> m = new HashMap<>();
            m.put("checkType", ci.getCheckType());
            m.put("checkTypeName", convertCheckTypeName(ci.getCheckType()));
            m.put("address", ci.getAddress());
            m.put("checkTime", ci.getCheckTime());
            checkinList.add(m);
        }
        detail.put("acceptTime", acceptTime);
        detail.put("departTime", departTime);
        detail.put("arriveTime", arriveTime);
        detail.put("checkins", checkinList);

        // 满意度评价
        ZwSatisfaction sat = satisfactionMapper.selectSatisfactionByWorkOrderId(workOrderId);
        if (sat != null) {
            Map<String, Object> evalMap = new HashMap<>();
            evalMap.put("responseScore", sat.getResponseScore());
            evalMap.put("techScore", sat.getTechScore());
            evalMap.put("overallScore", sat.getOverallScore());
            evalMap.put("comment", sat.getComment());
            detail.put("hasEvaluation", true);
            detail.put("evaluation", evalMap);
        } else {
            detail.put("hasEvaluation", false);
        }

        return detail;
    }

    private String convertOrderTypeName(String orderType) {
        OrderTypeEnum e = OrderTypeEnum.of(orderType);
        return e != null ? e.getLabel() : orderType;
    }

    private String convertPriorityName(String priority) {
        PriorityEnum e = PriorityEnum.of(priority);
        return e != null ? e.getLabel() : priority;
    }

    private String convertStatusName(String status) {
        WorkOrderStatusEnum e = WorkOrderStatusEnum.of(status);
        return e != null ? e.getLabel() : status;
    }

    private String convertCheckTypeName(String checkType) {
        return switch (checkType) {
            case "depart" -> "出发";
            case "arrive" -> "到达";
            case "complete" -> "完工";
            case "accept" -> "接单";
            default -> checkType;
        };
    }
}
