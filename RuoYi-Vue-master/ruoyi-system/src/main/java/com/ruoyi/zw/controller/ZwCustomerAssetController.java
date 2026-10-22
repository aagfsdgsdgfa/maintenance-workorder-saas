package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.service.IZwAssetService;
import com.ruoyi.zw.service.IZwCustomerAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户资产Controller
 * 无权限注解，允许外部访问
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/customer/asset")
public class ZwCustomerAssetController extends BaseController {

    @Autowired
    private IZwAssetService assetService;

    @Autowired
    private IZwCustomerAssetService customerAssetService;

    /**
     * 查询当前客户资产列表（分页）
     * GET /zw/customer/asset/list?assetName=&assetType=&status=&warrantyStatus=&qrCode=&assetCode=&pageNum=1&pageSize=10
     * customerId 从登录态获取，禁止通过请求参数传递
     */
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warrantyStatus,
            @RequestParam(required = false) String qrCode,
            @RequestParam(required = false) String assetCode,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        // 从登录态取customerId，防止越权查询其他客户资产
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return new TableDataInfo(java.util.Collections.emptyList(), 0L);
        }
        Map<String, Object> result = customerAssetService.selectAssetList(
                customerId, assetName, assetType, status, warrantyStatus, qrCode, assetCode, pageNum, pageSize);
        List<?> rows = (List<?>) result.get("rows");
        long total = Long.parseLong(result.get("total").toString());
        return new TableDataInfo(rows, total);
    }

    /**
     * 获取资产详情
     * GET /zw/customer/asset/{assetId}
     * customerId 从登录态获取
     */
    @GetMapping("/{assetId}")
    public AjaxResult getInfo(@PathVariable Long assetId) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("无权限访问");
        }
        ZwAsset asset = assetService.selectZwAssetById(assetId);
        if (asset == null || !customerId.equals(asset.getCustomerId())) {
            return error("资产不存在或无权限访问");
        }
        return success(asset);
    }

    /**
     * 获取资产时间轴
     * GET /zw/customer/asset/{assetId}/logs
     * customerId 从登录态获取
     */
    @GetMapping("/{assetId}/logs")
    public AjaxResult getAssetLogs(@PathVariable Long assetId) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("无权限访问");
        }
        ZwAsset asset = assetService.selectZwAssetById(assetId);
        if (asset == null || !customerId.equals(asset.getCustomerId())) {
            return error("资产不存在或无权限访问");
        }
        List<Map<String, Object>> logs = customerAssetService.selectAssetLogs(assetId);
        return success(logs);
    }
}
