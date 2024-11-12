package prezwiz.server.common.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

import java.io.File;

/**
 * 발표자료(PPT, Script)를 가져오고 저장하기 위한 객체
 */
public interface PrezStorage {

    String saveSlide(XMLSlideShow slideShow);
    String saveScript(String scriptContent);
    File getScript(String path);
    XMLSlideShow getSlide(String path);

}
