package com.ai.mcpserverremote.service;

import com.ai.mcpserverremote.entity.HelpDeskTicket;
import com.ai.mcpserverremote.model.TicketRequest;
import com.ai.mcpserverremote.repository.HelpDeskTIckerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class HelpdeskTicketService {
    private final HelpDeskTIckerRepository helpDeskTIckerRepository;


    public HelpDeskTicket createTicket(TicketRequest ticketRequest) {
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketRequest.issue())
                .username(ticketRequest.username())
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTIckerRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketByUsername(String username) {
        return helpDeskTIckerRepository.findByUsername(username);
    }
}