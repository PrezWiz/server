package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prezwiz.server.common.adapter.PrezServiceAdapter;
import prezwiz.server.common.annotation.ApiErrorCodeExample;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.request.CreateOutlineRequestDto;
import prezwiz.server.dto.response.OutlineResponseDto;
import prezwiz.server.dto.response.PresentationsResponseDto;
import prezwiz.server.dto.response.ScriptResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;

@Tag(name="slide V2", description="슬라이드 관련 controller V2")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SlideControllerV2 {

    private final PrezServiceAdapter prezServiceAdapter;

    @PostMapping("/slide-outline")
    @Operation(summary = "outline 생성",
            description =
                    "response json 에는 이후 요청을 위한 presentationId를 같이 반환합니다." +
                            "\n 이후에 slide를 생성하거나, script를 생성할때 url경로에 같이 보내줘야 합니다.")
    @ApiErrorCodeExample({ErrorCode.PRESENTATION_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND})
    public ResponseEntity<OutlineResponseDto> createOutline(@RequestBody CreateOutlineRequestDto request) {
        OutlineResponseDto outline = prezServiceAdapter.outline(request.getTopic());
        return ResponseEntity.ok(outline);
    }

    @PostMapping("/slides/{presentationId}")
    @Operation(summary = "슬라이드 생성")
    @ApiErrorCodeExample({
            ErrorCode.PRESENTATION_NOT_FOUND,
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.GPT_API_CLIENT_ERROR,
            ErrorCode.GPT_API_INTERNAL_ERROR,
            ErrorCode.AUTH_INVALID_ACCESS_TOKEN})
    public ResponseEntity<SlidesDto> createSlidesData(@RequestBody OutlinesDto outlinesDto, @PathVariable("presentationId") Long id) {
        SlidesDto slidesDto = prezServiceAdapter.slide(outlinesDto, id);
        return ResponseEntity.ok(slidesDto);
    }

    @GetMapping("/slides/{presentationId}")
    @Operation(summary = "슬라이드 가져오기")
    @ApiErrorCodeExample({ErrorCode.PRESENTATION_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND, ErrorCode.AUTH_INVALID_ACCESS_TOKEN})
    public ResponseEntity<SlidesDto> getSlidesData(@PathVariable("presentationId") Long id) {
        SlidesDto slides = prezServiceAdapter.getSlide(id);
        return ResponseEntity.ok(slides);
    }

    @PutMapping("/slides/{presentationId}")
    @Operation(summary = "슬라이드 수정")
    @ApiErrorCodeExample({ErrorCode.PRESENTATION_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND, ErrorCode.AUTH_INVALID_ACCESS_TOKEN})
    public void updateSlideData(@RequestBody SlidesDto slidesDto, @PathVariable("presentationId") Long id) {
        prezServiceAdapter.updateSlide(id, slidesDto);
    }

    @PostMapping("/script/{presentationId}")
    @Operation(summary = "대본 가져오기")
    @ApiErrorCodeExample({
            ErrorCode.PRESENTATION_NOT_FOUND,
            ErrorCode.MEMBER_NOT_FOUND,
            ErrorCode.GPT_API_CLIENT_ERROR,
            ErrorCode.GPT_API_INTERNAL_ERROR,
            ErrorCode.AUTH_INVALID_ACCESS_TOKEN})
    public ResponseEntity<ScriptResponseDto> createAndGetScript(@RequestBody SlidesDto slidesDto, @PathVariable("presentationId") Long id) {
        ScriptResponseDto scriptResponse = prezServiceAdapter.getScript(slidesDto, id);
        return ResponseEntity.ok(scriptResponse);
    }

    @GetMapping("/store")
    @Operation(summary = "모든 프레젠테이션 정보 가져오기")
    @ApiErrorCodeExample({ErrorCode.MEMBER_NOT_FOUND})
    public ResponseEntity<PresentationsResponseDto> getPresentation() {
        PresentationsResponseDto presentations = prezServiceAdapter.getSlides();
        return ResponseEntity.ok(presentations);
    }

    @DeleteMapping("/store/{presentationId}")
    @Operation(summary = "프레젠테이션 지우기")
    @ApiErrorCodeExample({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.AUTH_INVALID_ACCESS_TOKEN, ErrorCode.PRESENTATION_NOT_FOUND})
    public void deletePresentation(@PathVariable("presentationId") Long id) {
        prezServiceAdapter.deletePrez(id);
    }
}
