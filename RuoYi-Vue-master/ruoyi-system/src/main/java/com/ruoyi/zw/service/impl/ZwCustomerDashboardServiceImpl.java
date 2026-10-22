package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.MaintenanceContract;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwContractAssetRelationMapper;
import com.ruoyi.zw.mapper.ZwContractMapper;
import com.ruoyi.zw.mapper.ZwDashboardMapper;
import com.ruoyi.zw.mapper.ZwSatisfactionMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.service.IZwCustomerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户驾驶舱Service实现
 *
 * @author ruoyi
 */
@Service
public class ZwCustomerDashboardServiceImpl implements IZwCustomerDashboardService {

    @Autowired
    private ZwDashboardMapper dashboardMapper;

    @Autowired
    private ZwContractMapper contractMapper;

    @Autowired
    private ZwAssetMapper assetMapper;

    @Autowired
    private ZwWorkOrderMapper workOrderMapper;

    @Autowired
    private ZwSatisfactionMapper satisfactionMapper;

    @Autowired
    private ZwContractAssetRelationMapper contractAssetRelationMapper;

    @Override
    public Map<String, Object> selectCustomerDashboard(Long customerId) {
        Map<String, Object> dashboard = new HashMap<>();

        // --- 资产概览 ---
        List<ZwAsset> allAssets = assetMapper.selectZwAssetList(buildAssetQuery(customerId));
        int totalCount = allAssets.size();
        int normalCount = (int) allAssets.stream().filter(a -> "0".equals(a.getStatus())).count();
        int faultCount = (int) allAssets.stream().filter(a -> "1".equals(a.getStatus())).count();
        int repairCount = (int) allAssets.stream().filter(a -> "2".equals(a.getStatus())).count();
        int scrappedCount = (int) allAssets.stream().filter(a -> "3".equals(a.getStatus())).count();

        Date now = new Date();
        int inWarrantyCount = (int) allAssets.stream()
                .filter(a -> a.getWarrantyEndDate() != null && a.getWarrantyEndDate().after(now))
                .count();
        int outOfWarrantyCount = totalCount - inWarrantyCount;
        int expiringWarrantyCount = (int) allAssets.stream()
                .filter(a -> a.getWarrantyEndDate() != null && inWarranty(a.getWarrantyEndDate(), now))
                .count();

        // 质保内且有合同（通过contract_asset_relations关联）
        // inContractCount 需关联合同资产关系表，这里做简化估算：质保内资产数作为参考
        int inContractCount = inWarrantyCount;

        Map<String, Object> assetOverview = new HashMap<>();
        assetOverview.put("totalCount", totalCount);
        assetOverview.put("normalCount", normalCount);
        assetOverview.put("faultCount", faultCount);
        assetOverview.put("repairCount", repairCount);
        assetOverview.put("scrappedCount", scrappedCount);
        assetOverview.put("inWarrantyCount", inWarrantyCount);
        assetOverview.put("outOfWarrantyCount", outOfWarrantyCount);
        assetOverview.put("inContractCount", inContractCount);
        assetOverview.put("expiringWarrantyCount", expiringWarrantyCount);
        dashboard.put("assetOverview", assetOverview);

        // --- 服务概览 ---
        // 本月巡检完成率：本月完成的巡检任务数 / 本月计划巡检数
        int monthInspectionComplete = dashboardMapper.countMonthInspection();
        int monthInspectionTotal = dashboardMapper.countMonthInspectionPlan();
        double monthlyInspectionCompleteRate = monthInspectionTotal > 0
                ? Math.round((double) monthInspectionComplete / monthInspectionTotal * 1000.0) / 10.0
                : 0.0;

        // 本月维修工单总数
        List<ZwWorkOrder> monthWorkOrders = workOrderMapper.selectZwWorkOrderList(
                buildMonthWorkOrderQuery(customerId));
        int monthlyRepairTotal = (int) monthWorkOrders.stream()
                .filter(wo -> "2".equals(wo.getOrderType()))
                .count();

        // 响应时效（本月完成工单的平均完成时长）
        double avgResponseTime = computeAvgResponseTime(monthWorkOrders);

        // 满意度评分（本月已完成工单的满意度）
        double satisfactionScore = computeAvgSatisfaction(monthWorkOrders);

        // 工单统计
        int pendingWorkOrderCount = (int) monthWorkOrders.stream().filter(wo -> "0".equals(wo.getStatus())).count();
        int processingWorkOrderCount = (int) monthWorkOrders.stream().filter(wo -> "1".equals(wo.getStatus()) || "2".equals(wo.getStatus())).count();
        int completedThisMonth = (int) monthWorkOrders.stream().filter(wo -> "3".equals(wo.getStatus())).count();

        Map<String, Object> serviceOverview = new HashMap<>();
        serviceOverview.put("monthlyInspectionCompleteRate", monthlyInspectionCompleteRate);
        serviceOverview.put("monthlyRepairTotal", monthlyRepairTotal);
        serviceOverview.put("avgResponseTime", avgResponseTime);
        serviceOverview.put("satisfactionScore", satisfactionScore);
        serviceOverview.put("pendingWorkOrderCount", pendingWorkOrderCount);
        serviceOverview.put("processingWorkOrderCount", processingWorkOrderCount);
        serviceOverview.put("completedThisMonth", completedThisMonth);
        dashboard.put("serviceOverview", serviceOverview);

        // --- 故障排行 ---
        // 按资产故障次数排行（简化版：按当前故障状态的资产 + 按历史巡检异常数估算）
        List<Map<String, Object>> topFaultAssets = allAssets.stream()
                .filter(a -> "1".equals(a.getStatus()) || "2".equals(a.getStatus()))
                .limit(5)
                .map(a -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("assetId", a.getAssetId());
                    m.put("assetName", a.getAssetName());
                    m.put("faultCount", "1".equals(a.getStatus()) ? 1 : 0);
                    return m;
                })
                .collect(Collectors.toList());
        dashboard.put("topFaultAssets", topFaultAssets);

        // --- 即将到期的合同 ---
        List<MaintenanceContract> contracts = contractMapper.selectZwContractByCustomerId(customerId);
        Date thirtyDaysLater = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        List<Map<String, Object>> expiringContracts = contracts.stream()
                .filter(c -> c.getEndDate() != null && c.getEndDate().before(thirtyDaysLater) && c.getEndDate().after(now))
                .sorted(Comparator.comparing(MaintenanceContract::getEndDate))
                .limit(5)
                .map(c -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("contractId", c.getContractId());
                    m.put("contractNo", c.getContractNo());
                    m.put("endDate", c.getEndDate());
                    // 关联资产数（通过 contract_asset_relations 精确统计）
                    int assetCount = contractAssetRelationMapper.selectAssetIdsByContractId(c.getContractId()).size();
                    m.put("assetCount", assetCount);
                    return m;
                })
                .collect(Collectors.toList());
        dashboard.put("expiringContracts", expiringContracts);

        return dashboard;
    }

    private ZwAsset buildAssetQuery(Long customerId) {
        ZwAsset query = new ZwAsset();
        query.setCustomerId(customerId);
        query.setDelFlag("0");
        return query;
    }

    private ZwWorkOrder buildMonthWorkOrderQuery(Long customerId) {
        ZwWorkOrder query = new ZwWorkOrder();
        query.setCustomerId(customerId);
        query.setDelFlag("0");
        return query;
    }

    private boolean inWarranty(Date warrantyEndDate, Date now) {
        long diffDays = (warrantyEndDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
        return diffDays > 0 && diffDays <= 30;
    }

    private double computeAvgResponseTime(List<ZwWorkOrder> workOrders) {
        List<ZwWorkOrder> completed = workOrders.stream()
                .filter(wo -> "3".equals(wo.getStatus()) && wo.getCreateTime() != null && wo.getCompleteTime() != null)
                .collect(Collectors.toList());
        if (completed.isEmpty()) return 0.0;
        long totalHours = 0;
        for (ZwWorkOrder wo : completed) {
            long diffMs = wo.getCompleteTime().getTime() - wo.getCreateTime().getTime();
            totalHours += diffMs / (1000.0 * 60 * 60);
        }
        return Math.round(totalHours / completed.size() * 10.0) / 10.0;
    }

    private double computeAvgSatisfaction(List<ZwWorkOrder> workOrders) {
        if (workOrders == null || workOrders.isEmpty()) return 0.0;
        java.util.OptionalDouble avg = workOrders.stream()
                .filter(wo -> "3".equals(wo.getStatus()))
                .map(wo -> satisfactionMapper.selectSatisfactionByWorkOrderId(wo.getWorkOrderId()))
                .filter(sat -> sat != null && sat.getOverallScore() != null)
                .mapToInt(ZwSatisfaction::getOverallScore)
                .average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
    }
}
