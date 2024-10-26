package prezwiz.server.dto.slide.gptapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    private Long index;
    private Message message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
