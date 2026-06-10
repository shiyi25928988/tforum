package cc.shiyi.coleditor.markdown.service;

import cc.shiyi.coleditor.markdown.mapper.MarkdownDocMapper;
import cc.shiyi.coleditor.markdown.table.MarkdownDoc;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Setter(onMethod_ = @Autowired)
public class MarkdownDocService {

    private MarkdownDocMapper markdownDocMapper;

    public MarkdownDoc save(MarkdownDoc doc) {
        Long id;
        if (Objects.isNull(doc.getId()) || Objects.isNull(markdownDocMapper.selectById(doc.getId()))) {
            id = genNewId();
            doc.setId(id);
            doc.setCreatedTime(new Date());
            doc.setViewCount(0);
            markdownDocMapper.insert(doc);
        } else {
            id = doc.getId();
            doc.setUpdatedTime(new Date());
            markdownDocMapper.updateById(doc);
        }
        return getById(id);
    }

    public MarkdownDoc getById(Long id) {
        MarkdownDoc doc = markdownDocMapper.selectById(id);
        if (Objects.nonNull(doc)) {
            doc.setViewCount(Objects.isNull(doc.getViewCount()) ? 1 : doc.getViewCount() + 1);
            markdownDocMapper.updateById(doc);
        }
        return doc;
    }

    public void delete(Long id) {
        MarkdownDoc doc = markdownDocMapper.selectById(id);
        if (Objects.nonNull(doc)) {
            doc.setIsDeleted(1);
            doc.setUpdatedTime(new Date());
            markdownDocMapper.updateById(doc);
        }
    }

    public Page<MarkdownDoc> listByPage(int pageNum, int pageSize, String keyword) {
        Page<MarkdownDoc> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MarkdownDoc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        if (Objects.nonNull(keyword) && !keyword.isEmpty()) {
            queryWrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
        }
        queryWrapper.orderByDesc("created_time");
        return markdownDocMapper.selectPage(page, queryWrapper);
    }

    private Long genNewId() {
        if (Objects.isNull(markdownDocMapper.maxId())) {
            return 1L;
        }
        return markdownDocMapper.maxId() + 1L;
    }

}
