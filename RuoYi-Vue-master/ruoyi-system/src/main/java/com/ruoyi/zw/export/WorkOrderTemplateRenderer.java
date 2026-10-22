package com.ruoyi.zw.export;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word模板占位符渲染器：负责文档段落/表格中的 {@code ${fieldName}} 文本替换。
 *
 * <p>占位符格式：{@code ${fieldName}}（花括号内不允许有右花括号）。
 * 复选框占位符：当字段值以 {@code ☐} 或 {@code ☑} 开头时，原样保留字段值，
 * 不再读取占位符右侧文本（兼容模板内联字段）。
 */
public class WorkOrderTemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public void render(XWPFDocument document, Map<String, String> fields) {
        replacePlaceholdersInParagraphs(document.getParagraphs(), fields);
        for (XWPFTable table : document.getTables()) {
            replacePlaceholdersInTable(table, fields);
        }
    }

    private void replacePlaceholdersInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> fields) {
        for (XWPFParagraph para : paragraphs) {
            String text = para.getText();
            if (text == null || !text.contains("${")) {
                continue;
            }
            String newText = replaceText(text, fields);
            if (!newText.equals(text)) {
                if (!para.getRuns().isEmpty()) {
                    boolean replaced = false;
                    for (int i = 0; i < para.getRuns().size(); i++) {
                        String runText = para.getRuns().get(i).getText(0);
                        if (runText != null && runText.contains("${")) {
                            if (!replaced) {
                                para.getRuns().get(i).setText(newText, 0);
                                replaced = true;
                            } else {
                                para.getRuns().get(i).setText("", 0);
                            }
                        }
                    }
                    if (!replaced) {
                        para.getRuns().forEach(run -> run.setText("", 0));
                        if (!para.getRuns().isEmpty()) {
                            para.getRuns().get(0).setText(newText, 0);
                        }
                    }
                } else {
                    para.createRun().setText(newText, 0);
                }
            }
        }
    }

    private void replacePlaceholdersInTable(XWPFTable table, Map<String, String> fields) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                String text = cell.getText();
                if (text != null && text.contains("${")) {
                    String newText = replaceText(text, fields);
                    cell.removeParagraph(0);
                    XWPFParagraph newPara = cell.addParagraph();
                    newPara.createRun().setText(newText);
                }
                for (XWPFParagraph para : cell.getParagraphs()) {
                    String paraText = para.getText();
                    if (paraText != null && paraText.contains("${")) {
                        String newText = replaceText(paraText, fields);
                        if (!para.getRuns().isEmpty()) {
                            para.getRuns().forEach(run -> run.setText("", 0));
                            para.getRuns().get(0).setText(newText, 0);
                        } else {
                            para.createRun().setText(newText);
                        }
                    }
                }
            }
        }
    }

    private String replaceText(String text, Map<String, String> fields) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = fields.getOrDefault(key, "");
            if (value.startsWith("☐") || value.startsWith("☑")) {
                value = value + getTextAfterPlaceholder(text, matcher.group(0));
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getTextAfterPlaceholder(String text, String placeholder) {
        int idx = text.indexOf(placeholder);
        if (idx >= 0 && idx + placeholder.length() < text.length()) {
            return text.substring(idx + placeholder.length());
        }
        return "";
    }
}