package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwKnowledgeBase;
import com.ruoyi.zw.service.IZwKnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/knowledgeBase")
public class ZwKnowledgeBaseController extends BaseController {

    @Autowired
    private IZwKnowledgeBaseService knowledgeBaseService;

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwKnowledgeBase knowledgeBase) {
        startPage();
        List<ZwKnowledgeBase> list = knowledgeBaseService.selectZwKnowledgeBaseList(knowledgeBase);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:query')")
    @GetMapping("/{kbId}")
    public AjaxResult getInfo(@PathVariable Long kbId) {
        return success(knowledgeBaseService.selectZwKnowledgeBaseById(kbId));
    }

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:add')")
    @Log(title = "知识库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwKnowledgeBase knowledgeBase) {
        return toAjax(knowledgeBaseService.insertZwKnowledgeBase(knowledgeBase));
    }

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:edit')")
    @Log(title = "知识库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwKnowledgeBase knowledgeBase) {
        return toAjax(knowledgeBaseService.updateZwKnowledgeBase(knowledgeBase));
    }

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:remove')")
    @Log(title = "知识库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{kbIds}")
    public AjaxResult remove(@PathVariable Long[] kbIds) {
        return toAjax(knowledgeBaseService.deleteZwKnowledgeBaseByIds(kbIds));
    }

    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:list')")
    @GetMapping("/suggest")
    public AjaxResult suggest(@RequestParam String keyword) {
        return success(knowledgeBaseService.suggest(keyword));
    }

    /**
     * 知识库搜索（分页）
     */
    @PreAuthorize("@ss.hasPermi('zw:knowledgeBase:list')")
    @GetMapping("/search")
    public TableDataInfo search(ZwKnowledgeBase knowledgeBase) {
        startPage();
        List<ZwKnowledgeBase> list = knowledgeBaseService.selectZwKnowledgeBaseList(knowledgeBase);
        return getDataTable(list);
    }
}
