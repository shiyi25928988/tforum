package cc.shiyi.mcp.service;

import cc.shiyi.mcp.dto.SshMcpRequest;
import cc.shiyi.mcp.dto.SshMcpResponse;
import cc.shiyi.mcp.utils.SshUtil;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@org.springframework.stereotype.Service
@Slf4j
public class SshMcpService {

    @Tool(description = "通过ssh连接目标linux服务器，并发送shell命令")
    public SshMcpResponse sendShellCmd(@ToolParam(description = "通过SSH登录linux服务器并发送shell命令，包含ip port user passwd 和 cmd参数") SshMcpRequest request) throws Exception {
        log.info("sendShellCmd request: {}", request);
        Session session = SshUtil.getSession(request.getIp(), request.getUser(), request.getPasswd(), request.getPort());
        String res = SshUtil.exec(session, request.getCmd());
        return new SshMcpResponse(res);
    }



}
