package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.dto.response.ScriptResponseDto;
import prezwiz.server.dto.request.CreateContentsRequestDto;
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.prez.PrezService;

@Tag(name="slide", description="슬라이드 관련 controller")
@RestController
@RequestMapping("/api/prez")
@RequiredArgsConstructor
public class SlideController {

    private final PrezService prezService;
    private final JwtUtil jwtUtil;

    @PostMapping("/prototype")
    @Operation(summary = "프로토타입 생성")
    public ResponseEntity<PrototypeResponseDto> createPrototype(@RequestBody CreateContentsRequestDto request) {
        PrototypeResponseDto responseDto = prezService.makePrototype(request.getTopic());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/slides/{presentationId}")
    @Operation(summary = "슬라이드 생성")
    public ResponseEntity<SlidesDto> createSlides(@RequestBody PrototypesDto prototypesDto, @PathVariable("presentationId") Long id) {
        SlidesDto slidesDto = prezService.makeSlide(prototypesDto, id);
        return ResponseEntity.ok(slidesDto);
    }

    @PostMapping("/script/{presentationId}")
    @Operation(summary = "대본 생성")
    public ResponseEntity<ScriptResponseDto> createScript(@RequestBody SlidesDto slidesDto, @PathVariable("presentationId") Long id) {
        String script = prezService.makeScript(slidesDto, id);
        return ResponseEntity.ok(new ScriptResponseDto(script));
    }

//    @PostMapping("/api/create")
//    @Operation(summary = "프레젠테이션 생성")
//    public ResponseEntity<PresentationResponseDto> createPresentation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateRequestDto requestDto) {
//        String email = userDetails.getUsername();
//        PresentationResponseDto responseDto = prezService.makePrez(requestDto, email);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    @GetMapping("/api/store")
//    @Operation(summary = "모든 발표자료 정보 가져오기", description = "현재 로그인 되어있는 유저의 모든 발표자료정보를 가져옴")
//    public List<PresentationResponseDto> getAllPresentations(@AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        return prezService.getAllPrez(email);
//    }
//
//    @GetMapping("/api/store/{presentationId}")
//    @Operation(summary = "발표자료 정보 가져오기", description = "지정한 id의 발표자료정보를 가져옴")
//    public PresentationResponseDto getPresentation(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        return prezService.getPrez(email, id);
//    }
//
//    @GetMapping("/api/store/{presentationId}/slide")
//    @Operation(summary = "슬라이드(pptx) 가져오기")
//    public ResponseEntity<ByteArrayResource> getSlide(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        return prezService.getSlide(email, id);
//    }
//
//    @GetMapping("/api/store/{presentationId}/script")
//    @Operation(summary = "발표 대본 가져오기")
//    public ResponseEntity<InputStreamResource> getScript(@PathVariable("presentationId") Long id, @AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        return prezService.getScript(email, id);
//    }
}
