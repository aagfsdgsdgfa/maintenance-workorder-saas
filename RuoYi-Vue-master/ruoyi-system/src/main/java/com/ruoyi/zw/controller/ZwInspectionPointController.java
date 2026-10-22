package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwInspectionPoint;
import com.ruoyi.zw.service.IZwInspectionPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巡检点Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/inspectionPoint")
public class ZwInspectionPointController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ZwInspectionPointController.class);

    @Autowired
    private IZwInspectionPointService inspectionPointService;

    /**
     * 查询巡检点列表
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwInspectionPoint inspectionPoint) {
        startPage();
        List<ZwInspectionPoint> list = inspectionPointService.selectZwInspectionPointList(inspectionPoint);
        return getDataTable(list);
    }

    /**
     * 获取巡检点详情
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:list')")
    @GetMapping("/{pointId}")
    public AjaxResult getInfo(@PathVariable("pointId") Long pointId) {
        return success(inspectionPointService.selectZwInspectionPointById(pointId));
    }

    /**
     * 新增巡检点
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:add')")
    @Log(title = "巡检点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwInspectionPoint inspectionPoint) {
        inspectionPoint.setCreateBy(SecurityUtils.getUsername());
        return toAjax(inspectionPointService.insertZwInspectionPoint(inspectionPoint));
    }

    /**
     * 修改巡检点
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:edit')")
    @Log(title = "巡检点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ZwInspectionPoint inspectionPoint) {
        inspectionPoint.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(inspectionPointService.updateZwInspectionPoint(inspectionPoint));
    }

    /**
     * 删除巡检点
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:remove')")
    @Log(title = "巡检点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pointIds}")
    public AjaxResult remove(@PathVariable Long[] pointIds) {
        return toAjax(inspectionPointService.deleteZwInspectionPointByIds(pointIds));
    }

    /**
     * 按资产查询巡检点
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:list')")
    @GetMapping("/asset/{assetId}")
    public AjaxResult listByAsset(@PathVariable("assetId") Long assetId) {
        return success(inspectionPointService.selectZwInspectionPointByAssetId(assetId));
    }

    /**
     * 启用/停用巡检点
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:edit')")
    @Log(title = "巡检点状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{pointId}/toggle")
    public AjaxResult toggle(@PathVariable("pointId") Long pointId) {
        try {
            return toAjax(inspectionPointService.toggleStatus(pointId));
        } catch (RuntimeException e) {
            log.warn("巡检点状态切换失败 pointId={}: {}", pointId, e.getMessage());
            return error(e.getMessage());
        }
    }

    /**
     * 巡检点扫码验证
     */
    @PreAuthorize("@ss.hasPermi('zw:inspectionpoint:edit')")
    @PutMapping("/{pointId}/verify")
    public AjaxResult verify(@PathVariable("pointId") Long pointId,
                             @RequestParam(required = false) String scanCode) {
        boolean ok = inspectionPointService.verifyPoint(pointId, scanCode);
        if (ok) {
            return success(true);
        } else {
            return error("扫码验证失败，QR码与巡检点不匹配");
        }
    }
}
