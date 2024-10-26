package prezwiz.server.dto.request;

import lombok.Data;
import prezwiz.server.dto.slide.prototype.PrototypeDto;

import java.util.List;

/**
 * ppt와 대본을 만들어달라는 요청을 받기위한 requestDto
 */
@Data
public class CreateRequestDto {

    private String topic;
    private List<PrototypeDto> slides;

}
