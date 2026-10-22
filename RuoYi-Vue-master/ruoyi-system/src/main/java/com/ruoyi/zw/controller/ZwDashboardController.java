package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 数据大屏Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/dashboard")
public class ZwDashboardController extends BaseController {

    @Autowired
    private com.ruoyi.zw.mapper.ZwDashboardMapper dashboardMapper;

    /**
     * 总览数据
     * 返回：{assetTotal, assetNormal, assetFault, monthInspectionCount, monthWorkOrderCount,
     *       workOrderCompleteRate, faultRate, monthAssetNew}
     */
    @PreAuthorize("@ss.hasPermi('zw:dashboard:admin')")
    @GetMapping("/summary")
    public AjaxResult getSummary() {
        int assetTotal = dashboardMapper.countAssetTotal();
        int assetNormal = dashboardMapper.countAssetNormal();
        int assetFault = dashboardMapper.countAssetFault();
        int monthInspectionCount = dashboardMapper.countMonthInspection();
        int monthWorkOrderCount = dashboardMapper.countMonthWorkOrder();
        int monthWorkOrderComplete = dashboardMapper.countMonthWorkOrderComplete();
        int monthAssetNew = dashboardMapper.countMonthAssetNew();

        // 工单完成率
        BigDecimal workOrderCompleteRate = BigDecimal.ZERO;
        if (monthWorkOrderCount > 0) {
            workOrderCompleteRate = BigDecimal.valueOf(monthWorkOrderComplete)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(monthWorkOrderCount), 1, RoundingMode.HALF_UP);
        }

        // 故障率
        BigDecimal faultRate = BigDecimal.ZERO;
        if (assetTotal > 0) {
            faultRate = BigDecimal.valueOf(assetFault)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(assetTotal), 1, RoundingMode.HALF_UP);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("assetTotal", assetTotal);
        data.put("assetNormal", assetNormal);
        data.put("assetFault", assetFault);
        data.put("monthAssetNew", monthAssetNew);
        data.put("monthInspectionCount", monthInspectionCount);
        data.put("monthWorkOrderCount", monthWorkOrderCount);
        data.put("workOrderCompleteRate", workOrderCompleteRate);
        data.put("faultRate", faultRate);

        return success(data);
    }

    /**
     * 健康度分布（饼图数据）
     * 返回：[{name: '优', value: 120}, {name: '良', value: 80}, ...]
     */
    @PreAuthorize("@ss.hasPermi('zw:dashboard:admin')")
    @GetMapping("/health")
    public AjaxResult getHealth() {
        List<Map<String, Object>> rawList = dashboardMapper.selectHealthDistribution();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row.get("health_level"));
            item.put("value", row.get("cnt"));
            result.add(item);
        }
        return success(result);
    }

    /**
     * 趋势数据（近30天每天）
     * 返回：[{date: '2026-04-01', inspectionCount: 5, workOrderCount: 3}, ...]
     */
    @PreAuthorize("@ss.hasPermi('zw:dashboard:admin')")
    @GetMapping("/trend")
    public AjaxResult getTrend() {
        List<Map<String, Object>> inspectionList = dashboardMapper.selectInspectionTrend();
        List<Map<String, Object>> workOrderList = dashboardMapper.selectWorkOrderTrend();

        // 合并成统一格式
        Map<String, Map<String, Object>> dateMap = new LinkedHashMap<>();

        for (Map<String, Object> row : inspectionList) {
            String date = row.get("stat_date").toString();
            Map<String, Object> entry = dateMap.computeIfAbsent(date, k -> new LinkedHashMap<>());
            entry.put("date", date);
            entry.put("inspectionCount", row.get("inspection_count"));
        }

        for (Map<String, Object> row : workOrderList) {
            String date = row.get("stat_date").toString();
            Map<String, Object> entry = dateMap.computeIfAbsent(date, k -> new LinkedHashMap<>());
            entry.put("date", date);
            entry.put("workOrderCount", row.get("work_order_count"));
        }

        // 补充默认0值并排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> e : dateMap.entrySet()) {
            Map<String, Object> entry = e.getValue();
            entry.putIfAbsent("inspectionCount", 0);
            entry.putIfAbsent("workOrderCount", 0);
            result.add(entry);
        }

        return success(result);
    }
}
