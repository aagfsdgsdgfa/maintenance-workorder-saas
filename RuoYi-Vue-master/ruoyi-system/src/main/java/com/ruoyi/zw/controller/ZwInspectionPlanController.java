package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwInspectionPlan;
import com.ruoyi.zw.service.IZwInspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/inspectionPlan")
public class ZwInspectionPlanController extends BaseController {

    @Autowired
    private IZwInspectionPlanService planService;

    @PreAuthorize("@ss.hasPermi('zw:inspectionPlan:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwInspectionPlan plan) {
        startPage();
        List<ZwInspectionPlan> list = planService.selectZwInspectionPlanList(plan);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPlan:query')")
    @GetMapping("/{planId}")
    public AjaxResult getInfo(@PathVariable Long planId) {
        return success(planService.selectZwInspectionPlanById(planId));
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPlan:add')")
    @Log(title = "巡检计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwInspectionPlan plan) {
        return toAjax(planService.insertZwInspectionPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPlan:edit')")
    @Log(title = "巡检计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwInspectionPlan plan) {
        return toAjax(planService.updateZwInspectionPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPlan:remove')")
    @Log(title = "巡检计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds) {
        return toAjax(planService.deleteZwInspectionPlanByIds(planIds));
    }
}
