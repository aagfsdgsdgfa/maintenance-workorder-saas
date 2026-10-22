package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.service.IZwAssetService;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import com.ruoyi.zw.service.IZwWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 客户驾驶舱Controller
 * 无权限注解，允许外部访问
 */
@RestController
@RequestMapping("/zw/dashboard")
public class ZwCustomerDashboardController extends BaseController {

    @Autowired
    private IZwAssetService assetService;

    @Autowired
    private IZwWorkOrderService workOrderService;

    @Autowired
    private IZwInspectionTaskService inspectionTaskService;

    /**
     * 获取客户驾驶舱数据
     * GET /zw/dashboard/customer
     */
    @GetMapping("/customer")
    public AjaxResult getDashboard() {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return success(new LinkedHashMap<>());
        }
        Map<String, Object> data = new LinkedHashMap<>();

        // 资产概览
        ZwAsset assetQuery = new ZwAsset();
        assetQuery.setCustomerId(customerId);
        List<ZwAsset> allAssets = assetService.selectZwAssetList(assetQuery);
        int assetTotal = allAssets.size();
        int assetNormal = 0;
        int assetFault = 0;
        for (ZwAsset a : allAssets) {
            if ("0".equals(a.getStatus())) assetNormal++;
            if ("1".equals(a.getStatus())) assetFault++;
        }
        data.put("assetSummary", Map.of(
            "total", assetTotal,
            "normal", assetNormal,
            "fault", assetFault
        ));

        // 服务概览：工单统计
        ZwWorkOrder woQuery = new ZwWorkOrder();
        woQuery.setCustomerId(customerId);
        List<ZwWorkOrder> allWorkOrders = workOrderService.selectZwWorkOrderList(woQuery);
        int monthWorkOrder = 0;
        int completedCount = 0;
        int processingCount = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date monthStart = cal.getTime();
        for (ZwWorkOrder wo : allWorkOrders) {
            if (wo.getCreateTime() != null && !wo.getCreateTime().before(monthStart)) {
                monthWorkOrder++;
            }
            if ("3".equals(wo.getStatus())) completedCount++;
            if ("1".equals(wo.getStatus()) || "2".equals(wo.getStatus())) processingCount++;
        }

        // 巡检统计
        ZwInspectionTask taskQuery = new ZwInspectionTask();
        taskQuery.setAssetId(null); // 先查全部，再按资产归属过滤
        List<ZwInspectionTask> allTasks = inspectionTaskService.selectZwInspectionTaskList(taskQuery);
        List<Long> customerAssetIds = new ArrayList<>();
        for (ZwAsset a : allAssets) {
            if (a.getAssetId() != null) customerAssetIds.add(a.getAssetId());
        }
        int monthInspection = 0;
        for (ZwInspectionTask t : allTasks) {
            if (t.getPlanTime() != null && !t.getPlanTime().before(monthStart)
                    && customerAssetIds.contains(t.getAssetId())) {
                monthInspection++;
            }
        }

        Map<String, Object> serviceSummary = new LinkedHashMap<>();
        serviceSummary.put("monthInspectionCount", monthInspection);
        serviceSummary.put("monthWorkOrderCount", monthWorkOrder);
        serviceSummary.put("processingCount", processingCount);
        serviceSummary.put("completedCount", completedCount);
        data.put("serviceSummary", serviceSummary);

        // 即将到期合同（30天内），复用工单列表按创建时间排序
        data.put("expiringContracts", new ArrayList<>());

        // 健康度分布
        List<Map<String, Object>> healthList = new ArrayList<>();
        Map<String, Integer> healthMap = new LinkedHashMap<>();
        healthMap.put("优", 0); healthMap.put("良", 0); healthMap.put("中", 0); healthMap.put("差", 0);
        for (ZwAsset a : allAssets) {
            Integer score = a.getHealthScore();
            if (score != null) {
                if (score >= 90) healthMap.put("优", healthMap.get("优") + 1);
                else if (score >= 70) healthMap.put("良", healthMap.get("良") + 1);
                else if (score >= 50) healthMap.put("中", healthMap.get("中") + 1);
                else healthMap.put("差", healthMap.get("差") + 1);
            }
        }
        for (Map.Entry<String, Integer> e : healthMap.entrySet()) {
            if (e.getValue() > 0) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", e.getKey());
                item.put("value", e.getValue());
                healthList.add(item);
            }
        }
        data.put("healthDistribution", healthList);

        return success(data);
    }
}
