package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwPart;
import com.ruoyi.zw.service.IZwPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/part")
public class ZwPartController extends BaseController {

    @Autowired
    private IZwPartService partService;

    /**
     * 查询配件列表（小程序和后台共用，去掉权限限制）
     */
    @GetMapping("/list")
    public TableDataInfo list(ZwPart part) {
        startPage();
        List<ZwPart> list = partService.selectZwPartList(part);
        return getDataTable(list);
    }

    /**
     * 获取配件详情
     */
    @PreAuthorize("@ss.hasPermi('zw:part:query')")
    @GetMapping("/{partId}")
    public AjaxResult getInfo(@PathVariable Long partId) {
        return success(partService.selectZwPartById(partId));
    }

    /**
     * 新增配件
     */
    @PreAuthorize("@ss.hasPermi('zw:part:add')")
    @Log(title = "配件管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwPart part) {
        part.setCreateBy(SecurityUtils.getUsername());
        return toAjax(partService.insertZwPart(part));
    }

    /**
     * 修改配件
     */
    @PreAuthorize("@ss.hasPermi('zw:part:edit')")
    @Log(title = "配件管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwPart part) {
        part.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(partService.updateZwPart(part));
    }

    /**
     * 删除配件
     */
    @PreAuthorize("@ss.hasPermi('zw:part:remove')")
    @Log(title = "配件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{partIds}")
    public AjaxResult remove(@PathVariable Long[] partIds) {
        return toAjax(partService.deleteZwPartByIds(partIds));
    }
}
