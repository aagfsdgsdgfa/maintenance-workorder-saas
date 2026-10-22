package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwNotification;
import java.util.List;

public interface ZwNotificationMapper {
    List<ZwNotification> selectZwNotificationList(ZwNotification notification);
    ZwNotification selectZwNotificationById(Long notifId);
    int insertZwNotification(ZwNotification notification);
    int updateReadStatus(Long notifId, Long customerId, Long userId);
    int updateAllReadStatus(Long customerId, Long userId);
    int deleteZwNotificationByIds(Long[] notifIds);
    long countUnread(Long customerId, Long userId);
}
