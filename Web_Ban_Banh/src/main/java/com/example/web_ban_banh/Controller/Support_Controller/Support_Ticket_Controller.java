package com.example.web_ban_banh.Controller.Support_Controller;

import com.example.web_ban_banh.DTO.TicKet_DTO.TicketDTO;
import com.example.web_ban_banh.DTO.TicKet_DTO.TicketMessageDTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserPublic_DTO;
import com.example.web_ban_banh.Service.SupportTicket_Service.SupportTicket_ServiceIn;
import com.example.web_ban_banh.Service.User_Service.User_ServiceIn;
import com.example.web_ban_banh.Utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/support/tickets")

public class Support_Ticket_Controller {
    private final SupportTicket_ServiceIn ticketService;
    private final User_ServiceIn userService;

    @Autowired
    public Support_Ticket_Controller(SupportTicket_ServiceIn ticketService, User_ServiceIn userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO dto, Authentication auth) {
        String username = AuthUtils.getUsername(auth); // đổi từ getEmail sang getUsername
        UserPublic_DTO userDto = userService.findByUsername(username);
        return ResponseEntity.ok(ticketService.createTicket(dto, userDto.getId()));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TicketDTO>> getMyTickets(Authentication auth) {
        String username = AuthUtils.getUsername(auth);
        UserPublic_DTO userDto = userService.findByUsername(username);

        return ResponseEntity.ok(ticketService.getTicketsByUser(userDto.getId()));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDTO> getTicketDetail(@PathVariable Integer ticketId, Authentication auth) {
        String username = AuthUtils.getUsername(auth);
        UserPublic_DTO userDto = userService.findByUsername(username);
        boolean isAdmin = AuthUtils.isAdmin(auth);
        return ResponseEntity.ok(ticketService.getTicketDetail(ticketId, userDto.getId(), isAdmin));
    }

    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<TicketMessageDTO> addMessage(@PathVariable Integer ticketId, @RequestBody Map<String, String> req, Authentication auth) {
        String username = AuthUtils.getUsername(auth);
        UserPublic_DTO userDto = userService.findByUsername(username);
        boolean isStaff = AuthUtils.isAdmin(auth) || AuthUtils.isPartner(auth);
        String message = req.get("message");
        return ResponseEntity.ok(ticketService.addMessage(ticketId, userDto.getId(), message, isStaff));
    }

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets(@RequestParam(required = false) String status, @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ticketService.adminGetTickets(status, keyword));
    }

    @PostMapping("/{ticketId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer ticketId, @RequestBody Map<String, String> req) {
        ticketService.updateStatus(ticketId, req.get("status"));
        return ResponseEntity.ok().build();
    }
}
