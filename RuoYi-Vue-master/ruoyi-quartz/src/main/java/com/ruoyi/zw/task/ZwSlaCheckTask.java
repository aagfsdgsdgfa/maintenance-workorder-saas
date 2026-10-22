package com.ruoyi.zw.task;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.zw.domain.ZwNotification;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import com.ruoyi.zw.mapper.ZwNotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SLA超时预警定时任务
 * 每10分钟执行一次，检查即将超时的工单并发送预警通知
 *
 * @author ruoyi
 */
@Component("zwSlaCheckTask")
public class ZwSlaCheckTask {

    private static final Logger log = LoggerFactory.getLogger(ZwSlaCheckTask.class);

    /** SLA提前2小时预警（毫秒） */
    private static final long SLA_WARNING_LEAD_TIME_MS = 2 * 60 * 60 * 1000L;

    /** 每批处理数量，防止大结果集 OOM */
    private static final int BATCH_SIZE = 200;

    /**
     * 检查SLA即将超时的工单并发送预警（分批处理）
     */
    public void checkSlaWarning() {
        log.info("SLA超时检查任务开始执行");
        try {
            ZwWorkOrderMapper workOrderMapper = SpringUtils.getBean(ZwWorkOrderMapper.class);
            ZwNotificationMapper notificationMapper = SpringUtils.getBean(ZwNotificationMapper.class);

            Date now = new Date();
            int totalSent = 0;

            // 分批查询，防止大数据量 OOM
            List<ZwWorkOrder> pendingOrders;
            do {
                pendingOrders = workOrderMapper.selectSlaPendingOrders();
                for (ZwWorkOrder wo : pendingOrders) {
                    if (wo.getSlaDeadline() == null || wo.getAssignedTo() == null) {
                        continue; // 未派单或无截止时间不预警
                    }
                    long remaining = wo.getSlaDeadline().getTime() - now.getTime();
                    if (remaining >= SLA_WARNING_LEAD_TIME_MS) {
                        continue; // 剩余时间充足，无需预警
                    }

                    // 发送通知
                    ZwNotification notification = new ZwNotification();
                    notification.setUserId(wo.getAssignedTo());
                    notification.setTitle("工单即将超时");
                    notification.setContent(buildNotificationContent(wo, remaining));
                    notification.setType("sla_warning");
                    notification.setRelatedId(wo.getWorkOrderId());
                    notification.setIsRead("N");
                    notification.setDelFlag("0");
                    notification.setCreateTime(new Date());
                    notificationMapper.insertZwNotification(notification);

                    // 标记已发送
                    ZwWorkOrder update = new ZwWorkOrder();
                    update.setWorkOrderId(wo.getWorkOrderId());
                    update.setSlaWarningSent("Y");
                    workOrderMapper.updateZwWorkOrder(update);

                    totalSent++;
                    log.info("工单 {} SLA预警已发送", wo.getOrderNo());
                }
            } while (pendingOrders.size() == BATCH_SIZE);

            log.info("SLA超时检查任务执行完成，共发送 {} 条预警", totalSent);
        } catch (Exception e) {
            log.error("SLA检查任务执行异常", e);
            // 不再静默吞掉异常，便于运维告警
        }
    }

    private String buildNotificationContent(ZwWorkOrder wo, long remaining) {
        String timeDesc;
        if (remaining < 0) {
            long overdueMinutes = Math.abs(remaining) / 60000;
            timeDesc = "已超时" + overdueMinutes + "分钟";
        } else {
            long remainingMinutes = remaining / 60000;
            timeDesc = "剩余约" + remainingMinutes + "分钟";
        }
        return "工单 " + wo.getOrderNo() + " SLA即将超时，请尽快处理！" + timeDesc;
    }
}
