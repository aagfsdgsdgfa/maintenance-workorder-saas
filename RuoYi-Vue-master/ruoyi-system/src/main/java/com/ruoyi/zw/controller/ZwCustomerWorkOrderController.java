package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.domain.ZwWorkOrderDetail;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.service.IZwWorkOrderService;
import com.ruoyi.zw.service.IZwCheckInService;
import com.ruoyi.zw.service.IZwSatisfactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户工单Controller
 * 无权限注解，允许外部访问
 */
@RestController
@RequestMapping("/zw/customer/workorder")
public class ZwCustomerWorkOrderController extends BaseController {

    @Autowired
    private IZwWorkOrderService workOrderService;

    @Autowired
    private IZwCheckInService checkInService;

    @Autowired
    private IZwSatisfactionService satisfactionService;

    /**
     * 查询当前客户工单列表（分页）
     * customerId 从登录态获取，禁止通过请求参数传递
     */
    @GetMapping("/list")
    public TableDataInfo list(ZwWorkOrder workOrder) {
        startPage();
        // 从登录态取customerId，防止越权查询其他客户工单
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return new TableDataInfo(java.util.Collections.emptyList(), 0L);
        }
        workOrder.setCustomerId(customerId);
        List<ZwWorkOrder> list = workOrderService.selectZwWorkOrderList(workOrder);
        return getDataTable(list);
    }

    /**
     * 获取工单详情（含打卡时间轴、工程师信息、满意度评价）
     */
    @GetMapping("/{workOrderId}")
    public AjaxResult getInfo(@PathVariable Long workOrderId) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("无权限访问");
        }
        ZwWorkOrderDetail detail = workOrderService.selectZwWorkOrderDetailById(workOrderId);
        if (detail == null || !customerId.equals(detail.getCustomerId())) {
            return error("工单不存在或无权限访问");
        }

        // 打卡记录时间轴
        List<ZwCheckIn> checkInRecords = checkInService.selectCheckInByWorkOrderId(workOrderId);
        detail.setCheckInRecords(checkInRecords);

        // 满意度评价
        ZwSatisfaction satisfaction = satisfactionService.selectSatisfactionByWorkOrderId(workOrderId);
        detail.setSatisfaction(satisfaction);

        return success(detail);
    }
}
