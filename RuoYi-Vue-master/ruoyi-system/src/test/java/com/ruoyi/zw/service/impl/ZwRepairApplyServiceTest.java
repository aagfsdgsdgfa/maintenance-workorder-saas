package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwRepairApply;
import com.ruoyi.zw.domain.ZwWorkOrder;
import com.ruoyi.zw.mapper.ZwRepairApplyMapper;
import com.ruoyi.zw.mapper.ZwWorkOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 报修申请 Service 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ZwRepairApplyServiceTest {

    @Mock
    private ZwRepairApplyMapper repairApplyMapper;

    @Mock
    private ZwWorkOrderMapper workOrderMapper;

    @InjectMocks
    private ZwRepairApplyServiceImpl repairApplyService;

    private ZwRepairApply pendingApply;   // status = "0" 待受理
    private ZwRepairApply acceptedApply;  // status = "1" 已受理
    private ZwRepairApply processingApply; // status = "2" 处理中

    @BeforeEach
    void setUp() {
        pendingApply = new ZwRepairApply();
        pendingApply.setApplyId(1L);
        pendingApply.setApplyTitle("服务器网络故障");
        pendingApply.setCustomerId(100L);
        pendingApply.setAssetId(10L);
        pendingApply.setFaultDesc("服务器网络不通");
        pendingApply.setContactName("张三");
        pendingApply.setContactPhone("13800138000");
        pendingApply.setStatus("0");

        acceptedApply = new ZwRepairApply();
        acceptedApply.setApplyId(2L);
        acceptedApply.setApplyTitle("打印机故障");
        acceptedApply.setCustomerId(101L);
        acceptedApply.setAssetId(11L);
        acceptedApply.setStatus("1");

        processingApply = new ZwRepairApply();
        processingApply.setApplyId(3L);
        processingApply.setApplyTitle("已转工单");
        processingApply.setCustomerId(102L);
        processingApply.setAssetId(12L);
        processingApply.setStatus("2");
    }

    // ==================== selectZwRepairApplyList ====================

    @Test
    @DisplayName("查询报修申请列表 - 正常返回")
    void selectZwRepairApplyList_shouldReturnList() {
        when(repairApplyMapper.selectZwRepairApplyList(any())).thenReturn(Arrays.asList(pendingApply, acceptedApply));

        List<ZwRepairApply> result = repairApplyService.selectZwRepairApplyList(new ZwRepairApply());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repairApplyMapper, times(1)).selectZwRepairApplyList(any());
    }

    @Test
    @DisplayName("查询报修申请列表 - 空列表")
    void selectZwRepairApplyList_emptyList() {
        when(repairApplyMapper.selectZwRepairApplyList(any())).thenReturn(Arrays.asList());

        List<ZwRepairApply> result = repairApplyService.selectZwRepairApplyList(new ZwRepairApply());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询报修申请列表 - 按客户ID筛选")
    void selectZwRepairApplyList_filterByCustomerId() {
        ZwRepairApply query = new ZwRepairApply();
        query.setCustomerId(100L);
        when(repairApplyMapper.selectZwRepairApplyList(query)).thenReturn(Arrays.asList(pendingApply));

        List<ZwRepairApply> result = repairApplyService.selectZwRepairApplyList(query);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getCustomerId());
    }

    // ==================== selectZwRepairApplyById ====================

    @Test
    @DisplayName("查询报修申请详情 - 正常返回")
    void selectZwRepairApplyById_shouldReturnApply() {
        when(repairApplyMapper.selectZwRepairApplyById(1L)).thenReturn(pendingApply);

        ZwRepairApply result = repairApplyService.selectZwRepairApplyById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getApplyId());
        assertEquals("服务器网络故障", result.getApplyTitle());
        assertEquals("0", result.getStatus());
    }

    @Test
    @DisplayName("查询报修申请详情 - 不存在返回null")
    void selectZwRepairApplyById_notFound() {
        when(repairApplyMapper.selectZwRepairApplyById(999L)).thenReturn(null);

        ZwRepairApply result = repairApplyService.selectZwRepairApplyById(999L);

        assertNull(result);
    }

    // ==================== insertZwRepairApply ====================

    @Test
    @DisplayName("新增报修申请 - 设置默认状态为待受理")
    void insertZwRepairApply_shouldSetDefaultStatusToPending() {
        ZwRepairApply newApply = new ZwRepairApply();
        newApply.setApplyTitle("新报修");
        newApply.setCustomerId(200L);
        when(repairApplyMapper.insertZwRepairApply(any())).thenReturn(1);

        int result = repairApplyService.insertZwRepairApply(newApply);

        assertEquals(1, result);
        // 验证 delFlag 和 status 被正确设置
        assertEquals("0", newApply.getDelFlag());
        assertEquals("0", newApply.getStatus());
    }

    @Test
    @DisplayName("新增报修申请 - 返回影响行数")
    void insertZwRepairApply_shouldReturnRowsAffected() {
        when(repairApplyMapper.insertZwRepairApply(any())).thenReturn(1);

        int result = repairApplyService.insertZwRepairApply(pendingApply);

        assertEquals(1, result);
    }

    // ==================== updateZwRepairApply ====================

    @Test
    @DisplayName("修改报修申请 - 正常更新")
    void updateZwRepairApply_shouldReturnRowsAffected() {
        when(repairApplyMapper.updateZwRepairApply(any())).thenReturn(1);

        int result = repairApplyService.updateZwRepairApply(pendingApply);

        assertEquals(1, result);
    }

    // ==================== deleteZwRepairApplyByIds ====================

    @Test
    @DisplayName("删除报修申请 - 逻辑删除")
    void deleteZwRepairApplyByIds_shouldReturnCount() {
        Long[] ids = {1L, 2L, 3L};
        when(repairApplyMapper.deleteZwRepairApplyByIds(ids)).thenReturn(3);

        int result = repairApplyService.deleteZwRepairApplyByIds(ids);

        assertEquals(3, result);
    }

    @Test
    @DisplayName("删除报修申请 - 单个删除")
    void deleteZwRepairApplyByIds_single() {
        Long[] ids = {1L};
        when(repairApplyMapper.deleteZwRepairApplyByIds(ids)).thenReturn(1);

        int result = repairApplyService.deleteZwRepairApplyByIds(ids);

        assertEquals(1, result);
    }

    // ==================== acceptRepairApply ====================

    @Test
    @DisplayName("受理报修申请 - 待受理状态成功")
    void acceptRepairApply_whenPending_shouldUpdateToAccepted() {
        when(repairApplyMapper.selectZwRepairApplyById(1L)).thenReturn(pendingApply);
        when(repairApplyMapper.updateStatusById(1L, "1")).thenReturn(1);

        int result = repairApplyService.acceptRepairApply(1L);

        assertEquals(1, result);
        verify(repairApplyMapper).updateStatusById(1L, "1");
    }

    @Test
    @DisplayName("受理报修申请 - 已受理状态抛异常")
    void acceptRepairApply_whenAlreadyAccepted_shouldThrow() {
        when(repairApplyMapper.selectZwRepairApplyById(2L)).thenReturn(acceptedApply);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repairApplyService.acceptRepairApply(2L));
        assertEquals("当前状态不允许受理", ex.getMessage());
    }

    @Test
    @DisplayName("受理报修申请 - 处理中状态抛异常")
    void acceptRepairApply_whenProcessing_shouldThrow() {
        when(repairApplyMapper.selectZwRepairApplyById(3L)).thenReturn(processingApply);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repairApplyService.acceptRepairApply(3L));
        assertEquals("当前状态不允许受理", ex.getMessage());
    }

    @Test
    @DisplayName("受理报修申请 - ID不存在抛异常")
    void acceptRepairApply_whenNotFound_shouldThrow() {
        when(repairApplyMapper.selectZwRepairApplyById(999L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repairApplyService.acceptRepairApply(999L));
        assertEquals("报修申请不存在", ex.getMessage());
    }

    // ==================== transferToWorkOrder ====================

    @Test
    @DisplayName("转工单 - 已受理状态成功创建工单")
    void transferToWorkOrder_whenAccepted_shouldCreateWorkOrderAndUpdateApply() {
        when(repairApplyMapper.selectZwRepairApplyById(2L)).thenReturn(acceptedApply);
        when(workOrderMapper.insertZwWorkOrder(any())).thenAnswer(invocation -> {
            ZwWorkOrder wo = invocation.getArgument(0);
            wo.setWorkOrderId(100L);
            return 1;
        });
        when(repairApplyMapper.updateStatusById(2L, "2")).thenReturn(1);
        when(repairApplyMapper.updateZwRepairApply(any())).thenReturn(1);

        int result = repairApplyService.transferToWorkOrder(2L, 5L, "3");

        assertEquals(1, result);
        verify(workOrderMapper).insertZwWorkOrder(any(ZwWorkOrder.class));
        verify(repairApplyMapper).updateStatusById(2L, "2");
        verify(repairApplyMapper).updateZwRepairApply(any(ZwRepairApply.class));
    }

    @Test
    @DisplayName("转工单 - 默认优先级为2（中）")
    void transferToWorkOrder_defaultPriority_shouldSetTo2() {
        when(repairApplyMapper.selectZwRepairApplyById(2L)).thenReturn(acceptedApply);
        when(workOrderMapper.insertZwWorkOrder(any())).thenAnswer(invocation -> {
            ZwWorkOrder wo = invocation.getArgument(0);
            assertEquals("2", wo.getPriority()); // 默认中优先级
            wo.setWorkOrderId(100L);
            return 1;
        });
        when(repairApplyMapper.updateStatusById(2L, "2")).thenReturn(1);
        when(repairApplyMapper.updateZwRepairApply(any())).thenReturn(1);

        repairApplyService.transferToWorkOrder(2L, null, null);
    }

    @Test
    @DisplayName("转工单 - 待受理状态不允许转工单")
    void transferToWorkOrder_whenPending_shouldThrow() {
        when(repairApplyMapper.selectZwRepairApplyById(1L)).thenReturn(pendingApply);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> repairApplyService.transferToWorkOrder(1L, 5L, "3"));
        assertEquals("当前状态不允许转工单，请先受理", ex.getMessage());
    }

    @Test
    @DisplayName("转工单 - ID不存在抛异常")
    void transferToWorkOrder_whenNotFound_shouldThrow() {
        when(repairApplyMapper.selectZwRepairApplyById(999L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> repairApplyService.transferToWorkOrder(999L, null, null));
        assertEquals("报修申请不存在", ex.getMessage());
    }

    @Test
    @DisplayName("转工单 - SLA截止时间默认24小时后")
    void transferToWorkOrder_shouldSetSlaDeadline() {
        long beforeCall = System.currentTimeMillis();
        when(repairApplyMapper.selectZwRepairApplyById(2L)).thenReturn(acceptedApply);
        when(workOrderMapper.insertZwWorkOrder(any())).thenAnswer(invocation -> {
            ZwWorkOrder wo = invocation.getArgument(0);
            long diff = wo.getSlaDeadline().getTime() - beforeCall;
            // SLA应该在23-25小时之后
            assertTrue(diff >= 23 * 60 * 60 * 1000L, "SLA截止时间应在23小时之后");
            assertTrue(diff <= 25 * 60 * 60 * 1000L, "SLA截止时间应在25小时之内");
            wo.setWorkOrderId(100L);
            return 1;
        });
        when(repairApplyMapper.updateStatusById(2L, "2")).thenReturn(1);
        when(repairApplyMapper.updateZwRepairApply(any())).thenReturn(1);

        repairApplyService.transferToWorkOrder(2L, null, null);
    }
}
