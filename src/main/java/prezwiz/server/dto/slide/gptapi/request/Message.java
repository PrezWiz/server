package prezwiz.server.dto.slide.gptapi.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Message {

    private String role;
    private String content;

}
