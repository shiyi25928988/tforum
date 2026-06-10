package cc.shiyi.coleditor.markdown.utils;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DocxToMarkdown {


    public static File convert(File docxFile) throws IOException {
        String userHome = System.getProperty("user.home");
        String filePath = userHome + File.separator + System.currentTimeMillis() + "_.md";
        File markdownFile = new File(filePath);
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis);
             PrintWriter writer = new PrintWriter(markdownFile, "UTF-8")) {
            for (IBodyElement element : document.getBodyElements()) {
                if (element.getElementType() == BodyElementType.PARAGRAPH) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    convertParagraph(paragraph, writer);
                } else if (element.getElementType() == BodyElementType.TABLE) {
                    XWPFTable table = (XWPFTable) element;
                    convertTable(table, writer);
                }
                // 忽略其他元素如分节符等
            }
        }
        return markdownFile;
    }

    private static void convertParagraph(XWPFParagraph paragraph, PrintWriter writer) {
        String text = paragraph.getText().trim();

        if (text.isEmpty()) {
            writer.println();
            return;
        }

        // 判断标题级别
        int headingLevel = getHeadingLevel(paragraph);
        if (headingLevel > 0) {
            writer.print("#".repeat(headingLevel) + " " + text);
            writer.println();
            return;
        }

        // 处理普通段落和样式
        StringBuilder mdPara = new StringBuilder();

        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0); // 简单处理，忽略复杂文本分段
            if (runText == null) continue;

            String formatted = runText.replace("\n", "  \n"); // 换行处理

            // 加粗
            if (run.isBold()) {
                formatted = "**" + formatted + "**";
            }
            // 斜体
            if (run.isItalic()) {
                formatted = "*" + formatted + "*";
            }
            // 下划线（Markdown 不标准支持，可用 `<u>` 或忽略）
            if (run.getUnderline() != UnderlinePatterns.NONE) {
                formatted = "<u>" + formatted + "</u>";
            }
            // 删除线
            if (run.isStrikeThrough()) {
                formatted = "~~" + formatted + "~~";
            }

            mdPara.append(formatted);
        }

        // 列表判断
        if (paragraph.getNumID() != null && !paragraph.getNumID().toString().equals("0")) {
            String prefix = "  ".repeat(Math.max(0, paragraph.getIndentFromLeft() / 40)) + "- ";
            writer.println(prefix + mdPara);
        } else {
            writer.println(mdPara);
        }
        writer.println(); // 段后空行
    }

    private static int getHeadingLevel(XWPFParagraph paragraph) {
        String styleID = paragraph.getStyle();
        if (styleID == null) return 0;
        switch (styleID) {
            case "Heading1": return 1;
            case "Heading2": return 2;
            case "Heading3": return 3;
            case "Heading4": return 4;
            case "Heading5": return 5;
            default: return 0;
        }
    }

    private static void convertTable(XWPFTable table, PrintWriter writer) {
        // 写入表格头（仅写一行 --- 分隔符）
        boolean firstRow = true;
        for (XWPFTableRow row : table.getRows()) {
            StringBuilder rowLine = new StringBuilder("|");
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = cell.getText().replace("\n", " ");
                rowLine.append(" ").append(cellText).append(" |");
            }
            writer.println(rowLine);

            if (firstRow) {
                // 表头下加分割线
                StringBuilder separator = new StringBuilder("|");
                for (int i = 0; i < row.getTableCells().size(); i++) {
                    separator.append(" --- |");
                }
                writer.println(separator);
                firstRow = false;
            }
        }
        writer.println();
    }
}
