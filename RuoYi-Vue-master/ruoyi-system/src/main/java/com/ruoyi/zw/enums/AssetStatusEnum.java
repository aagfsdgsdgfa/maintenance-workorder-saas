package com.ruoyi.zw.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 设备资产状态
 *
 * <p>对应 {@code zw_asset.status} 字段。
 */
public enum AssetStatusEnum {

    NORMAL("0", "正常"),
    FAULT("1", "故障"),
    REPAIRING("2", "维修中"),
    SCRAPPED("3", "已报废");

    @JsonValue
    private final String code;
    private final String label;

    AssetStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static AssetStatusEnum of(String code) {
        if (code == null) return null;
        for (AssetStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }

    public static boolean isValid(String code) {
        return of(code) != null;
    }
}
