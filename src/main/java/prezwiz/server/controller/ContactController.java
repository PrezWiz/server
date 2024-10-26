package prezwiz.server.controller;

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
    public ResponseEntity<ResponseDto> contact(@RequestHeader("authorization") String bearer, @RequestBody ContactMessageRequestDto request) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return contactService.handleMessage(email, request);
    }
}
