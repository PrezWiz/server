package prezwiz.server.common.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prezwiz.server.dto.response.OutlineResponseDto;
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.response.ScriptResponseDto;
import prezwiz.server.dto.slide.SlideDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlineDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;
import prezwiz.server.dto.slide.prototype.PrototypeDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.service.prez.PrezService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PrezServiceAdapter {

    private final PrezService prezService;

    public OutlineResponseDto outline(String topic) {

        // table 생성
        Long presentationId = prezService.makeTable();


        PrototypeResponseDto prototypeResponseDto = prezService.makeOutline(topic, presentationId);
        PrototypesDto prototypesDto = prototypeResponseDto.getPrototypesDto();
        OutlineResponseDto outlineResponseDto = new OutlineResponseDto();
        outlineResponseDto.setPresentationId(presentationId);
        List<OutlineDto> outlines = outlineResponseDto.getOutlines();
        prototypesDto.getSlides().forEach(
                prototypeDto -> {
                    outlines.add(new OutlineDto(prototypeDto.getSlideNumber(), prototypeDto.getTitle(), prototypeDto.getTitle()));
                }
        );

        return outlineResponseDto;
    }

    public SlidesDto slide(OutlinesDto outlinesDto, Long presentationId) {

        PrototypesDto prototypesDto = new PrototypesDto();
        List<PrototypeDto> prototypes = prototypesDto.getSlides();

        List<OutlineDto> outlines = outlinesDto.getOutlines();
        outlines.forEach(outline -> {
            prototypes.add(new PrototypeDto(outline.getOutlineNumber(), outline.getTitle(), outline.getDescription()));
        });
        return prezService.makeSlide(prototypesDto, presentationId);
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
}
