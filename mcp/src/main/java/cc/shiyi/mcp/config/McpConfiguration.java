package cc.shiyi.mcp.config;

import cc.shiyi.mcp.service.RagMcpService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class McpConfiguration {

    @Autowired
    RagMcpService ragMcpService;

    @Bean
    public ToolCallbackProvider toolCallbackProvider() {
        return MethodToolCallbackProvider.builder().toolObjects(
                Arrays.asList(ragMcpService).toArray()
        ).build();
    }
}
