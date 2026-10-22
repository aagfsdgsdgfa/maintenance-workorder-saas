package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.MaintenanceContract;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwContractAssetRelationMapper;
import com.ruoyi.zw.mapper.ZwContractMapper;
import com.ruoyi.zw.mapper.ZwDashboardMapper;
import com.ruoyi.zw.mapper.ZwSatisfactionMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Dashboard 巡检完成率统计测试
 *
 * 验证：修复前 monthInspectionTotal = monthInspectionComplete（永远 100%）
 *       修复后使用 countMonthInspectionPlan() 真实统计
 */
@ExtendWith(MockitoExtension.class)
class DashboardInspectionStatsTest {

    @Mock private ZwDashboardMapper dashboardMapper;
    @Mock private ZwContractMapper contractMapper;
    @Mock private ZwAssetMapper assetMapper;
    @Mock private ZwWorkOrderMapper workOrderMapper;
    @Mock private ZwSatisfactionMapper satisfactionMapper;
    @Mock private ZwContractAssetRelationMapper contractAssetRelationMapper;

    @InjectMocks
    private ZwCustomerDashboardServiceImpl dashboardService;

    @Test
    @DisplayName("巡检完成率 - 3 已完成 + 2 待执行 = 60%")
    void inspectionCompletionRate_partialComplete() {
        Long customerId = 100L;

        // 关键修复点：完成数和计划数应该用不同方法统计
        when(dashboardMapper.countMonthInspection()).thenReturn(3);
        when(dashboardMapper.countMonthInspectionPlan()).thenReturn(5);

        // 资产、工单、合同都给空（避免触发 N+1 或 NPE）
        when(assetMapper.selectZwAssetList(any())).thenReturn(Collections.emptyList());
        when(workOrderMapper.selectZwWorkOrderList(any())).thenReturn(Collections.emptyList());
        when(contractMapper.selectZwContractByCustomerId(customerId)).thenReturn(Collections.emptyList());

        Map<String, Object> result = dashboardService.selectCustomerDashboard(customerId);

        @SuppressWarnings("unchecked")
        Map<String, Object> serviceOverview = (Map<String, Object>) result.get("serviceOverview");
        assertNotNull(serviceOverview, "serviceOverview 不应为空");

        Double rate = (Double) serviceOverview.get("monthlyInspectionCompleteRate");
        assertNotNull(rate, "巡检完成率不应为 null");
        assertEquals(60.0, rate, "3/5 = 60%");
    }

    @Test
    @DisplayName("巡检完成率 - 0 已完成 + 0 计划 = 0%（避免除零）")
    void inspectionCompletionRate_noTask() {
        when(dashboardMapper.countMonthInspection()).thenReturn(0);
        when(dashboardMapper.countMonthInspectionPlan()).thenReturn(0);
        when(assetMapper.selectZwAssetList(any())).thenReturn(Collections.emptyList());
        when(workOrderMapper.selectZwWorkOrderList(any())).thenReturn(Collections.emptyList());
        when(contractMapper.selectZwContractByCustomerId(any())).thenReturn(Collections.emptyList());

        Map<String, Object> result = dashboardService.selectCustomerDashboard(1L);

        @SuppressWarnings("unchecked")
        Map<String, Object> serviceOverview = (Map<String, Object>) result.get("serviceOverview");
        Double rate = (Double) serviceOverview.get("monthlyInspectionCompleteRate");
        assertEquals(0.0, rate, "无任务时完成率应为 0，不应抛除零异常");
    }

    @Test
    @DisplayName("巡检完成率 - 全部完成 = 100%")
    void inspectionCompletionRate_allComplete() {
        when(dashboardMapper.countMonthInspection()).thenReturn(8);
        when(dashboardMapper.countMonthInspectionPlan()).thenReturn(8);
        when(assetMapper.selectZwAssetList(any())).thenReturn(Collections.emptyList());
        when(workOrderMapper.selectZwWorkOrderList(any())).thenReturn(Collections.emptyList());
        when(contractMapper.selectZwContractByCustomerId(any())).thenReturn(Collections.emptyList());

        Map<String, Object> result = dashboardService.selectCustomerDashboard(1L);

        @SuppressWarnings("unchecked")
        Map<String, Object> serviceOverview = (Map<String, Object>) result.get("serviceOverview");
        Double rate = (Double) serviceOverview.get("monthlyInspectionCompleteRate");
        assertEquals(100.0, rate);
    }

    @Test
    @DisplayName("合同资产数 - 通过 contract_asset_relations 精确统计（不再硬编码 0）")
    void expiringContract_assetCount_fromRelationTable() {
        Long customerId = 100L;
        MaintenanceContract contract = new MaintenanceContract();
        contract.setContractId(200L);
        contract.setContractNo("HT-2026-001");
        contract.setEndDate(new Date(System.currentTimeMillis() + 15L * 24 * 60 * 60 * 1000)); // 15 天后到期
        contract.setStatus("0");
        contract.setDelFlag("0");

        when(assetMapper.selectZwAssetList(any())).thenReturn(Collections.emptyList());
        when(workOrderMapper.selectZwWorkOrderList(any())).thenReturn(Collections.emptyList());
        when(contractMapper.selectZwContractByCustomerId(customerId)).thenReturn(List.of(contract));
        // 关键修复点：assetCount 来自 contract_asset_relations，不再硬编码 0
        when(contractAssetRelationMapper.selectAssetIdsByContractId(200L))
                .thenReturn(List.of(10L, 11L, 12L, 13L, 14L));

        Map<String, Object> result = dashboardService.selectCustomerDashboard(customerId);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> expiringContracts =
                (List<Map<String, Object>>) result.get("expiringContracts");
        assertNotNull(expiringContracts);
        assertEquals(1, expiringContracts.size());

        Map<String, Object> firstContract = expiringContracts.get(0);
        assertEquals(5, firstContract.get("assetCount"),
                "assetCount 应来自 contract_asset_relations 真实统计，不应是 0");
    }
}
