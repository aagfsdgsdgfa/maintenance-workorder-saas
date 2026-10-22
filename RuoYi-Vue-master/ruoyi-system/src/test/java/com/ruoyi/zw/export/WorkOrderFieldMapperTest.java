package com.ruoyi.zw.export;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.zw.domain.ZwWorkOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderFieldMapperTest {

    @Mock
    private ISysUserService sysUserService;

    private WorkOrderFieldMapper fieldMapper;

    @BeforeEach
    void setUp() {
        fieldMapper = new WorkOrderFieldMapper(sysUserService);
    }

    @Test
    @DisplayName("build：完整工单→所有模板字段填充正确")
    void build_fullWorkOrder_allFieldsMapped() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setOrderNo("WO-001");
        wo.setTitle("测试工单");
        wo.setOrderType("2"); // 维修
        wo.setPriority("3");  // 高
        wo.setStatus("1");    // 处理中
        wo.setAssetName("Dell PowerEdge R740");
        wo.setSerialNumber("SN-12345");
        wo.setCustomerName("ACME Corp");
        wo.setContactName("张三");
        wo.setContactPhone("13800138000");
        wo.setContactAddress("北京市朝阳区");
        wo.setFaultDescription("无法开机");
        wo.setFaultCause("电源故障");
        wo.setVisitFee(new BigDecimal("100.00"));
        wo.setServiceFee(new BigDecimal("500.50"));
        wo.setTotalAmount(new BigDecimal("600.50"));
        wo.setChargeStatus("是");
        wo.setRemark("紧急处理");
        wo.setBrand("Dell");
        wo.setModel("R740");
        wo.setAssignedTo(42L);

        SysUser user = new SysUser();
        user.setNickName("李工程师");
        when(sysUserService.selectUserById(eq(42L))).thenReturn(user);

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("orderNo")).isEqualTo("WO-001");
        assertThat(fields.get("orderType")).isEqualTo("维修");
        assertThat(fields.get("priority")).isEqualTo("高");
        assertThat(fields.get("status")).isEqualTo("处理中");
        assertThat(fields.get("engineerName")).isEqualTo("李工程师");
        assertThat(fields.get("visitFee")).isEqualTo("¥100.00");
        assertThat(fields.get("serviceFee")).isEqualTo("¥500.50");
        assertThat(fields.get("totalAmount")).isEqualTo("¥600.50");
        assertThat(fields.get("chargeStatus")).isEqualTo("☑");
        assertThat(fields.get("customerSignUrl")).isEmpty();
        assertThat(fields.get("engineerSignUrl")).isEmpty();
    }

    @Test
    @DisplayName("build：空字段全部回退到默认值 '-' 或 ''")
    void build_emptyFields_fallbacks() {
        ZwWorkOrder wo = new ZwWorkOrder();
        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("createTime")).isEqualTo("-");
        assertThat(fields.get("completeTime")).isEqualTo("-");
        assertThat(fields.get("orderType")).isEqualTo("-");
        assertThat(fields.get("priority")).isEqualTo("-");
        assertThat(fields.get("status")).isEqualTo("-");
        assertThat(fields.get("engineerName")).isEqualTo("-");
        assertThat(fields.get("contactName")).isEqualTo("-");
        assertThat(fields.get("address")).isEqualTo("-");
        assertThat(fields.get("assetName")).isEqualTo("-");
        assertThat(fields.get("visitFee")).isEmpty();
        assertThat(fields.get("serviceFee")).isEmpty();
        assertThat(fields.get("totalAmount")).isEmpty();
        assertThat(fields.get("chargeStatus")).isEqualTo("☐");
        assertThat(fields.get("title")).isEmpty();
    }

    @Test
    @DisplayName("build：金额为0时不带 ¥ 符号返回空字符串")
    void build_zeroMoney_returnsEmpty() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setVisitFee(BigDecimal.ZERO);
        wo.setServiceFee(BigDecimal.ZERO);
        wo.setTotalAmount(BigDecimal.ZERO);

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("visitFee")).isEmpty();
        assertThat(fields.get("serviceFee")).isEmpty();
        assertThat(fields.get("totalAmount")).isEmpty();
    }

    @Test
    @DisplayName("build：金额为null时返回空字符串")
    void build_nullMoney_returnsEmpty() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setVisitFee(null);
        wo.setServiceFee(null);
        wo.setTotalAmount(null);

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("visitFee")).isEmpty();
        assertThat(fields.get("serviceFee")).isEmpty();
        assertThat(fields.get("totalAmount")).isEmpty();
    }

    @Test
    @DisplayName("build：复选框字段 chargeStatus='Y'/'是'→☑,其他→☐")
    void build_checkbox_variants() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setChargeStatus("Y");
        assertThat(fieldMapper.build(wo).get("chargeStatus")).isEqualTo("☑");

        wo.setChargeStatus("是");
        assertThat(fieldMapper.build(wo).get("chargeStatus")).isEqualTo("☑");

        wo.setChargeStatus("否");
        assertThat(fieldMapper.build(wo).get("chargeStatus")).isEqualTo("☐");

        wo.setChargeStatus(null);
        assertThat(fieldMapper.build(wo).get("chargeStatus")).isEqualTo("☐");
    }

    @Test
    @DisplayName("build：枚举值不在enum定义时保留原始code")
    void build_unknownEnumCode_kept() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setOrderType("99"); // 不在 OrderTypeEnum 内
        wo.setPriority("0");   // 不在 PriorityEnum 内
        wo.setStatus("9");     // 不在 WorkOrderStatusEnum 内

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("orderType")).isEqualTo("99");
        assertThat(fields.get("priority")).isEqualTo("0");
        assertThat(fields.get("status")).isEqualTo("9");
    }

    @Test
    @DisplayName("build：日期非空时按 yyyy年MM月dd日 格式化")
    void build_dateFormatted() {
        ZwWorkOrder wo = new ZwWorkOrder();
        Date date = new Date(0L); // 1970-01-01
        wo.setCreateTime(date);

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("createTime")).matches("\\d{4}年\\d{2}月\\d{2}日");
    }

    @Test
    @DisplayName("build：工程师查询异常时不抛错，回退 '-'")
    void build_engineerLookupException_returnsDash() {
        ZwWorkOrder wo = new ZwWorkOrder();
        wo.setAssignedTo(99L);
        when(sysUserService.selectUserById(eq(99L))).thenThrow(new RuntimeException("DB error"));

        Map<String, String> fields = fieldMapper.build(wo);

        assertThat(fields.get("engineerName")).isEqualTo("-");
    }
}