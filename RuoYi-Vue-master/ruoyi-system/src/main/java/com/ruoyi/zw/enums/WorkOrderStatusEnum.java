package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 工单状态
 *
 * <p>对应 {@code zw_work_order.status} 字段，数据库存字符串码。
 * 前端、移动端约定一致的字符串值；新增状态时仅改本枚举即可。
 */
public enum WorkOrderStatusEnum {

    PENDING("0", "待接单"),
    PROCESSING("1", "处理中"),
    CONFIRMING("2", "待确认"),
    COMPLETED("3", "已完成"),
    CANCELLED("4", "已取消");

    @JsonValue
    private final String code;
    private final String label;

    WorkOrderStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /** 按 code 查找枚举；找不到返回 null（业务可决定是否降级为"未知"） */
    public static WorkOrderStatusEnum of(String code) {
        if (code == null) return null;
        for (WorkOrderStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }

    /** code 合法判断 */
    public static boolean isValid(String code) {
        return of(code) != null;
    }
}
