package com.ai.mcpserverremote.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "HELPDESK_TICKET")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDeskTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String issue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime eta;
}
