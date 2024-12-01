package prezwiz.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import prezwiz.server.dto.slide.outline.OutlineDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlineResponseDto {

    @JsonProperty("id")
    private Long presentationId;
    private List<OutlineDto> outlines;
}
