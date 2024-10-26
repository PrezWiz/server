package prezwiz.server.dto.request;

import lombok.Data;

/**
 * slide구성을 만들어달라고 하기위한 requestDto
 */
@Data
public class CreateContentsRequestDto {

    private String topic;
}
