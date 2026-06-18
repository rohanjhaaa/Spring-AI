package com.ai.mcpserverremote.config;

import com.ai.mcpserverremote.tools.HelpdeskTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MCPServerConfig {

    @Bean
    public List<ToolCallback> toolCallbacks(HelpdeskTools helpdeskTools){
        return List.of(ToolCallbacks.from(helpdeskTools));
    }
}
