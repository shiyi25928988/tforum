package cc.shiyi.mcp.dto;

import lombok.Data;

@Data
public class McpResponse {
    private String response;

    public McpResponse(String response) {
        this.response = response;
    }
}
