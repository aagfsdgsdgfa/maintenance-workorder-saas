package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 巡检任务状态
 *
 * <p>对应 {@code zw_inspection_task.status} 字段。
 * 与 {@link WorkOrderStatusEnum} 数值含义不同（巡检有自己的状态机）。
 */
public enum InspectionStatusEnum {

    PENDING("0", "待执行"),
    IN_PROGRESS("1", "执行中"),
    CONFIRMING("2", "待确认"),
    COMPLETED("3", "已完成"),
    CANCELLED("4", "已取消");

    @JsonValue
    private final String code;
    private final String label;

    InspectionStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static InspectionStatusEnum of(String code) {
        if (code == null) return null;
        for (InspectionStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }

    public static boolean isValid(String code) {
        return of(code) != null;
    }
}
