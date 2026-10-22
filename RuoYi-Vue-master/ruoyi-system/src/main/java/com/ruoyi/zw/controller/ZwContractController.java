package com.ruoyi.zw.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.MaintenanceContract;
import com.ruoyi.zw.service.IZwContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 管理端合同CRUD Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/contract")
public class ZwContractController extends BaseController {

    @Autowired
    private IZwContractService contractService;

    /**
     * 查询合同列表
     */
// @PreAuthorize("@ss.hasPermi('zw:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(MaintenanceContract contract) {
        startPage();
        List<MaintenanceContract> list = contractService.selectContractList(contract);
        return getDataTable(list);
    }

    /**
     * 获取合同详情
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(contractService.selectContractById(id));
    }

    /**
     * 新增合同
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:add')")
    @Log(title = "合同", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MaintenanceContract contract) {
        contract.setCreateBy(SecurityUtils.getUsername());
        return toAjax(contractService.insertContract(contract));
    }

    /**
     * 修改合同
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:edit')")
    @Log(title = "合同", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MaintenanceContract contract) {
        contract.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(contractService.updateContract(contract));
    }

    /**
     * 删除合同
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:remove')")
    @Log(title = "合同", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(contractService.deleteContractByIds(ids));
    }

    /**
     * OCR图片扫描识别合同内容
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:add')")
    @PostMapping("/ocrImage")
    public AjaxResult ocrImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("请上传图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return error("仅支持图片文件（jpg、png等）");
        }
        Map<String, Object> result = contractService.ocrContractImage(file);
        if (result.containsKey("_error")) {
            return error((String) result.get("_error"));
        }
        return success(result);
    }

    /**
     * Excel导入解析合同内容
     */
    @PreAuthorize("@ss.hasPermi('zw:contract:add')")
    @PostMapping("/parseExcel")
    public AjaxResult parseExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("请上传Excel文件");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return error("仅支持Excel文件（.xlsx、.xls）");
        }
        Map<String, Object> result = contractService.parseContractExcel(file);
        if (result.containsKey("_error")) {
            return error((String) result.get("_error"));
        }
        return success(result);
    }
}
