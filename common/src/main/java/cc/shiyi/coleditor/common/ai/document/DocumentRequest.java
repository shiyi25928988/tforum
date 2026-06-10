package cc.shiyi.coleditor.common.ai.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {


    private String content;

    private Map<String, Object> metadata;

}
