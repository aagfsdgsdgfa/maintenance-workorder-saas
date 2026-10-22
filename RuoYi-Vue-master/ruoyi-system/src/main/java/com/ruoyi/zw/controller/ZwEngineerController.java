package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwEngineer;
import com.ruoyi.zw.domain.vo.ZwEngineerVo;
import com.ruoyi.zw.service.IZwEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工程师管理 Controller
 */
@RestController
@RequestMapping("/zw/engineer")
public class ZwEngineerController extends BaseController {

    @Autowired
    private IZwEngineerService engineerService;

    /**
     * 获取工程师列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('zw:engineer:list')")
    public TableDataInfo list(ZwEngineer engineer) {
        startPage();
        List<ZwEngineerVo> list = engineerService.selectEngineerList(engineer);
        return getDataTable(list);
    }

    /**
     * 导出工程师列表
     */
    @Log(title = "工程师管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermi('zw:engineer:export')")
    public AjaxResult export(ZwEngineer engineer) {
        List<ZwEngineerVo> list = engineerService.selectEngineerList(engineer);
        return AjaxResult.success("共 " + list.size() + " 条工程师数据", list);
    }

    /**
     * 获取工程师详情
     */
    @GetMapping("/{engineerId}")
    @PreAuthorize("@ss.hasPermi('zw:engineer:list')")
    public AjaxResult getInfo(@PathVariable Long engineerId) {
        return success(engineerService.selectEngineerById(engineerId));
    }

    /**
     * 新增工程师（关联已有系统用户）
     */
    @Log(title = "工程师管理", businessType = BusinessType.INSERT)
    @PostMapping
    @PreAuthorize("@ss.hasPermi('zw:engineer:add')")
    public AjaxResult add(@Validated @RequestBody ZwEngineer engineer) {
        return toAjax(engineerService.insertEngineer(engineer));
    }

    /**
     * 修改工程师
     */
    @Log(title = "工程师管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @PreAuthorize("@ss.hasPermi('zw:engineer:edit')")
    public AjaxResult edit(@Validated @RequestBody ZwEngineer engineer) {
        return toAjax(engineerService.updateEngineer(engineer));
    }

    /**
     * 删除工程师
     */
    @Log(title = "工程师管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{engineerIds}")
    @PreAuthorize("@ss.hasPermi('zw:engineer:remove')")
    public AjaxResult remove(@PathVariable Long[] engineerIds) {
        return toAjax(engineerService.deleteEngineerByIds(engineerIds));
    }

    /**
     * 获取可派发工程师列表（用于工单派发弹窗，支持姓名搜索和分页）
     */
    @GetMapping("/dispatchable")
    @PreAuthorize("@ss.hasPermi('zw:engineer:list')")
    public TableDataInfo dispatchable(ZwEngineer engineer) {
        startPage();
        List<ZwEngineerVo> list = engineerService.selectDispatchableEngineers(engineer);
        return getDataTable(list);
    }

    /**
     * 工单派发
     */
    @Log(title = "工单派发", businessType = BusinessType.OTHER)
    @PostMapping("/dispatch/{workOrderId}")
    @PreAuthorize("@ss.hasPermi('zw:engineer:dispatch')")
    public AjaxResult dispatch(@PathVariable Long workOrderId, @RequestBody DispatchRequest request) {
        return toAjax(engineerService.dispatchWorkOrder(workOrderId, request.getEngineerId()));
    }

    /**
     * 工程师派发历史
     */
    @GetMapping("/dispatchHistory/{engineerId}")
    @PreAuthorize("@ss.hasPermi('zw:engineer:list')")
    public TableDataInfo dispatchHistory(@PathVariable Long engineerId) {
        startPage();
        return getDataTable(engineerService.selectDispatchHistoryByEngineerId(engineerId));
    }

    /**
     * 派发请求体
     */
    public static class DispatchRequest {
        private Long engineerId;
        public Long getEngineerId() { return engineerId; }
        public void setEngineerId(Long engineerId) { this.engineerId = engineerId; }
    }
}
