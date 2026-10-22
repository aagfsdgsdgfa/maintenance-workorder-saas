package com.ruoyi.zw.service.impl;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.domain.ZwCheckIn;
import com.ruoyi.zw.domain.ZwSatisfaction;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import com.ruoyi.zw.mapper.ZwCheckInMapper;
import com.ruoyi.zw.mapper.ZwSatisfactionMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 工单列表 SQL 端分页测试
 *
 * 验证：
 * 1. 修复前：内存 subList 分页，全表拉取
 * 2. 修复后：PageHelper.startPage + SQL 端 LIMIT，query 包含日期过滤参数
 */
@ExtendWith(MockitoExtension.class)
class WorkOrderListPagingTest {

    @Mock private ZwWorkOrderMapper workOrderMapper;
    @Mock private ZwAssetMapper assetMapper;
    @Mock private ZwCheckInMapper checkInMapper;
    @Mock private ZwSatisfactionMapper satisfactionMapper;
    @Mock private SysUserMapper sysUserMapper;

    @InjectMocks
    private ZwCustomerWorkOrderServiceImpl workOrderService;

    private ZwWorkOrder wo1;
    private ZwWorkOrder wo2;

    @BeforeEach
    void setUp() {
        wo1 = new ZwWorkOrder();
        wo1.setWorkOrderId(1001L);
        wo1.setOrderNo("WO-2026-001");
        wo1.setCustomerId(100L);
        wo1.setOrderType("2");
        wo1.setTitle("服务器故障");
        wo1.setDescription("CPU 温度异常");
        wo1.setPriority("3");
        wo1.setStatus("3");
        wo1.setAssetId(50L);
        wo1.setAssignedTo(200L);
        wo1.setCreateTime(new Date());
        wo1.setCompleteTime(new Date());
        // selectZwWorkOrderVo 已 LEFT JOIN zw_asset，assetName 由 SQL 直接返回
        wo1.setAssetName("Dell PowerEdge R740");

        wo2 = new ZwWorkOrder();
        wo2.setWorkOrderId(1002L);
        wo2.setOrderNo("WO-2026-002");
        wo2.setCustomerId(100L);
        wo2.setOrderType("2");
        wo2.setStatus("1");
        wo2.setPriority("3");
        wo2.setAssetId(51L);
        wo2.setAssetName("HP ProLiant DL380");
    }

    @Test
    @DisplayName("分页查询 - query 包含日期过滤参数（不再内存过滤）")
    void selectWorkOrderList_queryIncludesDateFilter() {
        // Page 模拟 PageHelper 返回的分页结果
        Page<ZwWorkOrder> page = new Page<>(1, 10);
        page.add(wo1);
        page.add(wo2);
        page.setTotal(2);
        when(workOrderMapper.selectZwWorkOrderList(any(ZwWorkOrder.class))).thenReturn(page);

        Map<String, Object> result = workOrderService.selectWorkOrderList(
                100L, "3", "2", "2026-07-01", "2026-07-31", 1, 10);

        // 验证 query 参数正确传给 mapper
        ArgumentCaptor<ZwWorkOrder> queryCaptor = ArgumentCaptor.forClass(ZwWorkOrder.class);
        verify(workOrderMapper, times(1)).selectZwWorkOrderList(queryCaptor.capture());
        ZwWorkOrder captured = queryCaptor.getValue();
        assertEquals(100L, captured.getCustomerId());
        assertEquals("3", captured.getStatus());
        assertEquals("2", captured.getOrderType());
        assertEquals("2026-07-01", captured.getCreateTimeStart(),
                "startDate 必须下推到 SQL 端 query，不能在内存过滤");
        assertEquals("2026-07-31", captured.getCreateTimeEnd(),
                "endDate 必须下推到 SQL 端 query，不能在内存过滤");

        // 验证返回的 total 来自 SQL 端（PageInfo.getTotal），不是内存 subList
        assertEquals(2L, result.get("total"));
        assertEquals(1, result.get("pageNum"));
        assertEquals(10, result.get("pageSize"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals(2, rows.size());
    }

    @Test
    @DisplayName("分页查询 - 5000 行大数据量（修复前 OOM 风险，修复后 SQL LIMIT）")
    void selectWorkOrderList_largeDataset() {
        Page<ZwWorkOrder> page = new Page<>(1, 10);
        page.setTotal(5000); // 模拟 5000 行总数
        // 只放 10 条到 page（SQL LIMIT 后的结果）
        for (int i = 0; i < 10; i++) {
            ZwWorkOrder wo = new ZwWorkOrder();
            wo.setWorkOrderId((long) (2000 + i));
            wo.setOrderNo("WO-2026-" + String.format("%04d", i));
            wo.setOrderType("2");
            wo.setStatus("3");
            wo.setPriority("2");
            page.add(wo);
        }
        when(workOrderMapper.selectZwWorkOrderList(any(ZwWorkOrder.class))).thenReturn(page);

        Map<String, Object> result = workOrderService.selectWorkOrderList(
                100L, null, null, null, null, 1, 10);

        // total 来自 SQL 端 COUNT(*)，rows 只包含当前页 10 条
        assertEquals(5000L, result.get("total"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals(10, rows.size(), "rows 只能是当前页，不应包含全表 5000 条");
    }

    @Test
    @DisplayName("分页查询 - assetName 来自 SQL JOIN（不再 N+1 查 assetMapper）")
    void selectWorkOrderList_assetNameFromSqlJoin() {
        Page<ZwWorkOrder> page = new Page<>(1, 10);
        page.add(wo1);
        page.setTotal(1);
        when(workOrderMapper.selectZwWorkOrderList(any(ZwWorkOrder.class))).thenReturn(page);

        Map<String, Object> result = workOrderService.selectWorkOrderList(
                100L, null, null, null, null, 1, 10);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals("Dell PowerEdge R740", rows.get(0).get("assetName"),
                "assetName 应来自 SQL 端 JOIN 字段");

        // 关键：不应再调用 assetMapper.selectZwAssetById 产生 N+1
        verify(assetMapper, times(0)).selectZwAssetById(any());
    }

    @Test
    @DisplayName("分页查询 - 工程师信息按 userId 缓存（避免 N+1）")
    void selectWorkOrderList_engineerCacheByUserId() {
        Page<ZwWorkOrder> page = new Page<>(1, 10);
        ZwWorkOrder a = new ZwWorkOrder();
        a.setWorkOrderId(3001L);
        a.setOrderNo("WO-A");
        a.setOrderType("2");
        a.setStatus("1");
        a.setPriority("2");
        a.setAssignedTo(999L);
        ZwWorkOrder b = new ZwWorkOrder();
        b.setWorkOrderId(3002L);
        b.setOrderNo("WO-B");
        b.setOrderType("2");
        b.setStatus("1");
        b.setPriority("2");
        b.setAssignedTo(999L); // 同一工程师
        page.add(a);
        page.add(b);
        page.setTotal(2);
        when(workOrderMapper.selectZwWorkOrderList(any(ZwWorkOrder.class))).thenReturn(page);

        SysUser engineer = new SysUser();
        engineer.setUserId(999L);
        engineer.setNickName("王工");
        engineer.setPhonenumber("13800138000");
        when(sysUserMapper.selectUserById(999L)).thenReturn(engineer);

        Map<String, Object> result = workOrderService.selectWorkOrderList(
                100L, null, null, null, null, 1, 10);

        // 同一 userId 999 只查一次数据库（cache 命中）
        verify(sysUserMapper, times(1)).selectUserById(999L);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals("王工", rows.get(0).get("engineerName"));
        assertEquals("王工", rows.get(1).get("engineerName"));
    }
}
