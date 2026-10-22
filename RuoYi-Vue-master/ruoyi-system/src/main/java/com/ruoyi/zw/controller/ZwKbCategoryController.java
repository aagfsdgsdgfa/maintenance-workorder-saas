package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwKbCategory;
import com.ruoyi.zw.service.IZwKbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/kbCategory")
public class ZwKbCategoryController extends BaseController {

    @Autowired
    private IZwKbCategoryService kbCategoryService;

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwKbCategory category) {
        startPage();
        List<ZwKbCategory> list = kbCategoryService.selectZwKbCategoryList(category);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        return success(kbCategoryService.selectZwKbCategoryById(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:list')")
    @GetMapping("/all")
    public AjaxResult listAll() {
        return success(kbCategoryService.selectAllKbCategory());
    }

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:add')")
    @Log(title = "知识库分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwKbCategory category) {
        if (!kbCategoryService.checkCategoryCodeUnique(category)) {
            return AjaxResult.error("分类编码已存在");
        }
        return toAjax(kbCategoryService.insertZwKbCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:edit')")
    @Log(title = "知识库分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwKbCategory category) {
        if (!kbCategoryService.checkCategoryCodeUnique(category)) {
            return AjaxResult.error("分类编码已存在");
        }
        return toAjax(kbCategoryService.updateZwKbCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('zw:kbCategory:remove')")
    @Log(title = "知识库分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return toAjax(kbCategoryService.deleteZwKbCategoryByIds(categoryIds));
    }
}
