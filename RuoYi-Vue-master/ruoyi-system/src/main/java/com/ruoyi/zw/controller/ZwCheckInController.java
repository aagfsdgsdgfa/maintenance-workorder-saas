package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.service.IZwCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡记录Controller
 */
@RestController
@RequestMapping({"/zw/checkin", "/zw/checkIn"})
public class ZwCheckInController extends BaseController {

    @Autowired
    private IZwCheckInService checkInService;

    /**
     * 查询打卡记录列表
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwCheckIn checkIn) {
        startPage();
        List<ZwCheckIn> list = checkInService.selectZwCheckInList(checkIn);
        return getDataTable(list);
    }

    /**
     * 获取打卡记录详情
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:query')")
    @GetMapping("/{checkId}")
    public AjaxResult getInfo(@PathVariable Long checkId) {
        return success(checkInService.selectZwCheckInById(checkId));
    }

    /**
     * 获取工单的打卡记录
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:query')")
    @GetMapping("/listByWorkOrder/{workOrderId}")
    public AjaxResult listByWorkOrder(@PathVariable Long workOrderId) {
        return success(checkInService.selectCheckInByWorkOrderId(workOrderId));
    }

    /**
     * 新增打卡记录
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:add')")
    @Log(title = "打卡记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwCheckIn checkIn) {
        checkIn.setCreateBy(SecurityUtils.getUsername());
        return toAjax(checkInService.insertZwCheckIn(checkIn));
    }

    /**
     * 修改打卡记录
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:edit')")
    @Log(title = "打卡记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwCheckIn checkIn) {
        checkIn.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(checkInService.updateZwCheckIn(checkIn));
    }

    /**
     * 删除打卡记录
     */
    @PreAuthorize("@ss.hasPermi('zw:checkin:remove')")
    @Log(title = "打卡记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(checkInService.deleteZwCheckInByIds(ids));
    }
}
