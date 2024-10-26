package prezwiz.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.dto.request.CreateContentsRequestDto;
import prezwiz.server.dto.request.CreateRequestDto;
import prezwiz.server.dto.response.PresentationResponseDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.prez.PrezService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SlideController {

    private final PrezService prezService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/contents")
    public ResponseEntity<PrototypesDto> createContents(@RequestBody CreateContentsRequestDto request) {
        PrototypesDto prototypesDto = prezService.makePrototype(request.getTopic());
        return ResponseEntity.ok(prototypesDto);
    }

    @PostMapping("/api/create")
    public ResponseEntity<PresentationResponseDto> createPPTX(@RequestHeader("authorization") String bearer, @RequestBody CreateRequestDto requestDto) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        PresentationResponseDto responseDto = prezService.makePrez(requestDto, email);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/store")
    public List<PresentationResponseDto> getAllPresentations(@RequestHeader("authorization") String bearer) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return prezService.getAllPrez(email);
    }

    @GetMapping("/api/store/{presentationId}")
    public PresentationResponseDto getPresentation(@PathVariable("presentationId") Long id, @RequestHeader("authorization") String bearer) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return prezService.getPrez(email, id);
    }

    @GetMapping("/api/store/{presentationId}/slide")
    public ResponseEntity<ByteArrayResource> getSlide(@RequestHeader("authorization") String bearer, @PathVariable("presentationId") Long id) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return prezService.getSlide(email, id);
    }

    @GetMapping("/api/store/{presentationId}/script")
    public ResponseEntity<InputStreamResource> getScript(@RequestHeader("authorization") String bearer, @PathVariable("presentationId") Long id) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return prezService.getScript(email, id);
    }
}
