package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwNotification;
import java.util.List;

public interface IZwNotificationService {
    List<ZwNotification> selectZwNotificationList(ZwNotification notification);
    ZwNotification selectZwNotificationById(Long notifId, Long customerId, Long userId);
    int insertZwNotification(ZwNotification notification);
    int markAsRead(Long notifId, Long customerId, Long userId);
    int markAllAsRead(Long customerId, Long userId);
    int deleteZwNotificationByIds(Long[] notifIds);
    long countUnread(Long customerId, Long userId);
    int sendNotification(Long receiverId, Long customerId, String title, String content, String type, Long relatedId);
}
