package prezwiz.server.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import prezwiz.server.dto.slide.SlideDto;

import java.util.List;

/**
 * 발표자료를 생성하기 위한 객체
 */
public interface SlideUtil {

    XMLSlideShow makeSlide(List<SlideDto> slides);

}
