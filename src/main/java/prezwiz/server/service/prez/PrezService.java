package prezwiz.server.service.prez;

import prezwiz.server.dto.request.SlideRequestDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

public interface PrezService {
    PrototypesDto makePrototypes(String topic);
    SlidesDto makeSlides(String email, SlideRequestDto requestDto);
    String makeScript(String email, SlidesDto slidesDto, Long presentationId);
}
