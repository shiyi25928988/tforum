package cc.shiyi.mcp.service;

import cc.shiyi.mcp.dto.EmailSendMcpRequest;
import cc.shiyi.mcp.dto.McpResponse;
import cc.shiyi.mcp.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@org.springframework.stereotype.Service
@Slf4j
public class EmailSendMcpService {

    @Tool(description = "send Email")
    public McpResponse sendEmail(@ToolParam(description = "send email from") EmailSendMcpRequest request){
        try {
            String res = EmailUtil.sendEmail(request.getFrom(), request.getPassword(), request.getTo(), request.getSubject(), request.getContent());
            return new McpResponse(res);
        }catch (Exception e) {
            log.error("send email error", e);
            return new McpResponse("send email error");
        }
    }
}
