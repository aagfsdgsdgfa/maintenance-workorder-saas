package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 工单优先级
 *
 * <p>对应 {@code zw_work_order.priority} 字段。
 */
public enum PriorityEnum {

    LOW("1", "低"),
    MEDIUM("2", "中"),
    HIGH("3", "高"),
    URGENT("4", "紧急");

    @JsonValue
    private final String code;
    private final String label;

    PriorityEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PriorityEnum of(String code) {
        if (code == null) return null;
        for (PriorityEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
