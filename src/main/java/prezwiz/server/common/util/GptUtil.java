package prezwiz.server.common.util;

import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

/**
 * slide를 만들기 위해 필요한 json을 응답해줌
 */
public interface GptUtil {

    PrototypesDto getPrototypes(String topic);

    SlidesDto getSlides(PrototypesDto prototypes);

    String getScript(SlidesDto slides);
}
