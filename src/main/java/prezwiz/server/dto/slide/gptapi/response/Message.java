package prezwiz.server.dto.slide.gptapi.response;

import lombok.Data;

@Data
public class Message {

    private String role;
    private String content;
}
