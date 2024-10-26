package prezwiz.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresentationResponseDto {

    private Long id;
    private String topic;
    private LocalDateTime createdAt;

}
