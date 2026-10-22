package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.service.IZwAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 资产管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/asset")
public class ZwAssetController extends BaseController {

    @Autowired
    private IZwAssetService assetService;

    /**
     * 资产列表查询
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwAsset asset) {
        startPage();
        List<ZwAsset> list = assetService.selectZwAssetList(asset);
        return getDataTable(list);
    }

    /**
     * 导出资产Excel
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:export')")
    @Log(title = "资产", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, ZwAsset asset) {
        List<ZwAsset> list = assetService.selectZwAssetList(asset);
        ExcelUtil<ZwAsset> util = new ExcelUtil<ZwAsset>(ZwAsset.class);
        util.exportExcel(response, list, "资产数据");
    }

    /**
     * 资产详情查询
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:query')")
    @GetMapping("/{assetId}")
    public AjaxResult getInfo(@PathVariable Long assetId) {
        return success(assetService.selectZwAssetById(assetId));
    }

    /**
     * 扫码查询资产
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:query')")
    @GetMapping("/scan/{qrCode}")
    public AjaxResult scan(@PathVariable String qrCode) {
        return success(assetService.selectZwAssetByQrCode(qrCode));
    }

    /**
     * 新增资产
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:add')")
    @Log(title = "资产", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwAsset asset) {
        asset.setCreateBy(SecurityUtils.getUsername());
        return toAjax(assetService.insertZwAsset(asset));
    }

    /**
     * 修改资产
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:edit')")
    @Log(title = "资产", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwAsset asset) {
        asset.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(assetService.updateZwAsset(asset));
    }

    /**
     * 删除资产
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:remove')")
    @Log(title = "资产", businessType = BusinessType.DELETE)
    @DeleteMapping("/{assetIds}")
    public AjaxResult remove(@PathVariable Long[] assetIds) {
        return toAjax(assetService.deleteZwAssetByIds(assetIds));
    }

    /**
     * 获取资产二维码（自动生成）
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:query')")
    @GetMapping("/{assetId}/qrcode")
    public AjaxResult getQrCode(@PathVariable Long assetId) {
        String qrUrl = assetService.generateQrCode(assetId);
        if (qrUrl == null) {
            return error("生成二维码失败");
        }
        return success(qrUrl);
    }

    /**
     * 批量生成二维码
     */
    @PreAuthorize("@ss.hasPermi('zw:asset:query')")
    @GetMapping("/qrcode/batch")
    public AjaxResult batchQrCode(@RequestParam Long[] assetIds) {
        java.util.List<String> urls = new java.util.ArrayList<>();
        for (Long assetId : assetIds) {
            String url = assetService.generateQrCode(assetId);
            if (url != null) urls.add(url);
        }
        return success(urls);
    }
}
