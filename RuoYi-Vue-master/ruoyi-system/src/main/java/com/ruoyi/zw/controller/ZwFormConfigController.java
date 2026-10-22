package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwFormConfig;
import com.ruoyi.zw.service.IZwFormConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/formConfig")
public class ZwFormConfigController extends BaseController {

    @Autowired
    private IZwFormConfigService formConfigService;

    @PreAuthorize("@ss.hasPermi('zw:formConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwFormConfig formConfig) {
        startPage();
        List<ZwFormConfig> list = formConfigService.selectZwFormConfigList(formConfig);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:formConfig:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return success(formConfigService.selectZwFormConfigById(configId));
    }

    @PreAuthorize("@ss.hasPermi('zw:formConfig:add')")
    @Log(title = "表单配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwFormConfig formConfig) {
        return toAjax(formConfigService.insertZwFormConfig(formConfig));
    }

    @PreAuthorize("@ss.hasPermi('zw:formConfig:edit')")
    @Log(title = "表单配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwFormConfig formConfig) {
        return toAjax(formConfigService.updateZwFormConfig(formConfig));
    }

    @PreAuthorize("@ss.hasPermi('zw:formConfig:remove')")
    @Log(title = "表单配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return toAjax(formConfigService.deleteZwFormConfigByIds(configIds));
    }

    /**
     * 根据资产ID匹配表单配置（小程序巡检点执行页使用）
     */
    @PreAuthorize("@ss.hasPermi('zw:formConfig:list')")
    @GetMapping("/match/{assetId}")
    public AjaxResult matchByAsset(@PathVariable Long assetId) {
        return success(formConfigService.selectByAssetId(assetId));
    }
}
