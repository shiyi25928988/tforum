package cc.shiyi.coleditor.markdown.utils;

import org.apache.commons.io.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.batch.item.util.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HtmlToDocx {



    public static File createDocFile(String filePath) throws IOException {
        File file = new File(filePath);
        if(FileUtils.createNewFile(file)){
            return file;
        }else {
            throw new IOException("无法创建目标文件：" + file.getAbsolutePath());
        }
    }


    /**
     将HTML字符串转换为Word文档（.docx）内容并填充到指定的XWPFDocument对象中。
     该方法使用Jsoup解析HTML，并遍历其DOM结构，将各个节点按格式处理后写入Word文档。
     支持通过baseUri解析相对路径资源。

     @param html 要转换的HTML内容字符串，不能为空
     @param docFile 目标XWPFDocument文档对象，转换后的内容将被添加到此文档中，不能为null
     @throws Exception 如果HTML解析失败或文档处理过程中发生错误 */
    public static void convertHtmlToDocx(String html, File docFile) throws Exception {
        XWPFDocument xwpfDocument = new XWPFDocument();
        org.jsoup.nodes.Document jsoupDocument;
        jsoupDocument = Jsoup.parse(html);
        Element body = jsoupDocument.body();
        for (Node node : body.childNodes()) {
            handleNode(node, xwpfDocument, null, new FormatContext());
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(docFile)) {
            xwpfDocument.write(fileOutputStream);
        }
    }


    static class FormatContext {
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        Integer fontSize = null; // in pt
        String color = null; // hex without #
        String fontFamily = null;
        boolean inList = false;
        int listLevel = 0;
        boolean orderedList = false;
    }

    // 递归处理 Node
    private static void handleNode(Node node, XWPFDocument doc, XWPFParagraph paragraph, FormatContext ctx) throws Exception {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text();
            if (text.trim().length() == 0) {
                // preserve whitespace/newline only if paragraph exists
                if (paragraph != null) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(text);
                }
                return;
            }
            if (paragraph == null) {
                paragraph = doc.createParagraph();
            }
            XWPFRun run = paragraph.createRun();
            applyFormatToRun(run, ctx);
            run.setText(text);
            return;
        } else if (node instanceof Element) {
            Element el = (Element) node;
            String tag = el.tagName().toLowerCase(Locale.ROOT);

            switch (tag) {
                case "p":
                    XWPFParagraph p = doc.createParagraph();
                    parseStyleAttribute(el, ctx);
                    for (Node child : el.childNodes()) {
                        handleNode(child, doc, p, ctx);
                    }
                    break;
                case "br":
                    if (paragraph == null) paragraph = doc.createParagraph();
                    XWPFRun rbr = paragraph.createRun();
                    rbr.addBreak();
                    break;
                case "hr":
                    if (paragraph == null) paragraph = doc.createParagraph();
                    paragraph.setSpacingAfter(20); // 设置段落间距
                    // 设置底部边框为单线
                    paragraph.setBorderBottom(Borders.SINGLE);
                    paragraph.getCTP().addNewR().addNewT().setStringValue(" "); // 确保边框显示
                    break;
                case "h1":
                case "h2":
                case "h3":
                case "h4":
                case "h5":
                case "h6":
                    XWPFParagraph hp = doc.createParagraph();
                    XWPFRun hr = hp.createRun();
                    hr.setBold(true);
                    int size = 16;
                    switch (tag) {
                        case "h1": size = 24; break;
                        case "h2": size = 20; break;
                        case "h3": size = 18; break;
                        case "h4": size = 16; break;
                        case "h5": size = 14; break;
                        case "h6": size = 12; break;
                    }
                    hr.setFontSize(size);
                    for (Node child : el.childNodes()) {
                        if (child instanceof TextNode) {
                            hr.setText(((TextNode) child).text());
                        } else {
                            // nested elements inside headings
                            handleNode(child, doc, hp, ctx);
                        }
                    }
                    break;
                case "strong":
                case "b": {
                    FormatContext newCtx = copyCtx(ctx);
                    newCtx.bold = true;
                    for (Node child : el.childNodes()) handleNode(child, doc, paragraph, newCtx);
                    break;
                }
                case "em":
                case "i": {
                    FormatContext newCtx = copyCtx(ctx);
                    newCtx.italic = true;
                    for (Node child : el.childNodes()) handleNode(child, doc, paragraph, newCtx);
                    break;
                }
                case "u": {
                    FormatContext newCtx = copyCtx(ctx);
                    newCtx.underline = true;
                    for (Node child : el.childNodes()) handleNode(child, doc, paragraph, newCtx);
                    break;
                }
                case "span": {
                    FormatContext newCtx = copyCtx(ctx);
                    parseStyleAttribute(el, newCtx);
                    for (Node child : el.childNodes()) handleNode(child, doc, paragraph, newCtx);
                    break;
                }
                case "a": {
                    String href = el.attr("href");
                    // 尽量创建可点击的超链接（简化：把文本设为显示并附上 URL）
                    XWPFParagraph ph = paragraph;
                    if (ph == null) ph = doc.createParagraph();
                    XWPFRun run = ph.createRun();
                    applyFormatToRun(run, ctx);
                    String text = el.text();
                    // 简化处理：显示为文本并在后面加括号 URL；要创建真正的可点击超链接需要关系操作
                    run.setText(text + " (" + href + ")");
                    break;
                }
                case "ul":
                case "ol": {
                    boolean ordered = "ol".equals(tag);
                    for (Element li : el.select("> li")) {
                        XWPFParagraph lp = doc.createParagraph();
                        // 简化列表：前面加符号或数字
                        XWPFRun lrun = lp.createRun();
                        applyFormatToRun(lrun, ctx);
                        if (ordered) {
                            // 这里我们简单地用 "1. " 前缀（不支持多级编号递增），复杂实现需使用 Numbering API
                            lrun.setText("1. ");
                        } else {
                            lrun.setText("• ");
                        }
                        for (Node child : li.childNodes()) {
                            // 接着写 li 内容到同一个段落（或新段落）
                            handleNode(child, doc, lp, ctx);
                        }
                    }
                    break;
                }
                case "li": {
                    // handled by parent ul/ol
                    XWPFParagraph lp = (paragraph == null) ? doc.createParagraph() : paragraph;
                    for (Node child : el.childNodes()) handleNode(child, doc, lp, ctx);
                    break;
                }
                case "table": {
                    Elements rows = el.select("> tr > tbody > tr");
                    // if no rows, try nested
                    List<Element> rowEls = new ArrayList<>();
                    if (!rows.isEmpty()) {
                        rows.forEach(rowEls::add);
                    } else {
                        // find tr deeper
                        for (Element r : el.select("tr")) rowEls.add(r);
                    }
                    if (rowEls.isEmpty()) break;
                    int cols = Math.max(1, rowEls.get(0).select("> th, > td").size());
                    XWPFTable table = doc.createTable(rowEls.size(), cols);
                    table.setWidth(8000);
                    int rIdx = 0;
                    for (Element rowEl : rowEls) {
                        XWPFTableRow tr = table.getRow(rIdx);
                        Elements cells = rowEl.select("> th, > td");
                        int cIdx = 0;
                        for (Element cellEl : cells) {
                            XWPFTableCell cell = tr.getCell(cIdx);
                            // Clear default paragraph
                            cell.removeParagraph(0);
                            XWPFParagraph cellP = cell.addParagraph();
                            for (Node child : cellEl.childNodes()) {
                                handleNode(child, doc, cellP, ctx);
                            }
                            cIdx++;
                        }
                        rIdx++;
                    }
                    break;
                }
                case "img": {
                    String src = el.attr("src");
                    String abs = el.absUrl("src");
                    String url = abs != null && !abs.isEmpty() ? abs : src;
                    byte[] bytes = fetchImage(url);
                    if (bytes != null && bytes.length > 0) {
                        String filename = src;
                        int pictureType = detectPictureTypeFromFilename(filename);
                        if (pictureType == -1) pictureType = Document.PICTURE_TYPE_PNG;
                        // width/height 可由 style 或 width/height 属性决定，这里简单使用 150px 宽度
                        int widthPx = 150;
                        int heightPx = 150;
                        // 如果有 width/height 属性或 style, 尝试解析
                        String wAttr = el.attr("width"), hAttr = el.attr("height");
                        if (!wAttr.isEmpty()) {
                            try { widthPx = Integer.parseInt(wAttr); } catch (Exception ignored) {}
                        }
                        if (!hAttr.isEmpty()) {
                            try { heightPx = Integer.parseInt(hAttr); } catch (Exception ignored) {}
                        }
                        // 放入段落
                        XWPFParagraph imgP = (paragraph == null) ? doc.createParagraph() : paragraph;
                        XWPFRun imgRun = imgP.createRun();
                        try (InputStream is = new ByteArrayInputStream(bytes)) {
                            imgRun.addPicture(is, pictureType, filename, Units.toEMU(widthPx), Units.toEMU(heightPx));
                        }
                    } else {
                        // 图片获取失败，插入占位文本
                        XWPFParagraph ip = (paragraph == null) ? doc.createParagraph() : paragraph;
                        XWPFRun ir = ip.createRun();
                        ir.setText("[无法加载图片: " + src + "]");
                    }
                    break;
                }
                default: {
                    // 默认：递归处理子节点，保持当前 paragraph
                    for (Node child : el.childNodes()) {
                        handleNode(child, doc, paragraph, ctx);
                    }
                    break;
                }
            }
        } else {
            // 未处理的 Node 类型
        }
    }

    private static FormatContext copyCtx(FormatContext ctx) {
        FormatContext nc = new FormatContext();
        nc.bold = ctx.bold;
        nc.italic = ctx.italic;
        nc.underline = ctx.underline;
        nc.fontSize = ctx.fontSize;
        nc.color = ctx.color;
        nc.fontFamily = ctx.fontFamily;
        nc.inList = ctx.inList;
        nc.listLevel = ctx.listLevel;
        nc.orderedList = ctx.orderedList;
        return nc;
    }

    // 解析 style="..." 中的 color/font-size/font-family 等到 ctx (只解析常见属性)
    private static void parseStyleAttribute(Element el, FormatContext ctx) {
        String style = el.attr("style");
        if (style == null || style.trim().isEmpty()) return;
        String[] parts = style.split(";");
        for (String part : parts) {
            if (part == null || !part.contains(":")) continue;
            String[] kv = part.split(":");
            if (kv.length < 2) continue;
            String key = kv[0].trim().toLowerCase(Locale.ROOT);
            String val = kv[1].trim();
            if (key.equals("color")) {
                String hex = val.replace(" ", "");
                if (hex.startsWith("#")) hex = hex.substring(1);
                if (hex.length() >= 6) ctx.color = hex.substring(0,6);
            } else if (key.equals("font-size")) {
                // 支持 px 和 pt
                if (val.endsWith("px")) {
                    try {
                        int px = Integer.parseInt(val.replace("px", "").trim());
                        // 近似：1pt = 1.333px -> pt = px / 1.333
                        int pt = Math.max(1, Math.round(px / 1.333f));
                        ctx.fontSize = pt;
                    } catch (Exception ignored) {}
                } else if (val.endsWith("pt")) {
                    try {
                        int pt = Integer.parseInt(val.replace("pt", "").trim());
                        ctx.fontSize = pt;
                    } catch (Exception ignored) {}
                }
            } else if (key.equals("font-family")) {
                ctx.fontFamily = val.replace("\"", "").split(",")[0].trim();
            }
        }
    }

    private static void applyFormatToRun(XWPFRun run, FormatContext ctx) {
        run.setBold(ctx.bold);
        run.setItalic(ctx.italic);
        if (ctx.underline) run.setUnderline(UnderlinePatterns.SINGLE);
        if (ctx.fontSize != null) run.setFontSize(ctx.fontSize);
        if (ctx.color != null) run.setColor(ctx.color);
        if (ctx.fontFamily != null) run.setFontFamily(ctx.fontFamily);
    }

    // 简单按扩展名判断图片类型
    private static int detectPictureTypeFromFilename(String filename) {
        if (filename == null) return -1;
        String low = filename.toLowerCase(Locale.ROOT);
        if (low.endsWith(".emf")) return Document.PICTURE_TYPE_EMF;
        if (low.endsWith(".wmf")) return Document.PICTURE_TYPE_WMF;
        if (low.endsWith(".pict")) return Document.PICTURE_TYPE_PICT;
        if (low.endsWith(".jpeg") || low.endsWith(".jpg")) return Document.PICTURE_TYPE_JPEG;
        if (low.endsWith(".png")) return Document.PICTURE_TYPE_PNG;
        if (low.endsWith(".dib")) return Document.PICTURE_TYPE_DIB;
        if (low.endsWith(".gif")) return Document.PICTURE_TYPE_GIF;
        if (low.endsWith(".tiff")) return Document.PICTURE_TYPE_TIFF;
        if (low.endsWith(".eps")) return Document.PICTURE_TYPE_EPS;
        return -1;
    }

    // 支持 http/https 或 file:// 或 相对路径(若 baseUri 在 Jsoup 中已解析)
    private static byte[] fetchImage(String src) {
        if (src == null || src.isEmpty()) return null;
        try {
            if (src.startsWith("http://") || src.startsWith("https://")) {
                URL url = new URL(src);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                try (InputStream is = conn.getInputStream()) {
                    return IOUtils.toByteArray(is);
                }
            } else if (src.startsWith("file:")) {
                URL url = new URL(src);
                try (InputStream is = url.openStream()) {
                    return IOUtils.toByteArray(is);
                }
            } else {
                // 相对或绝对文件路径
                File f = new File(src);
                if (f.exists()) {
                    try (InputStream is = new FileInputStream(f)) {
                        return IOUtils.toByteArray(is);
                    }
                } else {
                    // 尝试作为 URL
                    try {
                        URL url = new URL(src);
                        try (InputStream is = url.openStream()) {
                            return IOUtils.toByteArray(is);
                        }
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
