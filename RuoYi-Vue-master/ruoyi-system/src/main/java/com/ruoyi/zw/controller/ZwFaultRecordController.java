package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwFaultRecord;
import com.ruoyi.zw.service.IZwFaultRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/faultRecord")
public class ZwFaultRecordController extends BaseController {

    @Autowired
    private IZwFaultRecordService faultRecordService;

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwFaultRecord faultRecord) {
        startPage();
        List<ZwFaultRecord> list = faultRecordService.selectZwFaultRecordList(faultRecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:query')")
    @GetMapping("/{faultId}")
    public AjaxResult getInfo(@PathVariable Long faultId) {
        return success(faultRecordService.selectZwFaultRecordById(faultId));
    }

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:add')")
    @Log(title = "故障记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwFaultRecord faultRecord) {
        return toAjax(faultRecordService.insertZwFaultRecord(faultRecord));
    }

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:edit')")
    @Log(title = "故障记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwFaultRecord faultRecord) {
        return toAjax(faultRecordService.updateZwFaultRecord(faultRecord));
    }

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:remove')")
    @Log(title = "故障记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{faultIds}")
    public AjaxResult remove(@PathVariable Long[] faultIds) {
        return toAjax(faultRecordService.deleteZwFaultRecordByIds(faultIds));
    }

    @PreAuthorize("@ss.hasPermi('zw:faultRecord:edit')")
    @Log(title = "故障记录", businessType = BusinessType.UPDATE)
    @PostMapping("/toWorkOrder/{faultId}")
    public AjaxResult toWorkOrder(@PathVariable Long faultId) {
        return success(faultRecordService.faultToWorkOrder(faultId));
    }

    /**
     * 标记故障已解决
     */
    @PreAuthorize("@ss.hasPermi('zw:faultRecord:edit')")
    @Log(title = "故障已解决", businessType = BusinessType.UPDATE)
    @PutMapping("/{faultId}/resolve")
    public AjaxResult resolve(@PathVariable Long faultId, @RequestParam(required = false) String resolution) {
        return toAjax(faultRecordService.resolveFault(faultId, resolution));
    }

    /**
     * 新增故障并同时创建工单（派发给指定工程师）
     */
    @PreAuthorize("@ss.hasPermi('zw:faultRecord:add')")
    @Log(title = "故障上报（含工单）", businessType = BusinessType.INSERT)
    @PostMapping("/withWorkOrder")
    public AjaxResult createWithWorkOrder(@RequestBody ZwFaultRecord faultRecord, @RequestParam(required = false) Long assignedTo) {
        Long workOrderId = faultRecordService.createFaultWithWorkOrder(faultRecord, assignedTo);
        return success(workOrderId);
    }
}
