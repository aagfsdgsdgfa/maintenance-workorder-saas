package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.MaintenanceContract;
import com.ruoyi.zw.service.IZwContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户合同Controller
 * 无权限注解，允许外部访问
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/zw/customer/contract")
public class ZwCustomerContractController extends BaseController {

    @Autowired
    private IZwContractService contractService;

    /**
     * 查询当前客户合同列表（含已使用次数）
     * customerId 从登录态获取，禁止通过请求参数传递
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        // 从登录态取customerId，防止越权查询其他客户合同
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return new TableDataInfo(java.util.Collections.emptyList(), 0L);
        }
        MaintenanceContract query = new MaintenanceContract();
        query.setCustomerId(customerId);
        List<MaintenanceContract> list = contractService.selectContractList(query);
        return getDataTable(list);
    }

    /**
     * 获取合同详情（含关联资产、剩余次数）
     * customerId 从登录态获取
     */
    @GetMapping("/{contractId}")
    public AjaxResult getInfo(@PathVariable Long contractId) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("无权限访问");
        }
        MaintenanceContract contract = contractService.selectContractById(contractId);
        if (contract == null || !customerId.equals(contract.getCustomerId())) {
            return error("合同不存在或无权限访问");
        }
        Map<String, Object> detail = contractService.selectContractDetail(contractId);
        return success(detail);
    }

    /**
     * 客户提交新增合同申请
     * customerId 从登录态自动注入，禁止前端伪造
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody MaintenanceContract contract) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("无权限访问，请重新登录");
        }
        String username = SecurityUtils.getUsername();
        contract.setCustomerId(customerId);
        contract.setCreateBy(username);
        contract.setStatus("0");
        return toAjax(contractService.insertContract(contract));
    }
}
