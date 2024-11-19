package prezwiz.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrototypeResponseDto {
    private Long presentationId;
    private PrototypesDto prototypesDto;
}
