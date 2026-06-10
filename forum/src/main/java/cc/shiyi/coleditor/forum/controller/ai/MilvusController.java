package cc.shiyi.coleditor.forum.controller.ai;

import cc.shiyi.coleditor.common.ai.document.MilvusInitiator;
import cc.shiyi.coleditor.common.http.ResponseWrapper;
import com.google.common.base.Strings;
import io.milvus.param.RpcStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "Milvus向量数据库控制-MilvusController")
public class MilvusController {

    MilvusInitiator milvusInitiator;

    @Operation(summary = "如果集合不存在就创建")
    @PostMapping("/api/v1/milvus/createCollectionIfNotExists")
    public ResponseWrapper<RpcStatus> createCollectionIfNotExists() {
        RpcStatus rpcStatus = milvusInitiator.createIfNotExists();
        if (Objects.nonNull(rpcStatus)) {
            if(!Strings.isNullOrEmpty(rpcStatus.getMsg()) && "Success".equals(rpcStatus.getMsg())){
                return new ResponseWrapper<>().success(rpcStatus);
            }
        }
        return new ResponseWrapper<>().fail();
    }

    @Operation(summary = "删除集合并重新创建")
    @PostMapping("/api/v1/milvus/dropAndCreateCollection")
    public ResponseWrapper<RpcStatus> dropAndCreateCollection() {
        RpcStatus rpcStatus = milvusInitiator.dropAndCreateCollection();
        if (Objects.nonNull(rpcStatus)) {
            if(!Strings.isNullOrEmpty(rpcStatus.getMsg()) && "Success".equals(rpcStatus.getMsg())){
                return new ResponseWrapper<>().success(rpcStatus);
            }
        }
        return new ResponseWrapper<>().fail();
    }
}
