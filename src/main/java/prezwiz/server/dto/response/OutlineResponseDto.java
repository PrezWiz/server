package prezwiz.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import prezwiz.server.dto.slide.outline.OutlineDto;

import java.util.List;

@Data
public class OutlineResponseDto {

    @JsonProperty("id")
    private Long presentationId;
    private List<OutlineDto> outlines;
}
