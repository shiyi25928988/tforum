package cc.shiyi.oss.http;

import cc.shiyi.oss.utils.MinioUrlUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Jackson 序列化器，将直接的 MinIO URL 转换为后端代理 URL。
 * 通过 {@code @JsonSerialize(using = MinioUrlSerializer.class)} 注解在实体字段上使用。
 */
public class MinioUrlSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(MinioUrlUtil.toProxyUrl(value));
    }
}
