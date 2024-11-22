package prezwiz.server.dto.slide.outline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlineDto {

    @JsonProperty("outline_number")
    private Long outlineNumber;
    private String title;
    private String description;
}
