package cc.shiyi.coleditor.forum.controller.ai;

import cc.shiyi.coleditor.common.ai.chat.ChatRequest;
import cc.shiyi.coleditor.common.ai.chat.ParameterizedChatRequest;
import cc.shiyi.coleditor.common.ai.chat.SystemChatRequest;
import cc.shiyi.coleditor.common.ai.config.ChantClientPool;
import cc.shiyi.coleditor.common.ai.service.VectorService;
import cc.shiyi.coleditor.common.ai.utils.PromptComposer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@Setter(onMethod_ = @Autowired)
@Tag(name = "AI对话接口-ChatController")
public class ChatController {

    private VectorService vectorService;
    private ChantClientPool chatClientPool;
    private ToolCallbackProvider toolCallbackProvider;

    @Operation(summary = "简单聊天接口")
    @PostMapping("/api/v1/ai/simpleChat")
    public String simpleChat(@RequestBody ChatRequest request) {
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt()
                .user(request.getMessage())
                .call()
                .content();
    }

    @Operation(summary = "流式聊天接口")
    @PostMapping(value = "/api/v1/ai/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatRequest request) {
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt()
                .user(request.getMessage())
                .toolCallbacks(toolCallbackProvider)
                .stream()
                .content();
    }

    @Operation(summary = "带参数的聊天接口")
    @PostMapping("/api/v1/ai/parameterizedChat")
    public Flux<String> parameterizedChat(@RequestBody ParameterizedChatRequest request) {
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(request.getTemplate())
                .variables(request.getParameters())
                .renderer(StTemplateRenderer.builder().build())
                .build();
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt(promptTemplate.create())
                .stream()
                .content();
    }

    @Operation(summary = "带系统提示的聊天接口")
    @PostMapping(value = "/api/v1/ai/chatWithSystemPrompt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithSystemPrompt(@RequestBody SystemChatRequest request) {
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt()
                .system(request.getSystemMessage())
                .user(request.getUserMessage())
                .stream()
                .content();
    }

    @Operation(summary = "RAG聊天接口")
    @PostMapping(value = "/api/v1/ai/rag", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithRag(@RequestBody ChatRequest request) {
        String query = request.getMessage();
        List<Document> documents = vectorService.searchSimilarDocuments(query, 5);
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt(PromptComposer.compose(query, documents))
                .stream()
                .content();
    }

    @Operation(summary = "MCP流式聊天接口（支持工具调用）")
    @PostMapping(value = "/api/v1/ai/mcpChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> mcpChat(@RequestBody ChatRequest request) {
        ChatClient chatClient = chatClientPool.get(request.getConversationId());
        return chatClient.prompt()
                .user(request.getMessage())
                .tools(toolCallbackProvider)
                .stream()
                .content();
    }

}
