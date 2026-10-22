package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.domain.ZwWorkOrderDetail;
import com.ruoyi.zw.domain.vo.WorkOrderCompleteRequest;
import com.ruoyi.zw.service.IZwWorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.util.List;

/**
 * 工单管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/workOrder")
public class ZwWorkOrderController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ZwWorkOrderController.class);

    @Autowired
    private IZwWorkOrderService workOrderService;

    /**
     * 查询工单列表
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwWorkOrder workOrder) {
        startPage();
        List<ZwWorkOrder> list = workOrderService.selectZwWorkOrderList(workOrder);
        return getDataTable(list);
    }

/**
     * 导出工单列表
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:export')")
    @Log(title = "工单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, ZwWorkOrder workOrder) {
        List<ZwWorkOrder> list = workOrderService.selectZwWorkOrderList(workOrder);
        ExcelUtil<ZwWorkOrder> util = new ExcelUtil<ZwWorkOrder>(ZwWorkOrder.class);
        util.exportExcel(response, list, "工单数据");
    }

    /**
     * 获取工单详情
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:query')")
    @GetMapping("/{workOrderId}")
    public AjaxResult getInfo(@PathVariable("workOrderId") Long workOrderId) {
        return success(workOrderService.selectZwWorkOrderDetailById(workOrderId));
    }

    /**
     * 新增工单
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:add')")
    @Log(title = "工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwWorkOrder workOrder) {
        workOrder.setCreateBy(SecurityUtils.getUsername());
        return toAjax(workOrderService.insertZwWorkOrder(workOrder));
    }

    /**
     * 工程师现场受理创建工单（无需权限校验）
     */
    @Log(title = "工程师受理建单", businessType = BusinessType.INSERT)
    @PostMapping("/engineer/create")
    public AjaxResult engineerCreate(@RequestBody ZwWorkOrder workOrder) {
        Long engineerId = SecurityUtils.getUserId();
        if (engineerId == null) {
            return error("未找到登录工程师信息");
        }
        workOrder.setCreateBy(SecurityUtils.getUsername());
        workOrder.setAssignedTo(engineerId);
        workOrder.setAssignedBy(engineerId);
        return toAjax(workOrderService.insertZwWorkOrder(workOrder));
    }

    /**
     * 修改工单
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwWorkOrder workOrder) {
        workOrder.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(workOrderService.updateZwWorkOrder(workOrder));
    }

    /**
     * 删除工单
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:remove')")
    @Log(title = "工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{workOrderIds}")
    public AjaxResult remove(@PathVariable Long[] workOrderIds) {
        return toAjax(workOrderService.deleteZwWorkOrderByIds(workOrderIds));
    }

    /**
     * 变更工单状态
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam Long workOrderId, @RequestParam String status) {
        return toAjax(workOrderService.changeStatus(workOrderId, status));
    }

    /**
     * 派单给工程师
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单派单", businessType = BusinessType.UPDATE)
    @PutMapping("/assign")
    public AjaxResult assign(@RequestParam Long workOrderId, @RequestParam Long engineerId) {
        Long assignedBy = SecurityUtils.getUserId();
        return toAjax(workOrderService.assignEngineer(workOrderId, engineerId, assignedBy));
    }

    /**
     * 获取工程师的工单任务列表（供小程序调用）
     * 从 token 获取 engineerId，支持按 status 过滤
     */
    @GetMapping("/myTasks")
    public TableDataInfo myTasks(ZwWorkOrder workOrder) {
        startPage();
        Long engineerId = SecurityUtils.getUserId();
        workOrder.setAssignedTo(engineerId);
        // 兼容前端传入的 "0,1,2" 多状态格式，转换为 list 供 Mapper 使用
        String status = workOrder.getStatus();
        if (status != null && status.contains(",")) {
            workOrder.setStatusList(java.util.Arrays.asList(status.split(",")));
            workOrder.setStatus(null);
        }
        List<ZwWorkOrder> list = workOrderService.selectZwWorkOrderList(workOrder);
        return getDataTable(list);
    }

    /**
     * 获取工程师的工单统计（供小程序调用，返回统计数据）
     */
    @GetMapping("/myTasks/stats")
    public AjaxResult myTasksStats() {
        Long engineerId = SecurityUtils.getUserId();
        return success(workOrderService.selectMyTaskStats(engineerId));
    }

    /**
     * 接单
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单接单", businessType = BusinessType.UPDATE)
    @PutMapping("/accept/{id}")
    public AjaxResult accept(@PathVariable("id") Long id) {
        Long engineerId = SecurityUtils.getUserId();
        try {
            return toAjax(workOrderService.accept(id, engineerId));
        } catch (RuntimeException e) {
            log.warn("工单接单失败 id={}, engineerId={}: {}", id, engineerId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 拒单
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单拒单", businessType = BusinessType.UPDATE)
    @PutMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable("id") Long id, @RequestParam String reason) {
        Long engineerId = SecurityUtils.getUserId();
        try {
            return toAjax(workOrderService.reject(id, engineerId, reason));
        } catch (RuntimeException e) {
            log.warn("工单拒单失败 id={}, engineerId={}: {}", id, engineerId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 开始处理
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单开始处理", businessType = BusinessType.UPDATE)
    @PutMapping("/start/{id}")
    public AjaxResult start(@PathVariable("id") Long id) {
        try {
            return toAjax(workOrderService.start(id));
        } catch (RuntimeException e) {
            log.warn("工单开始处理失败 id={}: {}", id, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 完工确认
     */
    @PreAuthorize("@ss.hasPermi('zw:workorder:edit')")
    @Log(title = "工单完工确认", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{id}")
    public AjaxResult complete(@PathVariable("id") Long id, @RequestBody WorkOrderCompleteRequest req) {
        log.info("完工确认接口收到 id={}, req={}", id, req);
        try {
            return toAjax(workOrderService.complete(id, req.getRepairResult(), req.getSignature(), req.getPhotos(), req.getParts(), req.getNextVisitTime()));
        } catch (RuntimeException e) {
            log.warn("工单完工确认失败 id={}: {}", id, e.getMessage());
            return error(e.getMessage());
        }
    }
}