package cc.shiyi.coleditor.common.ai.document;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MilvusInitiator {

    @Autowired
    MilvusClient milvusClient;

    @Autowired
    EmbeddingModel embeddingModel;

    @Value("${spring.ai.vectorstore.milvus.client.database-name:default}")
    String databaseName;

    @Value("${spring.ai.vectorstore.milvus.client.collection-name:vector_store}")
    String collectionName;

    /**
     * 删除并重新创建集合
     * <p>
     * 该方法首先删除指定的集合，然后调用createIfNotExists方法重新创建集合。
     * 使用默认的集合名称和数据库名称进行操作。
     *
     * @return RpcStatus 创建操作的返回状态
     */
    public RpcStatus dropAndCreateCollection() {
        milvusClient.dropCollection(DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDatabaseName(databaseName)
                .build());
        return createIfNotExists();
    }

    /**
     * 创建Milvus集合（如果不存在的话）
     * <p>
     * 该方法首先检查默认集合是否存在，如果不存在则创建集合并初始化相关配置。
     * 创建成功后会自动创建索引并加载集合到内存中。
     *
     * @return RpcStatus 创建操作的状态信息，如果集合已存在则返回null
     * @throws Exception 当创建集合、索引或加载集合过程中发生错误时抛出
     */
    public RpcStatus createIfNotExists() {
        boolean exists = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build()).getData();
        if (!exists) {
            try {
                RpcStatus status = milvusClient.createCollection((CreateCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withSchema(CollectionSchemaParam.newBuilder()
                                .withEnableDynamicField(true)
                                .withFieldTypes(composeFieldTypes())
                                .build())
                        .build())).getData();
                if (RpcStatus.SUCCESS_MSG.equals(status.getMsg())) {
                    milvusClient.createIndex(CreateIndexParam.newBuilder()
                            .withDatabaseName(databaseName)
                            .withCollectionName(collectionName)
                            .withFieldName("embedding")
                            .withIndexType(IndexType.AUTOINDEX)
                            .build());
                    milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                            .withDatabaseName(databaseName)
                            .withCollectionName(collectionName)
                            .build());
                }
                return status;
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return null;
    }

    /**
     * 构造字段类型列表
     * <p>
     * 该方法用于创建并返回一个字段类型列表，包含以下四个字段：
     * 1. id字段：主键，自动递增的Int64类型
     * 2. content字段：非空的VarChar类型，最大长度65535
     * 3. metadata字段：可为空的JSON类型
     * 4. embedding字段：非空的FloatVector类型，维度由dimension变量指定
     *
     * @return 包含四个字段类型的列表
     */
    private List<FieldType> composeFieldTypes() {
        List<FieldType> fieldTypes = new ArrayList<>();
        fieldTypes.add(FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .withNullable(false)
                .build());
        fieldTypes.add(FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withMaxLength(10000)
                .withNullable(false)
                .build());
        fieldTypes.add(FieldType.newBuilder()
                .withName("metadata")
                .withDataType(DataType.JSON)
                .withNullable(true)
                .build());
        fieldTypes.add(FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(embeddingModel.dimensions())
                .withNullable(false)
                .build());
        return fieldTypes;
    }

}
