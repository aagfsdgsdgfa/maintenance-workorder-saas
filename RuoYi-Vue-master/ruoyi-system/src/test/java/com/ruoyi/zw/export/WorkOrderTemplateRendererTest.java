package com.ruoyi.zw.export;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WorkOrderTemplateRendererTest {

    private XWPFDocument document;
    private final WorkOrderTemplateRenderer renderer = new WorkOrderTemplateRenderer();

    @BeforeEach
    void setUp() {
        document = new XWPFDocument();
    }

    @AfterEach
    void tearDown() throws Exception {
        document.close();
    }

    @Test
    @DisplayName("render：段落中 ${field} 占位符被替换")
    void render_paragraphPlaceholderReplaced() {
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("工单号：${orderNo}");

        Map<String, String> fields = new HashMap<>();
        fields.put("orderNo", "WO-001");

        renderer.render(document, fields);

        assertThat(para.getText()).isEqualTo("工单号：WO-001");
    }

    @Test
    @DisplayName("render：占位符在文本中部")
    void render_placeholderInMiddle() {
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("类型：${orderType}，优先级：${priority}");

        Map<String, String> fields = new HashMap<>();
        fields.put("orderType", "维修");
        fields.put("priority", "高");

        renderer.render(document, fields);

        assertThat(para.getText()).isEqualTo("类型：维修，优先级：高");
    }

    @Test
    @DisplayName("render：缺失字段替换为空字符串")
    void render_missingField_emptyString() {
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("客户：${customerName}");

        renderer.render(document, new HashMap<>());

        assertThat(para.getText()).isEqualTo("客户：");
    }

    @Test
    @DisplayName("render：复选框字段 ☑ 保留且不剥离右侧文本")
    void render_checkboxValue_keptWithTrailing() {
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("是否收费：${chargeStatus}（含税）");

        Map<String, String> fields = new HashMap<>();
        fields.put("chargeStatus", "☑");

        renderer.render(document, fields);

        // 复选框占位符保留，且其后文本（"（含税）"）追加到 value 后
        assertThat(para.getText()).contains("☑");
    }

    @Test
    @DisplayName("render：无占位符的段落保持不变")
    void render_noPlaceholderUnchanged() {
        XWPFParagraph para = document.createParagraph();
        para.createRun().setText("这是普通文本，没有占位符");

        renderer.render(document, new HashMap<>());

        assertThat(para.getText()).isEqualTo("这是普通文本，没有占位符");
    }

    @Test
    @DisplayName("render：多段落独立替换")
    void render_multipleParagraphs() {
        XWPFParagraph p1 = document.createParagraph();
        p1.createRun().setText("第一段：${a}");
        XWPFParagraph p2 = document.createParagraph();
        p2.createRun().setText("第二段：${b}");

        Map<String, String> fields = new HashMap<>();
        fields.put("a", "alpha");
        fields.put("b", "beta");

        renderer.render(document, fields);

        assertThat(p1.getText()).isEqualTo("第一段：alpha");
        assertThat(p2.getText()).isEqualTo("第二段：beta");
    }
}