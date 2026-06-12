package cc.shiyi.coleditor.forum.service;

import cc.shiyi.coleditor.forum.mapper.BookMapper;
import cc.shiyi.coleditor.forum.table.Book;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Setter(onMethod_ = @Autowired)
public class BookService {

    private BookMapper bookMapper;

    public boolean existsByHash(String fileHash) {
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_hash", fileHash);
        queryWrapper.eq("is_deleted", 0);
        return bookMapper.selectCount(queryWrapper) > 0;
    }

    public Book save(Book book) {
        Long id;
        if (Objects.isNull(book.getId()) || Objects.isNull(bookMapper.selectById(book.getId()))) {
            id = genNewId();
            book.setId(id);
            book.setCreatedTime(new Date());
            book.setDownloadCount(0);
            book.setViewCount(0);
            bookMapper.insert(book);
        } else {
            id = book.getId();
            book.setUpdatedTime(new Date());
            bookMapper.updateById(book);
        }
        return getById(id);
    }

    public Book getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (Objects.nonNull(book)) {
            book.setViewCount(Objects.isNull(book.getViewCount()) ? 1 : book.getViewCount() + 1);
            bookMapper.updateById(book);
        }
        return book;
    }

    public void delete(Long id) {
        bookMapper.deleteById(id);
    }

    public Page<Book> listByPage(int pageNum, int pageSize, Long categoryId, String keyword) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        if (Objects.nonNull(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (Objects.nonNull(keyword) && !keyword.isEmpty()) {
            queryWrapper.and(w -> w.like("title", keyword).or().like("author", keyword));
        }
        queryWrapper.orderByDesc("created_time");
        return bookMapper.selectPage(page, queryWrapper);
    }

    public void increaseDownload(Long id) {
        Book book = bookMapper.selectById(id);
        if (Objects.nonNull(book)) {
            book.setDownloadCount(Objects.isNull(book.getDownloadCount()) ? 1 : book.getDownloadCount() + 1);
            bookMapper.updateById(book);
        }
    }

    private Long genNewId() {
        if (Objects.isNull(bookMapper.maxId())) {
            return 1L;
        }
        return bookMapper.maxId() + 1L;
    }
}
