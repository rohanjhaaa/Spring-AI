package com.ai.mcpserverremote.repository;

import com.ai.mcpserverremote.entity.HelpDeskTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpDeskTIckerRepository extends JpaRepository<HelpDeskTicket, Long> {
    List<HelpDeskTicket> findByUsername(String username);
}