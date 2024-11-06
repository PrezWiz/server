package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.dto.request.ContactMessageRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.contact.ContactService;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContactController {

    private final ContactService contactService;
    private final JwtUtil jwtUtil;

    @PostMapping("/contact")
    @Operation(summary = "contact 메세지 처리")
    public ResponseEntity<ResponseDto> contact(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ContactMessageRequestDto request) {
        String email = userDetails.getUsername();
        return contactService.handleMessage(email, request);
    }
}
