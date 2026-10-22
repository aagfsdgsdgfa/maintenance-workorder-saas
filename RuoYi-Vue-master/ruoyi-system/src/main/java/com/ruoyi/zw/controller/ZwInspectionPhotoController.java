package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.zw.domain.ZwInspectionPhoto;
import com.ruoyi.zw.service.IZwInspectionPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/zw/inspectionPhoto")
public class ZwInspectionPhotoController extends BaseController {

    @Autowired
    private IZwInspectionPhotoService photoService;

    @PreAuthorize("@ss.hasPermi('zw:inspectionPhoto:list')")
    @GetMapping("/list")
    public TableDataInfo list(ZwInspectionPhoto photo) {
        startPage();
        List<ZwInspectionPhoto> list = photoService.selectZwInspectionPhotoList(photo);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPhoto:query')")
    @GetMapping("/{photoId}")
    public AjaxResult getInfo(@PathVariable Long photoId) {
        return success(photoService.selectZwInspectionPhotoById(photoId));
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPhoto:add')")
    @Log(title = "巡检照片", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ZwInspectionPhoto photo) {
        return toAjax(photoService.insertZwInspectionPhoto(photo));
    }

    @PreAuthorize("@ss.hasPermi('zw:inspectionPhoto:remove')")
    @Log(title = "巡检照片", businessType = BusinessType.DELETE)
    @DeleteMapping("/{photoIds}")
    public AjaxResult remove(@PathVariable Long[] photoIds) {
        return toAjax(photoService.deleteZwInspectionPhotoByIds(photoIds));
    }
}
