package prezwiz.server.dto.slide.outline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlinesDto {
    private List<OutlineDto> outlines;
}
