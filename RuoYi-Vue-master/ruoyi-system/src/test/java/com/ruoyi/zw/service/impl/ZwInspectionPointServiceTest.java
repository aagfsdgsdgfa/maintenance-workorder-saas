package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwInspectionPoint;
import com.ruoyi.zw.mapper.ZwInspectionPointMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 巡检点 Service 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ZwInspectionPointServiceTest {

    @Mock
    private ZwInspectionPointMapper inspectionPointMapper;

    @InjectMocks
    private ZwInspectionPointServiceImpl inspectionPointService;

    private ZwInspectionPoint enabledPoint;   // status = "0" 启用
    private ZwInspectionPoint disabledPoint;   // status = "1" 停用

    @BeforeEach
    void setUp() {
        enabledPoint = new ZwInspectionPoint();
        enabledPoint.setPointId(1L);
        enabledPoint.setAssetId(10L);
        enabledPoint.setPointName("电源检查点");
        enabledPoint.setPointCode("PWR-001");
        enabledPoint.setPointLocation("机房A-机柜1");
        enabledPoint.setLatitude(new BigDecimal("39.908823"));
        enabledPoint.setLongitude(new BigDecimal("116.397470"));
        enabledPoint.setScanRadius(50);
        enabledPoint.setCheckItems("[{\"name\":\"电源指示灯\",\"result\":\"正常\"}]");
        enabledPoint.setStatus("0");
        enabledPoint.setDelFlag("0");

        disabledPoint = new ZwInspectionPoint();
        disabledPoint.setPointId(2L);
        disabledPoint.setAssetId(11L);
        disabledPoint.setPointName("网络检查点");
        disabledPoint.setStatus("1");
        disabledPoint.setDelFlag("0");
    }

    // ==================== selectZwInspectionPointList ====================

    @Test
    @DisplayName("查询巡检点列表 - 正常返回")
    void selectZwInspectionPointList_shouldReturnList() {
        when(inspectionPointMapper.selectZwInspectionPointList(any()))
            .thenReturn(Arrays.asList(enabledPoint, disabledPoint));

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointList(new ZwInspectionPoint());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询巡检点列表 - 按资产ID筛选")
    void selectZwInspectionPointList_filterByAssetId() {
        ZwInspectionPoint query = new ZwInspectionPoint();
        query.setAssetId(10L);
        when(inspectionPointMapper.selectZwInspectionPointList(query))
            .thenReturn(Arrays.asList(enabledPoint));

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointList(query);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getAssetId());
    }

    @Test
    @DisplayName("查询巡检点列表 - 空列表")
    void selectZwInspectionPointList_emptyList() {
        when(inspectionPointMapper.selectZwInspectionPointList(any()))
            .thenReturn(Collections.emptyList());

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointList(new ZwInspectionPoint());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== selectZwInspectionPointById ====================

    @Test
    @DisplayName("查询巡检点详情 - 正常返回")
    void selectZwInspectionPointById_shouldReturnPoint() {
        when(inspectionPointMapper.selectZwInspectionPointById(1L)).thenReturn(enabledPoint);

        ZwInspectionPoint result = inspectionPointService.selectZwInspectionPointById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPointId());
        assertEquals("电源检查点", result.getPointName());
        assertEquals("0", result.getStatus());
    }

    @Test
    @DisplayName("查询巡检点详情 - 不存在返回null")
    void selectZwInspectionPointById_notFound() {
        when(inspectionPointMapper.selectZwInspectionPointById(999L)).thenReturn(null);

        ZwInspectionPoint result = inspectionPointService.selectZwInspectionPointById(999L);

        assertNull(result);
    }

    // ==================== selectZwInspectionPointByAssetId ====================

    @Test
    @DisplayName("按资产查询巡检点 - 正常返回")
    void selectZwInspectionPointByAssetId_shouldReturnList() {
        when(inspectionPointMapper.selectZwInspectionPointByAssetId(10L))
            .thenReturn(Arrays.asList(enabledPoint));

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointByAssetId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getAssetId());
    }

    @Test
    @DisplayName("按资产查询巡检点 - 无结果返回空列表")
    void selectZwInspectionPointByAssetId_emptyList() {
        when(inspectionPointMapper.selectZwInspectionPointByAssetId(999L))
            .thenReturn(Collections.emptyList());

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointByAssetId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== selectZwInspectionPointByCode ====================

    @Test
    @DisplayName("按巡检点编码查询 - 正常返回")
    void selectZwInspectionPointByCode_shouldReturnPoint() {
        when(inspectionPointMapper.selectZwInspectionPointByCode("PWR-001"))
            .thenReturn(enabledPoint);

        ZwInspectionPoint result = inspectionPointService.selectZwInspectionPointByCode("PWR-001");

        assertNotNull(result);
        assertEquals("PWR-001", result.getPointCode());
    }

    // ==================== insertZwInspectionPoint ====================

    @Test
    @DisplayName("新增巡检点 - 设置默认status=0和delFlag=0")
    void insertZwInspectionPoint_shouldSetDefaults() {
        ZwInspectionPoint newPoint = new ZwInspectionPoint();
        newPoint.setPointName("新检查点");
        newPoint.setAssetId(20L);
        when(inspectionPointMapper.insertZwInspectionPoint(any())).thenReturn(1);

        int result = inspectionPointService.insertZwInspectionPoint(newPoint);

        assertEquals(1, result);
        assertEquals("0", newPoint.getDelFlag());
        assertEquals("0", newPoint.getStatus());
    }

    @Test
    @DisplayName("新增巡检点 - 未设置scanRadius时默认50米")
    void insertZwInspectionPoint_withoutScanRadius_shouldSetDefault50() {
        ZwInspectionPoint newPoint = new ZwInspectionPoint();
        newPoint.setPointName("无半径检查点");
        newPoint.setAssetId(20L);
        newPoint.setScanRadius(null);
        when(inspectionPointMapper.insertZwInspectionPoint(any())).thenReturn(1);

        int result = inspectionPointService.insertZwInspectionPoint(newPoint);

        assertEquals(1, result);
        assertEquals(50, newPoint.getScanRadius());
    }

    @Test
    @DisplayName("新增巡检点 - 已设置scanRadius保持不变")
    void insertZwInspectionPoint_withScanRadius_shouldKeep() {
        ZwInspectionPoint newPoint = new ZwInspectionPoint();
        newPoint.setPointName("自定义半径检查点");
        newPoint.setAssetId(20L);
        newPoint.setScanRadius(100);
        when(inspectionPointMapper.insertZwInspectionPoint(any())).thenReturn(1);

        int result = inspectionPointService.insertZwInspectionPoint(newPoint);

        assertEquals(1, result);
        assertEquals(100, newPoint.getScanRadius()); // 保持100，不被覆盖
    }

    // ==================== updateZwInspectionPoint ====================

    @Test
    @DisplayName("修改巡检点 - 正常更新")
    void updateZwInspectionPoint_shouldReturnRowsAffected() {
        when(inspectionPointMapper.updateZwInspectionPoint(any())).thenReturn(1);

        int result = inspectionPointService.updateZwInspectionPoint(enabledPoint);

        assertEquals(1, result);
    }

    // ==================== deleteZwInspectionPointByIds ====================

    @Test
    @DisplayName("删除巡检点 - 逻辑删除")
    void deleteZwInspectionPointByIds_shouldReturnCount() {
        Long[] ids = {1L, 2L, 3L};
        when(inspectionPointMapper.deleteZwInspectionPointByIds(ids)).thenReturn(3);

        int result = inspectionPointService.deleteZwInspectionPointByIds(ids);

        assertEquals(3, result);
    }

    @Test
    @DisplayName("删除巡检点 - 单个删除")
    void deleteZwInspectionPointByIds_single() {
        Long[] ids = {1L};
        when(inspectionPointMapper.deleteZwInspectionPointByIds(ids)).thenReturn(1);

        int result = inspectionPointService.deleteZwInspectionPointByIds(ids);

        assertEquals(1, result);
    }

    // ==================== toggleStatus ====================

    @Test
    @DisplayName("切换状态 - 从启用切到停用")
    void toggleStatus_fromEnableToDisable() {
        when(inspectionPointMapper.selectZwInspectionPointById(1L)).thenReturn(enabledPoint);
        doAnswer(invocation -> {
            enabledPoint.setStatus("1");
            return 1;
        }).when(inspectionPointMapper).updateStatusById(1L, "1");

        int result = inspectionPointService.toggleStatus(1L);

        assertEquals(1, result);
        assertEquals("1", enabledPoint.getStatus());
        verify(inspectionPointMapper).updateStatusById(1L, "1");
    }

    @Test
    @DisplayName("切换状态 - 从停用切到启用")
    void toggleStatus_fromDisableToEnable() {
        when(inspectionPointMapper.selectZwInspectionPointById(2L)).thenReturn(disabledPoint);
        doAnswer(invocation -> {
            disabledPoint.setStatus("0");
            return 1;
        }).when(inspectionPointMapper).updateStatusById(2L, "0");

        int result = inspectionPointService.toggleStatus(2L);

        assertEquals(1, result);
        assertEquals("0", disabledPoint.getStatus());
        verify(inspectionPointMapper).updateStatusById(2L, "0");
    }

    @Test
    @DisplayName("切换状态 - ID不存在抛异常")
    void toggleStatus_whenNotFound_shouldThrow() {
        when(inspectionPointMapper.selectZwInspectionPointById(999L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> inspectionPointService.toggleStatus(999L));
        assertEquals("巡检点不存在", ex.getMessage());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("巡检点名称最大长度边界")
    void selectZwInspectionPointList_withMaxLengthName() {
        String maxName = "A".repeat(100);
        enabledPoint.setPointName(maxName);
        when(inspectionPointMapper.selectZwInspectionPointList(any()))
            .thenReturn(Arrays.asList(enabledPoint));

        List<ZwInspectionPoint> result = inspectionPointService.selectZwInspectionPointList(new ZwInspectionPoint());

        assertEquals(100, result.get(0).getPointName().length());
    }

    @Test
    @DisplayName("GPS坐标精度保留")
    void insertZwInspectionPoint_gpsPrecision() {
        ZwInspectionPoint newPoint = new ZwInspectionPoint();
        newPoint.setPointName("高精度测试");
        newPoint.setAssetId(30L);
        newPoint.setLatitude(new BigDecimal("39.9088230"));
        newPoint.setLongitude(new BigDecimal("116.3974700"));
        when(inspectionPointMapper.insertZwInspectionPoint(any())).thenReturn(1);

        inspectionPointService.insertZwInspectionPoint(newPoint);

        assertNotNull(newPoint.getLatitude());
        assertNotNull(newPoint.getLongitude());
    }
}
