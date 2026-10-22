package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwAsset;
import com.ruoyi.zw.mapper.ZwAssetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 资产 Service 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ZwAssetServiceTest {

    @Mock
    private ZwAssetMapper assetMapper;

    @InjectMocks
    private ZwAssetServiceImpl assetService;

    private ZwAsset normalAsset;    // status = "0" 正常
    private ZwAsset faultAsset;    // status = "1" 故障
    private ZwAsset repairingAsset; // status = "2" 维修中

    @BeforeEach
    void setUp() {
        normalAsset = new ZwAsset();
        normalAsset.setAssetId(1L);
        normalAsset.setAssetCode("SN-DELL-001");
        normalAsset.setAssetName("Dell PowerEdge R740 服务器");
        normalAsset.setAssetType("device");
        normalAsset.setBrand("Dell");
        normalAsset.setModel("PowerEdge R740");
        normalAsset.setCustomerId(100L);
        normalAsset.setPurchaseDate(new java.util.Date());
        normalAsset.setWarrantyEndDate(new java.util.Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000));
        normalAsset.setLocation("机房A-机柜12");
        normalAsset.setQrCode("QR-DELL-001");
        normalAsset.setStatus("0");
        normalAsset.setHealthScore(95);

        faultAsset = new ZwAsset();
        faultAsset.setAssetId(2L);
        faultAsset.setAssetCode("SN-HP-002");
        faultAsset.setAssetName("HP打印机故障");
        faultAsset.setStatus("1");
        faultAsset.setHealthScore(30);

        repairingAsset = new ZwAsset();
        repairingAsset.setAssetId(3L);
        repairingAsset.setAssetCode("SN-LEN-003");
        repairingAsset.setAssetName("联想电脑维修中");
        repairingAsset.setStatus("2");
        repairingAsset.setHealthScore(60);
    }

    // ==================== selectZwAssetList ====================

    @Test
    @DisplayName("查询资产列表 - 正常返回")
    void selectZwAssetList_shouldReturnList() {
        when(assetMapper.selectZwAssetList(any())).thenReturn(Arrays.asList(normalAsset, faultAsset, repairingAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(new ZwAsset());

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("查询资产列表 - 空列表")
    void selectZwAssetList_emptyList() {
        when(assetMapper.selectZwAssetList(any())).thenReturn(Collections.emptyList());

        List<ZwAsset> result = assetService.selectZwAssetList(new ZwAsset());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询资产列表 - 按资产名称模糊查询")
    void selectZwAssetList_filterByName() {
        ZwAsset query = new ZwAsset();
        query.setAssetName("Dell");
        when(assetMapper.selectZwAssetList(query)).thenReturn(Arrays.asList(normalAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(query);

        assertEquals(1, result.size());
        assertEquals("Dell PowerEdge R740 服务器", result.get(0).getAssetName());
    }

    @Test
    @DisplayName("查询资产列表 - 按客户ID筛选")
    void selectZwAssetList_filterByCustomerId() {
        ZwAsset query = new ZwAsset();
        query.setCustomerId(100L);
        when(assetMapper.selectZwAssetList(query)).thenReturn(Arrays.asList(normalAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(query);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getCustomerId());
    }

    @Test
    @DisplayName("查询资产列表 - 按状态筛选")
    void selectZwAssetList_filterByStatus() {
        ZwAsset query = new ZwAsset();
        query.setStatus("1");
        when(assetMapper.selectZwAssetList(query)).thenReturn(Arrays.asList(faultAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(query);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getStatus());
    }

    // ==================== selectZwAssetById ====================

    @Test
    @DisplayName("查询资产详情 - 正常返回")
    void selectZwAssetById_shouldReturnAsset() {
        when(assetMapper.selectZwAssetById(1L)).thenReturn(normalAsset);

        ZwAsset result = assetService.selectZwAssetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAssetId());
        assertEquals("SN-DELL-001", result.getAssetCode());
        assertEquals(95, result.getHealthScore());
    }

    @Test
    @DisplayName("查询资产详情 - 不存在返回null")
    void selectZwAssetById_notFound() {
        when(assetMapper.selectZwAssetById(999L)).thenReturn(null);

        ZwAsset result = assetService.selectZwAssetById(999L);

        assertNull(result);
    }

    // ==================== selectZwAssetByQrCode ====================

    @Test
    @DisplayName("按二维码查询资产 - 扫码场景")
    void selectZwAssetByQrCode_shouldReturnAsset() {
        when(assetMapper.selectZwAssetByQrCode("QR-DELL-001")).thenReturn(normalAsset);

        ZwAsset result = assetService.selectZwAssetByQrCode("QR-DELL-001");

        assertNotNull(result);
        assertEquals("QR-DELL-001", result.getQrCode());
    }

    // ==================== insertZwAsset ====================

    @Test
    @DisplayName("新增资产 - 设置delFlag=0")
    void insertZwAsset_shouldSetDelFlag() {
        ZwAsset newAsset = new ZwAsset();
        newAsset.setAssetCode("SN-NEW-001");
        newAsset.setAssetName("新资产");
        when(assetMapper.insertZwAsset(any())).thenReturn(1);

        int result = assetService.insertZwAsset(newAsset);

        assertEquals(1, result);
        assertEquals("0", newAsset.getDelFlag());
    }

    @Test
    @DisplayName("新增资产 - 返回影响行数")
    void insertZwAsset_shouldReturnRowsAffected() {
        when(assetMapper.insertZwAsset(any())).thenReturn(1);

        int result = assetService.insertZwAsset(normalAsset);

        assertEquals(1, result);
        verify(assetMapper, times(1)).insertZwAsset(any());
    }

    // ==================== updateZwAsset ====================

    @Test
    @DisplayName("修改资产 - 正常更新")
    void updateZwAsset_shouldReturnRowsAffected() {
        when(assetMapper.updateZwAsset(any())).thenReturn(1);

        int result = assetService.updateZwAsset(normalAsset);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("修改资产 - 资产不存在")
    void updateZwAsset_notFound() {
        when(assetMapper.updateZwAsset(any())).thenReturn(0);

        int result = assetService.updateZwAsset(normalAsset);

        assertEquals(0, result);
    }

    // ==================== deleteZwAssetByIds ====================

    @Test
    @DisplayName("删除资产 - 逻辑删除")
    void deleteZwAssetByIds_shouldReturnCount() {
        Long[] ids = {1L, 2L, 3L};
        when(assetMapper.deleteZwAssetByIds(ids)).thenReturn(3);

        int result = assetService.deleteZwAssetByIds(ids);

        assertEquals(3, result);
    }

    @Test
    @DisplayName("删除资产 - 单个删除")
    void deleteZwAssetByIds_single() {
        Long[] ids = {1L};
        when(assetMapper.deleteZwAssetByIds(ids)).thenReturn(1);

        int result = assetService.deleteZwAssetByIds(ids);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("删除资产 - 批量删除包含不存在的ID")
    void deleteZwAssetByIds_partialMatch() {
        Long[] ids = {1L, 999L};
        when(assetMapper.deleteZwAssetByIds(ids)).thenReturn(1); // 只删除了1条

        int result = assetService.deleteZwAssetByIds(ids);

        assertEquals(1, result);
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("资产名称最大长度200字符")
    void selectZwAssetList_maxLengthName() {
        String maxName = "A".repeat(200);
        normalAsset.setAssetName(maxName);
        when(assetMapper.selectZwAssetList(any())).thenReturn(Arrays.asList(normalAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(new ZwAsset());

        assertEquals(200, result.get(0).getAssetName().length());
    }

    @Test
    @DisplayName("健康度评分范围0-100")
    void healthScoreRange() {
        normalAsset.setHealthScore(100);
        when(assetMapper.selectZwAssetById(1L)).thenReturn(normalAsset);

        ZwAsset result = assetService.selectZwAssetById(1L);

        assertTrue(result.getHealthScore() >= 0 && result.getHealthScore() <= 100);
    }

    @Test
    @DisplayName("资产类型枚举值")
    void assetTypeValues() {
        normalAsset.setAssetType("device");
        faultAsset.setAssetType("software");
        repairingAsset.setAssetType("consumable");
        when(assetMapper.selectZwAssetList(any())).thenReturn(Arrays.asList(normalAsset, faultAsset, repairingAsset));

        List<ZwAsset> result = assetService.selectZwAssetList(new ZwAsset());

        assertEquals("device", result.get(0).getAssetType());
        assertEquals("software", result.get(1).getAssetType());
        assertEquals("consumable", result.get(2).getAssetType());
    }

    @Test
    @DisplayName("状态流转验证")
    void assetStatusTransition() {
        // 验证状态值正确：0正常 -> 1故障 -> 2维修中 -> 3已报废
        assertEquals("0", normalAsset.getStatus());
        assertEquals("1", faultAsset.getStatus());
        assertEquals("2", repairingAsset.getStatus());
    }

    @Test
    @DisplayName("第三方代保标志")
    void thirdPartyFlag() {
        normalAsset.setIsThirdParty("0");  // 自售
        when(assetMapper.selectZwAssetById(1L)).thenReturn(normalAsset);

        ZwAsset result = assetService.selectZwAssetById(1L);

        assertEquals("0", result.getIsThirdParty());
    }
}
