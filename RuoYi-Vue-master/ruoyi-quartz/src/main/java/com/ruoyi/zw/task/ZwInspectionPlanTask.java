package com.ruoyi.zw.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.zw.domain.ZwInspectionPlan;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.mapper.ZwInspectionPlanMapper;
import com.ruoyi.zw.mapper.ZwInspectionTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 巡检计划自动生成任务定时任务
 * 每日凌晨1点执行，根据启用的巡检计划自动生成巡检任务
 */
@Component("zwInspectionPlanTask")
public class ZwInspectionPlanTask {

    private static final Logger log = LoggerFactory.getLogger(ZwInspectionPlanTask.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 根据巡检计划生成巡检任务
     * 按 frequency_type 判断是否应在今日生成：
     * 1=每日, 2=每周(周一), 3=每月(1号), 4=自定义(按cron)
     */
    public void generateTasks() {
        log.info("巡检计划任务生成开始");
        try {
            ZwInspectionPlanMapper planMapper = SpringUtils.getBean(ZwInspectionPlanMapper.class);
            ZwInspectionTaskMapper taskMapper = SpringUtils.getBean(ZwInspectionTaskMapper.class);

            List<ZwInspectionPlan> activePlans = planMapper.selectActivePlans();
            int totalCreated = 0;

            for (ZwInspectionPlan plan : activePlans) {
                if (!shouldRunToday(plan)) {
                    continue;
                }

                List<Long> assetIdList = parseAssetIds(plan.getAssetIds());
                if (assetIdList.isEmpty()) {
                    log.warn("巡检计划 {} 无有效资产，跳过", plan.getPlanName());
                    continue;
                }

                Date planDate = new Date();
                int created = 0;

                for (Long assetId : assetIdList) {
                    // 跳过已存在今日同计划同资产的任务（防重复）
                    if (taskMapper.existsTodayTaskByPlanAndAsset(plan.getPlanId(), assetId, planDate) > 0) {
                        continue;
                    }

                    ZwInspectionTask task = new ZwInspectionTask();
                    task.setWorkOrderId(null);
                    task.setAssetId(assetId);
                    task.setTaskType(plan.getFrequencyType());
                    task.setPlanTime(planDate);
                    task.setStatus("0");
                    task.setDelFlag("0");
                    task.setCreateTime(new Date());
                    task.setCustomerId(plan.getCustomerId());
                    taskMapper.insertZwInspectionTask(task);
                    created++;
                }

                log.info("巡检计划 [{}] 生成 {} 个任务", plan.getPlanName(), created);
                totalCreated += created;
            }

            log.info("巡检计划任务生成完成，共创建 {} 个任务", totalCreated);
        } catch (Exception e) {
            log.error("巡检计划任务生成异常", e);
        }
    }

    /**
     * 根据 frequency_type 判断今日是否应执行
     */
    private boolean shouldRunToday(ZwInspectionPlan plan) {
        String freqType = plan.getFrequencyType();
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        return switch (freqType) {
            case "1" -> true; // 每日
            case "2" -> dayOfWeek == Calendar.MONDAY; // 每周一
            case "3" -> dayOfMonth == 1; // 每月1号
            case "4" -> true; // 自定义cron，由管理员保证cron表达式正确
            default -> false;
        };
    }

    /**
     * 解析 assetIds JSON 数组字符串为 Long 列表
     */
    private List<Long> parseAssetIds(String assetIdsJson) {
        if (assetIdsJson == null || assetIdsJson.isEmpty()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(assetIdsJson, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("解析 assetIds 失败: {}", assetIdsJson);
            return List.of();
        }
    }
}
