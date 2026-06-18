package com.ai.mcpserverremote.tools;

import com.ai.mcpserverremote.entity.HelpDeskTicket;
import com.ai.mcpserverremote.model.TicketRequest;
import com.ai.mcpserverremote.service.HelpdeskTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelpdeskTools {

    private final HelpdeskTicketService helpdeskTicketService;

    @Tool(name = "createTicket",
            description = "Create a new support ticket")
    public String createTicket(@ToolParam(description = "The detailed description of the issue or problem faced by the user")
                                   TicketRequest ticketRequest)
    {
        log.info("Creating support ticket for users with details {}", ticketRequest);
        HelpDeskTicket savedTicket = helpdeskTicketService.createTicket(ticketRequest);
        log.info("Ticket created successfully for user : {}", savedTicket.getUsername());
        return "Ticket #"+savedTicket.getId()+" create successfully for user "+savedTicket;
    }

    @Tool(name = "getTicketStatus", description = "Fetch the status of the open tickets based on a given username")
    List<HelpDeskTicket> getTicketStatus(@ToolParam(description = "Username to fetch the status of the help desk tickets")
                                         String username)
    {
        log.info("Fetching ticket for user : {}",username);
        List<HelpDeskTicket> tickets = helpdeskTicketService.getTicketByUsername(username);
        log.info("Ticket fetched for user : {}",username);
        return tickets;
    }

}
