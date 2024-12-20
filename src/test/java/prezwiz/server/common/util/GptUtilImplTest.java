package prezwiz.server.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;

@SpringBootTest
@Slf4j
class GptUtilImplTest {

    @Autowired
    GptUtil gptUtil;
    StopWatch stopWatch;

    @BeforeEach
    public void beforeEach() {
        stopWatch = new StopWatch();
    }

    @Test
    @DisplayName("gptUtil 전반 테스트")
    void test() {

        stopWatch.start("prototype");
        OutlinesDto outlinesDto = gptUtil.getOutlines("객체지향");
        log.info("prototypes: {}", outlinesDto);
        stopWatch.stop();

        stopWatch.start("slides");
        SlidesDto slides = gptUtil.getSlides(outlinesDto);
        log.info("slides: {}", slides);
        stopWatch.stop();

        stopWatch.start("script");
        String script = gptUtil.getScript(slides);
        log.info("script: {}", script);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }
}