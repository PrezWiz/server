package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.dto.request.ContactMessageRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.contact.ContactService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContactController {

    private final ContactService contactService;
    private final JwtUtil jwtUtil;

    @PostMapping("/contact")
    @Operation(summary = "contact 메세지 처리")
    public ResponseEntity<ResponseDto> contact(@RequestHeader("authorization") String bearer, @RequestBody ContactMessageRequestDto request) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return contactService.handleMessage(email, request);
    }
}
