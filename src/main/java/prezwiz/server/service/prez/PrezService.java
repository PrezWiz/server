package prezwiz.server.service.prez;

import prezwiz.server.dto.response.PresentationsResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;

public interface PrezService {

    Long makeTable();

    // Create
    OutlinesDto makeOutline(String topic, Long presentationId);
    SlidesDto makeSlide(OutlinesDto prototypesDto, Long presentationId);
    String makeScript(SlidesDto slidesDto, Long presentationId);

    // Read
    SlidesDto getSlide(Long presentationId);
    String getScript(Long presentationId);
    PresentationsResponseDto getPresentations();

    // Update
    void updateSlide(Long presentationId, SlidesDto slidesDto);
    void updateScript(Long presentationId, String script);

    // Delete
    void deletePrez(Long presentationId);

}
