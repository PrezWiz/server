package prezwiz.server.dto.slide.prototype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrototypeDto {

    @JsonProperty("slide_number")
    private Long slideNumber;
    private String title;
    private String description;

}
