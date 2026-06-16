package com.ai.repository;

import com.ai.entity.HelpDeskTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HelpDeskTIckerRepository extends JpaRepository<HelpDeskTicket , Long> {
    List<HelpDeskTicket> findByUsername(String username);
}
