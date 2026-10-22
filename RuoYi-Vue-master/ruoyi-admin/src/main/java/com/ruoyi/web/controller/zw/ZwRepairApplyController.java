package com.ruoyi.web.controller.zw;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zw.domain.ZwRepairApply;
import com.ruoyi.zw.service.IZwRepairApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户报修Controller（外部访问）
 */
@RestController
@RequestMapping("/zw")
public class ZwRepairApplyController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ZwRepairApplyController.class);

    @Autowired
    private IZwRepairApplyService repairApplyService;

    /**
     * 客户提交报修申请（需隔离）
     */
    @PostMapping("/repairApply")
    public AjaxResult add(@RequestBody ZwRepairApply repairApply) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("用户未登录或无归属客户");
        }
        repairApply.setCustomerId(customerId);
        return toAjax(repairApplyService.insertZwRepairApply(repairApply));
    }

    /**
     * 查询当前客户的报修申请列表（需数据隔离）
     */
    @GetMapping("/customer/repairApply/list")
    public TableDataInfo listForCustomer() {
        startPage();
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return new TableDataInfo(java.util.Collections.emptyList(), 0L);
        }
        List<ZwRepairApply> list = repairApplyService.selectZwRepairApplyListByCustomerId(customerId);
        return getDataTable(list);
    }

    /**
     * 查询报修申请列表（管理端，有权限）
     */
    @GetMapping("/repairApply/list")
    public TableDataInfo list(ZwRepairApply repairApply) {
        startPage();
        return TableDataInfo.build(
            repairApplyService.selectZwRepairApplyList(repairApply));
    }

    /**
     * 获取报修申请详情（需校验归属）
     */
    @GetMapping("/repairApply/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId) {
        Long customerId = SecurityUtils.getCustomerId();
        ZwRepairApply apply = repairApplyService.selectZwRepairApplyById(applyId);
        if (apply == null) {
            return error("申请不存在");
        }
        if (customerId != null && !customerId.equals(apply.getCustomerId())) {
            return error("无权访问该申请");
        }
        return success(apply);
    }

    /**
     * 受理报修（管理端，有权限）
     */
    @PutMapping("/repairApply/{applyId}/accept")
    public AjaxResult accept(@PathVariable Long applyId) {
        try {
            return toAjax(repairApplyService.acceptRepairApply(applyId));
        } catch (RuntimeException e) {
            log.warn("受理报修失败 applyId={}: {}", applyId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 转工单（管理端，有权限）
     */
    @PutMapping("/repairApply/{applyId}/transfer")
    public AjaxResult transfer(
            @PathVariable Long applyId,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String priority) {
        try {
            return toAjax(repairApplyService.transferToWorkOrder(applyId, assignedTo, priority));
        } catch (RuntimeException e) {
            log.warn("转工单失败 applyId={}: {}", applyId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 一键受理并派发（状态0->2，合并受理+转工单两步操作）
     */
    @PostMapping("/repairApply/{applyId}/acceptAndTransfer")
    public AjaxResult acceptAndTransfer(
            @PathVariable Long applyId,
            @RequestParam(required = false) Long assignedTo) {
        try {
            return toAjax(repairApplyService.acceptAndTransfer(applyId, assignedTo));
        } catch (RuntimeException e) {
            log.warn("一键受理派发失败 applyId={}: {}", applyId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 批量一键受理派发（支持多选）
     */
    @PostMapping("/repairApply/acceptAndTransfer/batch")
    public AjaxResult acceptAndTransferBatch(
            @RequestBody List<Long> applyIds,
            @RequestParam Long assignedTo) {
        try {
            int count = repairApplyService.acceptAndTransferBatch(applyIds, assignedTo);
            return success(count);
        } catch (RuntimeException e) {
            log.warn("批量一键受理派发失败: {}", e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 导出报修申请列表（管理端，有权限，不受客户数据隔离限制）
     */
    @PreAuthorize("@ss.hasPermi('zw:repairapply:export')")
    @PostMapping("/repairApply/export")
    public void export(HttpServletResponse response, ZwRepairApply repairApply) {
        List<ZwRepairApply> list = repairApplyService.selectZwRepairApplyListForExport(repairApply);
        ExcelUtil<ZwRepairApply> util = new ExcelUtil<>(ZwRepairApply.class);
        util.exportExcel(response, list, "报修申请数据");
    }
}