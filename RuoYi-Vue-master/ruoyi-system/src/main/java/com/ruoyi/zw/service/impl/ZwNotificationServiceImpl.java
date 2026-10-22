package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwNotification;
import com.ruoyi.zw.mapper.ZwNotificationMapper;
import com.ruoyi.zw.service.IZwNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ZwNotificationServiceImpl implements IZwNotificationService {

    @Autowired
    private ZwNotificationMapper notificationMapper;

    @Override
    public List<ZwNotification> selectZwNotificationList(ZwNotification notification) {
        return notificationMapper.selectZwNotificationList(notification);
    }

    @Override
    public ZwNotification selectZwNotificationById(Long notifId, Long customerId, Long userId) {
        ZwNotification notification = notificationMapper.selectZwNotificationById(notifId);
        if (notification == null) {
            return null;
        }
        // 客户按customerId隔离，工程师按userId隔离
        if (customerId != null && customerId.equals(notification.getCustomerId())) {
            return notification;
        }
        if (userId != null && userId.equals(notification.getUserId())) {
            return notification;
        }
        return null;
    }

    @Override
    public int insertZwNotification(ZwNotification notification) {
        notification.setDelFlag("0");
        notification.setIsRead("N");
        return notificationMapper.insertZwNotification(notification);
    }

    @Override
    public int markAsRead(Long notifId, Long customerId, Long userId) {
        return notificationMapper.updateReadStatus(notifId, customerId, userId);
    }

    @Override
    public int markAllAsRead(Long customerId, Long userId) {
        return notificationMapper.updateAllReadStatus(customerId, userId);
    }

    @Override
    public int deleteZwNotificationByIds(Long[] notifIds) {
        return notificationMapper.deleteZwNotificationByIds(notifIds);
    }

    @Override
    public long countUnread(Long customerId, Long userId) {
        return notificationMapper.countUnread(customerId, userId);
    }

    @Override
    public int sendNotification(Long receiverId, Long customerId, String title, String content, String type, Long relatedId) {
        ZwNotification notification = new ZwNotification();
        notification.setUserId(receiverId);
        notification.setCustomerId(customerId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        return insertZwNotification(notification);
    }
}
