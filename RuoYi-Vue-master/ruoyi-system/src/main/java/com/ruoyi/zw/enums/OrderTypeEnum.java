package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 工单类型
 *
 * <p>对应 {@code zw_work_order.order_type} 字段。注意码从 "1" 开始（"0" 不使用）。
 */
public enum OrderTypeEnum {

    INSPECTION("1", "巡检"),
    REPAIR("2", "维修"),
    DELIVERY("3", "送货"),
    MIXED("4", "混合");

    @JsonValue
    private final String code;
    private final String label;

    OrderTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static OrderTypeEnum of(String code) {
        if (code == null) return null;
        for (OrderTypeEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
