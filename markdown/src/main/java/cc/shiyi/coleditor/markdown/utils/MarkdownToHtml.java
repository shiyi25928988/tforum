package cc.shiyi.coleditor.markdown.utils;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.autolink.AutolinkType;
import org.commonmark.ext.footnotes.FootnotesExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

@Slf4j
public final class MarkdownToHtml {

    public static String render(String markdown) {
        List<Extension> extensions = List.of(
                TablesExtension.create(),
                AutolinkExtension.builder().linkTypes(AutolinkType.URL, AutolinkType.EMAIL, AutolinkType.WWW).build(),
                FootnotesExtension.builder().inlineFootnotes(true).build(),
                HeadingAnchorExtension.builder().build(),
                ImageAttributesExtension.create(),
                InsExtension.create(),
                StrikethroughExtension.builder().build(),
                TaskListItemsExtension.create(),
                YamlFrontMatterExtension.create()
            );
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                //.attributeProviderFactory(a -> HeadingIdAttributeProvider.create(""))
                .build();
        return renderer.render(document);
    }

}
