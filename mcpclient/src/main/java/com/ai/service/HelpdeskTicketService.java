package com.ai.service;

import com.ai.entity.HelpDeskTicket;
import com.ai.model.TicketRequest;
import com.ai.repository.HelpDeskTIckerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class HelpdeskTicketService {
    private final HelpDeskTIckerRepository helpDeskTIckerRepository;


    public HelpDeskTicket createTicket(TicketRequest ticketRequest, String username){
        HelpDeskTicket ticket = HelpDeskTicket.builder()
                .issue(ticketRequest.issue())
                .username(username)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .eta(LocalDateTime.now().plusDays(7))
                .build();
        return helpDeskTIckerRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketByUsername(String username){
        return helpDeskTIckerRepository.findByUsername(username);
    }

}
