package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Operation(summary = "프로토타입 생성")
    public ResponseEntity<PrototypesDto> createContents(@RequestBody CreateContentsRequestDto request) {
        PrototypesDto prototypesDto = prezService.makePrototype(request.getTopic());
        return ResponseEntity.ok(prototypesDto);
    }

    @PostMapping("/api/create")
    @Operation(summary = "프레젠테이션 생성")
    public ResponseEntity<PresentationResponseDto> createPresentation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateRequestDto requestDto) {
        String email = userDetails.getUsername();
        PresentationResponseDto responseDto = prezService.makePrez(requestDto, email);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/store")
    @Operation(summary = "모든 발표자료 정보 가져오기", description = "현재 로그인 되어있는 유저의 모든 발표자료정보를 가져옴")
    public List<PresentationResponseDto> getAllPresentations(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return prezService.getAllPrez(email);
    }

    @GetMapping("/api/store/{presentationId}")
    @Operation(summary = "발표자료 정보 가져오기", description = "지정한 id의 발표자료정보를 가져옴")
    public PresentationResponseDto getPresentation(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return prezService.getPrez(email, id);
    }

    @GetMapping("/api/store/{presentationId}/slide")
    @Operation(summary = "슬라이드(pptx) 가져오기")
    public ResponseEntity<ByteArrayResource> getSlide(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return prezService.getSlide(email, id);
    }

    @GetMapping("/api/store/{presentationId}/script")
    @Operation(summary = "발표 대본 가져오기")
    public ResponseEntity<InputStreamResource> getScript(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return prezService.getScript(email, id);
    }
}
