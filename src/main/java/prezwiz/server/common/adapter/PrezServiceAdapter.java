package prezwiz.server.common.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prezwiz.server.dto.response.PresentationsResponseDto;
import prezwiz.server.dto.response.OutlineResponseDto;
import prezwiz.server.dto.response.ScriptResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlineDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;
import prezwiz.server.dto.slide.prototype.PrototypeDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.service.prez.PrezService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PrezServiceAdapter {

    private final PrezService prezService;

    public OutlineResponseDto outline(String topic) {

        // table 생성
        Long presentationId = prezService.makeTable();
        // outline 생성
        OutlinesDto outlinesDto = prezService.makeOutline(topic, presentationId);

        return new OutlineResponseDto(presentationId, outlinesDto.getOutlines());
    }

    public SlidesDto slide(OutlinesDto outlinesDto, Long presentationId) {
        return prezService.makeSlide(outlinesDto, presentationId);
    }

    public void updateSlide(Long id, SlidesDto slides) {
        prezService.updateSlide(id, slides);
    }


    public ScriptResponseDto getScript(SlidesDto slidesDto, Long id) {
        String script = prezService.makeScript(slidesDto, id);
        return new ScriptResponseDto(script);
    }

    public SlidesDto getSlide(Long id) {
        return prezService.getSlide(id);
    }

    public PresentationsResponseDto getSlides() {
        return prezService.getPresentations();
    }
}
