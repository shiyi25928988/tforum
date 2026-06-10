package cc.shiyi.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailSendMcpRequest {
    @ToolParam(required = true, description = "send email from")
    private String from;

    @ToolParam(required = true, description = "send email password")
    private String password;

//    @ToolParam(required = true, description = "send email smtp host")
//    private String smtpHost;

    @ToolParam(required = true, description = "send email to")
    private String to;

    @ToolParam(required = true, description = "send email subject")
    private String subject;

    @ToolParam(required = true, description = "send email content")
    private String content;
}
