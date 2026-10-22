package com.ruoyi.web.controller.zw;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zw.domain.ZwRepairApply;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.mapper.ZwRepairApplyMapper;
import com.ruoyi.zw.service.IZwSatisfactionService;
import com.ruoyi.zw.service.IZwWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 客户满意度评价Controller（外部访问）
 * 无权限注解，允许外部访问
 */
@RestController
@RequestMapping("/zw/satisfaction")
public class ZwSatisfactionController extends BaseController {

    @Autowired
    private IZwSatisfactionService satisfactionService;

    @Autowired
    private IZwWorkOrderService workOrderService;

    @Autowired
    private ZwRepairApplyMapper repairApplyMapper;

    /**
     * 客户提交满意度评价（无需权限）
     */
    @PostMapping
    public AjaxResult add(@RequestBody ZwSatisfaction satisfaction) {
        Long workOrderId = satisfaction.getWorkOrderId();
        if (workOrderId == null) {
            return error("工单ID不能为空");
        }

        // 先更新工单状态为已完成（4）
        var order = workOrderService.selectZwWorkOrderById(workOrderId);
        if (order == null) {
            return error("工单不存在");
        }
        if (!"4".equals(order.getStatus())) {
            order.setStatus("4");
            order.setCompleteTime(new Date());
            workOrderService.updateZwWorkOrder(order);
        }

        // 同时更新报修申请状态为已评价（4）
        if (order.getWorkOrderId() != null) {
            try {
                repairApplyMapper.updateStatusByWorkOrderId(workOrderId, "4");
            } catch (Exception e) {
                // 找不到关联的报修申请无所谓，继续
            }
        }

        // 防止重复评价
        ZwSatisfaction existing = satisfactionService.selectSatisfactionByWorkOrderId(workOrderId);
        if (existing != null) {
            return error("该工单已评价过，请勿重复提交");
        }

        // 自动补充 customerId
        if (satisfaction.getCustomerId() == null) {
            satisfaction.setCustomerId(order.getCustomerId());
        }

        return toAjax(satisfactionService.insertZwSatisfaction(satisfaction));
    }

    /**
     * 查询满意度评价列表（管理端，有权限）
     */
    @GetMapping("/list")
    public TableDataInfo list(ZwSatisfaction satisfaction) {
        startPage();
        List<ZwSatisfaction> list = satisfactionService.selectZwSatisfactionList(satisfaction);
        return getDataTable(list);
    }

    /**
     * 获取满意度评价详情（管理端，有权限）
     */
    @GetMapping("/{evalId}")
    public AjaxResult getInfo(@PathVariable Long evalId) {
        return success(satisfactionService.selectZwSatisfactionById(evalId));
    }

    /**
     * 删除满意度评价（管理端，有权限）
     */
    @DeleteMapping("/{evalIds}")
    public AjaxResult remove(@PathVariable Long[] evalIds) {
        return toAjax(satisfactionService.deleteZwSatisfactionByIds(evalIds));
    }
}