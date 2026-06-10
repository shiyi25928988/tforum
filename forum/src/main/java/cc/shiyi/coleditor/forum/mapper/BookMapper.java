package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Select("select MAX(id) from book")
    Long maxId();
}
