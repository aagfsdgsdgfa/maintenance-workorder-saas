package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwNotification;
import com.ruoyi.zw.service.IZwNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知Controller（支持客户按公司隔离 + 工程师按用户隔离）
 */
@RestController
@RequestMapping("/zw/notification")
public class ZwNotificationController extends BaseController {

    @Autowired
    private IZwNotificationService notificationService;

    /**
     * 查询通知列表（客户按customerId，工程师按userId）
     */
    @GetMapping("/list")
    public TableDataInfo list(ZwNotification notification) {
        startPage();
        Long customerId = SecurityUtils.getCustomerId();
        Long userId = SecurityUtils.getUserId();
        if (customerId != null) {
            notification.setCustomerId(customerId);
        } else if (userId != null) {
            notification.setUserId(userId);
        }
        List<ZwNotification> list = notificationService.selectZwNotificationList(notification);
        return getDataTable(list);
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{notifId}")
    public AjaxResult getInfo(@PathVariable Long notifId) {
        Long customerId = SecurityUtils.getCustomerId();
        Long userId = SecurityUtils.getUserId();
        if (customerId == null && userId == null) {
            return error("无权限访问");
        }
        ZwNotification notification = notificationService.selectZwNotificationById(notifId, customerId, userId);
        if (notification == null) {
            return error("通知不存在或无权限访问");
        }
        return success(notification);
    }

    /**
     * 标记通知已读
     */
    @PutMapping("/read/{notifId}")
    public AjaxResult markRead(@PathVariable Long notifId) {
        Long customerId = SecurityUtils.getCustomerId();
        Long userId = SecurityUtils.getUserId();
        if (customerId == null && userId == null) {
            return error("无权限访问");
        }
        return toAjax(notificationService.markAsRead(notifId, customerId, userId));
    }

    /**
     * 一键全部已读
     */
    @PutMapping("/markAllRead")
    public AjaxResult markAllRead() {
        Long customerId = SecurityUtils.getCustomerId();
        Long userId = SecurityUtils.getUserId();
        if (customerId == null && userId == null) {
            return error("无权限访问");
        }
        notificationService.markAllAsRead(customerId, userId);
        return success();
    }

    /**
     * 新增通知
     */
    @PostMapping
    public AjaxResult add(@RequestBody ZwNotification notification) {
        return toAjax(notificationService.insertZwNotification(notification));
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount() {
        Long customerId = SecurityUtils.getCustomerId();
        Long userId = SecurityUtils.getUserId();
        if (customerId == null && userId == null) {
            return success(0);
        }
        return success(notificationService.countUnread(customerId, userId));
    }
}
