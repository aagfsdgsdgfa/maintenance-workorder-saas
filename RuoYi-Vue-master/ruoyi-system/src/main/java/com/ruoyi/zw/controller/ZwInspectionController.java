package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import com.ruoyi.zw.service.IZwInspectionResultService;
import com.ruoyi.zw.service.IZwReportService;
import com.ruoyi.zw.service.IZwFormConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 巡检任务管理Controller
 */
@RestController
@RequestMapping("/zw/inspection")
public class ZwInspectionController extends BaseController {

    @Autowired
    private IZwInspectionTaskService taskService;

    @Autowired
    private IZwInspectionResultService resultService;

    @Autowired
    private IZwReportService reportService;

    @Autowired
    private IZwFormConfigService formConfigService;

    /**
     * 查询巡检任务列表
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:list')")
    @GetMapping("/task/list")
    public TableDataInfo list(ZwInspectionTask task) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId != null && !isAdmin()) {
            task.setCustomerId(customerId);
        }
        startPage();
        List<ZwInspectionTask> list = taskService.selectZwInspectionTaskList(task);
        return getDataTable(list);
    }

    private boolean isAdmin() {
        String username = SecurityUtils.getUsername();
        return "admin".equals(username);
    }

    /**
     * 获取巡检任务详情
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:query')")
    @GetMapping("/task/{taskId}")
    public AjaxResult getTaskInfo(@PathVariable Long taskId) {
        return success(taskService.selectZwInspectionTaskById(taskId));
    }

    /**
     * 获取工单下的巡检任务列表
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:query')")
    @GetMapping("/task/listByWorkOrder/{workOrderId}")
    public AjaxResult listByWorkOrder(@PathVariable Long workOrderId) {
        return success(taskService.selectTasksByWorkOrderId(workOrderId));
    }

    /**
     * 新增巡检任务
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:add')")
    @Log(title = "巡检任务", businessType = BusinessType.INSERT)
    @PostMapping("/task")
    public AjaxResult addTask(@RequestBody ZwInspectionTask task) {
        task.setCreateBy(SecurityUtils.getUsername());
        return toAjax(taskService.insertZwInspectionTask(task));
    }

    /**
     * 修改巡检任务
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:edit')")
    @Log(title = "巡检任务", businessType = BusinessType.UPDATE)
    @PutMapping("/task")
    public AjaxResult editTask(@RequestBody ZwInspectionTask task) {
        task.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(taskService.updateZwInspectionTask(task));
    }

    /**
     * 删除巡检任务
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:remove')")
    @Log(title = "巡检任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/task/{taskIds}")
    public AjaxResult removeTask(@PathVariable Long[] taskIds) {
        return toAjax(taskService.deleteZwInspectionTaskByIds(taskIds));
    }

    /**
     * 执行巡检任务（到达现场打卡）
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:edit')")
    @Log(title = "巡检执行", businessType = BusinessType.UPDATE)
    @PutMapping("/task/execute")
    public AjaxResult executeTask(@RequestParam Long taskId,
                                   @RequestParam(required = false) String checkInLocation,
                                   @RequestParam(required = false) String checkInPhoto,
                                   @RequestParam(required = false) String checkInAddress) {
        return toAjax(taskService.executeTask(taskId, checkInLocation, checkInPhoto, checkInAddress));
    }

    /**
     * 完成巡检任务
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:edit')")
    @Log(title = "巡检完成", businessType = BusinessType.UPDATE)
    @PutMapping("/task/complete/{taskId}")
    public AjaxResult completeTask(@PathVariable Long taskId) {
        ZwInspectionTask task = taskService.selectZwInspectionTaskById(taskId);
        if (task == null) {
            return error("任务不存在");
        }
        task.setStatus("3"); // 已完成
        task.setActualEndTime(new java.util.Date());
        task.setUpdateBy(SecurityUtils.getUsername());
        int rows = taskService.updateZwInspectionTask(task);
        if (rows > 0) {
            // 生成巡检报告PDF
            reportService.generateInspectionPdf(taskId);
        }
        return toAjax(rows);
    }

    /**
     * 批量分配工程师
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:edit')")
    @Log(title = "批量分配工程师", businessType = BusinessType.UPDATE)
    @PutMapping("/task/assign")
    public AjaxResult batchAssign(@RequestBody java.util.Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> taskIds = (List<Long>) params.get("taskIds");
        Long engineerId = ((Number) params.get("engineerId")).longValue();
        if (taskIds == null || taskIds.isEmpty()) {
            return error("请选择要分配的任务");
        }
        if (engineerId == null) {
            return error("请选择工程师");
        }
        int rows = taskService.batchAssign(taskIds, engineerId, SecurityUtils.getUsername());
        return toAjax(rows);
    }

    // ==================== 检查结果 ====================

    /**
     * 查询检查结果列表
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:list')")
    @GetMapping("/result/list")
    public TableDataInfo listResult(ZwInspectionResult result) {
        startPage();
        List<ZwInspectionResult> list = resultService.selectZwInspectionResultList(result);
        return getDataTable(list);
    }

    /**
     * 获取检查结果详情
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:query')")
    @GetMapping("/result/{resultId}")
    public AjaxResult getResultInfo(@PathVariable Long resultId) {
        return success(resultService.selectZwInspectionResultById(resultId));
    }

    /**
     * 根据任务ID获取检查结果
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:query')")
    @GetMapping("/result/byTask/{taskId}")
    public AjaxResult getResultByTask(@PathVariable Long taskId) {
        return success(resultService.selectResultByTaskId(taskId));
    }

    /**
     * 提交检查结果
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:add')")
    @Log(title = "检查结果", businessType = BusinessType.INSERT)
    @PostMapping("/result")
    public AjaxResult addResult(@RequestBody ZwInspectionResult result) {
        result.setCreateBy(SecurityUtils.getUsername());
        int rows = resultService.insertZwInspectionResult(result);
        // 同时更新任务状态为进行中
        if (rows > 0 && result.getTaskId() != null) {
            ZwInspectionTask task = new ZwInspectionTask();
            task.setTaskId(result.getTaskId());
            task.setStatus("2"); // 进行中
            task.setUpdateBy(SecurityUtils.getUsername());
            taskService.updateZwInspectionTask(task);
        }
        return toAjax(rows);
    }

    /**
     * 修改检查结果
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:edit')")
    @Log(title = "检查结果", businessType = BusinessType.UPDATE)
    @PutMapping("/result")
    public AjaxResult editResult(@RequestBody ZwInspectionResult result) {
        result.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(resultService.updateZwInspectionResult(result));
    }

    /**
     * 删除检查结果
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:remove')")
    @Log(title = "检查结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/result/{resultIds}")
    public AjaxResult removeResult(@PathVariable Long[] resultIds) {
        return toAjax(resultService.deleteZwInspectionResultByIds(resultIds));
    }

    /**
     * 获取资产对应的巡检表单模板（小程序巡检点执行页使用）
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:query')")
    @GetMapping("/template/{assetId}")
    public AjaxResult getTemplate(@PathVariable Long assetId) {
        return success(formConfigService.selectByAssetId(assetId));
    }

    /**
     * 上传巡检照片（小程序使用，通用上传走 /common/upload）
     */
    @PreAuthorize("@ss.hasPermi('zw:inspection:add')")
    @PostMapping("/photo")
    public AjaxResult uploadPhoto(MultipartFile file) {
        try {
            String filePath = RuoYiConfig.getUploadPath();
            String fileName = FileUploadUtils.upload(filePath, file);
            AjaxResult ajax = AjaxResult.success("上传成功");
            ajax.put("url", fileName);
            ajax.put("fileName", fileName);
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }
}