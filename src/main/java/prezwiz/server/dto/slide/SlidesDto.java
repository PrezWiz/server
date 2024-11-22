package prezwiz.server.dto.slide;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlidesDto {
    private List<SlideDto> slides;
}
