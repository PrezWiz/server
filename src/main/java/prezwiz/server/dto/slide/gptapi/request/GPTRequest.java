package prezwiz.server.dto.slide.gptapi.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GPTRequest {

    private String model;
    private List<Message> messages;

}
