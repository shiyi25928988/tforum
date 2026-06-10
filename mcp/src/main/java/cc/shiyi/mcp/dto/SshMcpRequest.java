package cc.shiyi.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SshMcpRequest {
    @ToolParam(required = true, description = "target server ip")
    private String ip;

    @ToolParam(required = false, description = "target server ssh port, default value 22")
    private Integer port = 22;

    @ToolParam(required = true, description = "target server ssh user")
    private String user;

    @ToolParam(required = true, description = "target server ssh password")
    private String passwd;

    @ToolParam(required = true, description = "target server ssh cmd")
    private String cmd;
}
