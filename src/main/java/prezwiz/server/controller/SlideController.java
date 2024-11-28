package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.common.adapter.PrezServiceAdapter;
import prezwiz.server.common.annotation.ApiErrorCodeExample;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.response.ScriptResponseDto;
import prezwiz.server.dto.request.CreateOutlineRequestDto;
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.service.prez.PrezService;

@Tag(name="slide", description="슬라이드 관련 controller")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SlideController {

    private final PrezService prezService;

    @PostMapping("/prez/prototype")
    @Operation(summary = "프로토타입 생성",
            description =
                    "response json 에는 이후 요청을 위한 presentationId를 같이 반환합니다." +
                    "\n 이후에 slide를 생성하거나, script를 생성할때 url경로에 같이 보내줘야 합니다.")
    public ResponseEntity<PrototypeResponseDto> createPrototype(@RequestBody CreateOutlineRequestDto request) {
        Long id = prezService.makeTable();
        PrototypesDto prototypesDto = prezService.makeOutline(request.getTopic(), id);
        PrototypeResponseDto responseDto = new PrototypeResponseDto(id, prototypesDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/prez/slides/{presentationId}")
    @Operation(summary = "슬라이드 생성")
    @ApiErrorCodeExample({
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.PRESENTATION_NOT_FOUND,
            ErrorCode.INVALID_VALUE})
    public ResponseEntity<SlidesDto> createSlides(@RequestBody PrototypesDto prototypesDto, @PathVariable("presentationId") Long id) {
        SlidesDto slidesDto = prezService.makeSlide(prototypesDto, id);
        return ResponseEntity.ok(slidesDto);
    }

    @PostMapping("/prez/script/{presentationId}")
    @Operation(summary = "대본 생성")
    public ResponseEntity<ScriptResponseDto> createScript(@RequestBody SlidesDto slidesDto, @PathVariable("presentationId") Long id) {
        String script = prezService.makeScript(slidesDto, id);
        return ResponseEntity.ok(new ScriptResponseDto(script));
    }
}
