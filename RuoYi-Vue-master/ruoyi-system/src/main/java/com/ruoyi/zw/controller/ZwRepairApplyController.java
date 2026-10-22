package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwRepairApply;
import com.ruoyi.zw.service.IZwRepairApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@RestController("zwRepairApplyAdminController")
@RequestMapping("/zw/repairApply/admin")
public class ZwRepairApplyController extends BaseController {

    @Autowired
    private IZwRepairApplyService repairApplyService;

    @PreAuthorize("@ss.hasPermi('zw:repairapply:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwRepairApply repairApply) {
        startPage();
        List<ZwRepairApply> list = repairApplyService.selectZwRepairApplyList(repairApply);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:export')")
    @Log(title = "报修申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ZwRepairApply repairApply) {
        List<ZwRepairApply> list = repairApplyService.selectZwRepairApplyListForExport(repairApply);
        com.ruoyi.common.utils.poi.ExcelUtil<ZwRepairApply> util = new com.ruoyi.common.utils.poi.ExcelUtil<>(ZwRepairApply.class);
        util.exportExcel(response, list, "报修申请数据");
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:query')")
    @GetMapping("/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId) {
        return success(repairApplyService.selectZwRepairApplyById(applyId));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:add')")
    @Log(title = "报修申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwRepairApply repairApply) {
        return toAjax(repairApplyService.insertZwRepairApply(repairApply));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:edit')")
    @Log(title = "报修申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwRepairApply repairApply) {
        return toAjax(repairApplyService.updateZwRepairApply(repairApply));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:remove')")
    @Log(title = "报修申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds) {
        return toAjax(repairApplyService.deleteZwRepairApplyByIds(applyIds));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:accept')")
    @Log(title = "报修申请-受理", businessType = BusinessType.UPDATE)
    @PutMapping("/{applyId}/accept")
    public AjaxResult accept(@PathVariable Long applyId) {
        return toAjax(repairApplyService.acceptRepairApply(applyId));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:transfer')")
    @Log(title = "报修申请-转工单", businessType = BusinessType.UPDATE)
    @PutMapping("/{applyId}/transfer")
    public AjaxResult transfer(@PathVariable Long applyId,
                               @RequestParam(required = false) Long assignedTo,
                               @RequestParam(required = false) String priority) {
        return toAjax(repairApplyService.transferToWorkOrder(applyId, assignedTo, priority));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:accept')")
    @Log(title = "报修申请-一键受理派发", businessType = BusinessType.UPDATE)
    @PostMapping("/{applyId}/acceptAndTransfer")
    public AjaxResult acceptAndTransfer(@PathVariable Long applyId, @RequestParam Long assignedTo) {
        return toAjax(repairApplyService.acceptAndTransfer(applyId, assignedTo));
    }

    @PreAuthorize("@ss.hasPermi('zw:repairapply:accept')")
    @Log(title = "报修申请-批量受理派发", businessType = BusinessType.UPDATE)
    @PostMapping("/acceptAndTransfer/batch")
    public AjaxResult acceptAndTransferBatch(@RequestBody List<Long> applyIds, @RequestParam Long assignedTo) {
        return toAjax(repairApplyService.acceptAndTransferBatch(applyIds, assignedTo));
    }
}