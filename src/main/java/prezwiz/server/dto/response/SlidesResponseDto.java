package prezwiz.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

/**
 * slide의 각 구성과 함께 status와 message를 전송함
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlidesResponseDto {
    private String status;
    private String message;
    private PrototypesDto slideDto;
}
