package cc.shiyi.coleditor.markdown.mapper;

import cc.shiyi.coleditor.markdown.table.MarkdownDoc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MarkdownDocMapper extends BaseMapper<MarkdownDoc> {

    @Select("select MAX(id) from markdown_doc")
    Long maxId();
}
