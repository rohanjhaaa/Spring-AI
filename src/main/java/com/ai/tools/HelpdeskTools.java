package com.ai.tools;

import com.ai.entity.HelpDeskTicket;
import com.ai.model.TicketRequest;
import com.ai.service.HelpdeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpdeskTools {
    private static final Logger logger = LoggerFactory.getLogger(HelpdeskTools.class);

    private final HelpdeskTicketService helpdeskTicketService;

    /*
        LLM to Tools request (TicketRequest) mapping is handle by spring AI using ToolCallResultConverter
    */

    @Tool(name = "createTicket",
    description = "Create a new support ticket")
    public String createTicket(@ToolParam(description = "The detailed description of the issue or problem faced by the user")TicketRequest ticketRequest,
                               ToolContext toolContext){
        String username = toolContext.getContext().get("username").toString();
        HelpDeskTicket savedTicket = helpdeskTicketService.createTicket(ticketRequest,username);
        return "Ticket #"+savedTicket.getId()+" create successfully for user "+savedTicket;
    }

    @Tool(description = "Fetch the status of the open tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(ToolContext toolContext){
        String username = toolContext.getContext().get("username").toString();
        return helpdeskTicketService.getTicketByUsername(username);
    }

}
