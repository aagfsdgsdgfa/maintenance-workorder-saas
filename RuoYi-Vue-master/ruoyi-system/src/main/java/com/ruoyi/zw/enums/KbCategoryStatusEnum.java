package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 知识库分类启停状态
 *
 * <p>对应 {@code zw_kb_category.status} 字段。
 */
public enum KbCategoryStatusEnum {

    ENABLED("0", "正常"),
    DISABLED("1", "停用");

    @JsonValue
    private final String code;
    private final String label;

    KbCategoryStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static KbCategoryStatusEnum of(String code) {
        if (code == null) return null;
        for (KbCategoryStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
