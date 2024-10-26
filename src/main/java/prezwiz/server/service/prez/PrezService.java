package prezwiz.server.service.prez;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import prezwiz.server.dto.request.CreateRequestDto;
import prezwiz.server.dto.response.PresentationResponseDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

import java.util.List;

public interface PrezService {

    List<PresentationResponseDto> getAllPrez(String email);
    PresentationResponseDto getPrez(String email, Long id);
    PrototypesDto makePrototype(String topic);
    PresentationResponseDto makePrez(CreateRequestDto requestDto, String email);
    ResponseEntity<InputStreamResource> getScript(String email, Long presentationId);
    ResponseEntity<ByteArrayResource> getSlide(String email, Long presentationId);

}
