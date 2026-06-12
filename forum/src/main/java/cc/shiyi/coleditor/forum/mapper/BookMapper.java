package cc.shiyi.coleditor.forum.mapper;

import cc.shiyi.coleditor.forum.table.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Select("select MAX(id) from book")
    Long maxId();

    @Update("UPDATE book SET is_deleted = 0, updated_time = NOW() WHERE id = #{id}")
    void recoverById(Long id);
}
