package com.ruoyi.zw.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.zw.domain.ZwInspectionTask;
import com.ruoyi.zw.domain.ZwInspectionResult;
import com.ruoyi.zw.service.IZwInspectionTaskService;
import com.ruoyi.zw.service.IZwInspectionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 客户巡检Controller
 * 无权限注解，允许外部访问
 */
@RestController
@RequestMapping("/zw/customer/inspection")
public class ZwCustomerInspectionController extends BaseController {

    @Autowired
    private IZwInspectionTaskService inspectionTaskService;

    @Autowired
    private IZwInspectionResultService inspectionResultService;

    /**
     * 查询客户巡检记录列表（分页）
     */
    @GetMapping("/list")
    public TableDataInfo list(ZwInspectionTask task,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return new TableDataInfo(java.util.Collections.emptyList(), 0L);
        }
        task.setCustomerId(customerId);
        startPage();
        List<ZwInspectionTask> list = inspectionTaskService.selectZwInspectionTaskList(task);
        return getDataTable(list);
    }

    /**
     * 获取巡检报告详情
     */
    @GetMapping("/{taskId}/report")
    public AjaxResult selectInspectionReport(@PathVariable Long taskId) {
        Long customerId = SecurityUtils.getCustomerId();
        if (customerId == null) {
            return error("用户未登录");
        }
        ZwInspectionTask task = inspectionTaskService.selectZwInspectionTaskById(taskId);
        if (task == null) {
            return error("巡检任务不存在");
        }
        if (task.getCustomerId() != null && !task.getCustomerId().equals(customerId)) {
            return error("无权查看此巡检报告");
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("taskId", task.getTaskId());
        report.put("assetId", task.getAssetId());
        report.put("planTime", task.getPlanTime());
        report.put("actualStartTime", task.getActualStartTime());
        report.put("actualEndTime", task.getActualEndTime());
        report.put("status", task.getStatus());
        report.put("checkInAddress", task.getCheckInAddress());
        report.put("remark", task.getReportUrl());

        ObjectMapper objectMapper = new ObjectMapper();
        ZwInspectionResult resultQuery = new ZwInspectionResult();
        resultQuery.setTaskId(taskId);
        List<ZwInspectionResult> results = inspectionTaskService.selectZwInspectionTaskById(taskId) != null
                ? inspectionResultService.selectZwInspectionResultList(resultQuery)
                : Collections.emptyList();

        int normalCount = 0;
        int abnormalCount = 0;
        List<Map<String, Object>> parsedResults = new ArrayList<>();
        List<String> photoUrls = new ArrayList<>();

        if (results != null) {
            for (ZwInspectionResult r : results) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("resultId", r.getResultId());
                item.put("remark", r.getRemark());
                item.put("isNormal", r.getIsNormal());
                item.put("voiceText", r.getVoiceText());

                // 解析 checkItems JSON，提取检查项明细
                // 数据库字段：item(名称), value(值), pass(true正常/false异常)
                List<Map<String, Object>> checkItemList = new ArrayList<>();
                if (r.getCheckItems() != null && !r.getCheckItems().isEmpty()) {
                    try {
                        JsonNode node = objectMapper.readTree(r.getCheckItems());
                        if (node.isArray()) {
                            for (JsonNode cn : node) {
                                Map<String, Object> ci = new LinkedHashMap<>();
                                ci.put("name", cn.has("item") ? cn.get("item").asText() : cn.has("name") ? cn.get("name").asText() : "");
                                ci.put("value", cn.has("value") ? cn.get("value").asText() : "");
                                boolean pass = cn.has("pass") && !cn.get("pass").isNull() ? cn.get("pass").asBoolean() : true;
                                ci.put("isNormal", pass ? "Y" : "N");
                                checkItemList.add(ci);
                            }
                        }
                    } catch (Exception ignored) {}
                }
                item.put("checkItems", checkItemList);

                // 统计正常/异常
                if (checkItemList.isEmpty()) {
                    if ("N".equals(r.getIsNormal())) abnormalCount++;
                    else normalCount++;
                } else {
                    for (Map<String, Object> ci : checkItemList) {
                        if ("N".equals(ci.get("isNormal"))) abnormalCount++;
                        else normalCount++;
                    }
                }

                // 收集照片（JSON数组格式）
                if (r.getPhotoUrls() != null && !r.getPhotoUrls().isEmpty()) {
                    try {
                        JsonNode photosNode = objectMapper.readTree(r.getPhotoUrls());
                        if (photosNode.isArray()) {
                            for (JsonNode p : photosNode) {
                                photoUrls.add(p.asText());
                            }
                        }
                    } catch (Exception ignored) {}
                }

                parsedResults.add(item);
            }
        }

        report.put("resultList", parsedResults);
        report.put("normalCount", normalCount);
        report.put("abnormalCount", abnormalCount);
        report.put("photoUrls", photoUrls);

        return success(report);
    }
}
