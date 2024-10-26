package prezwiz.server.util;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;
import prezwiz.server.dto.slide.SlideDto;

import java.util.List;

@Component
public class NormalSlideUtil implements SlideUtil {

    @Override
    public XMLSlideShow makeSlide(List<SlideDto> slides) {
        XMLSlideShow ppt = new XMLSlideShow();
        for (SlideDto slide : slides) {
            createSlide(ppt, slide.getTitle(), slide.getContent());
        }
        return ppt;
    }

    private void createSlide(XMLSlideShow ppt, String title, String content) {
        XSLFSlide slide = ppt.createSlide();
        XSLFTextBox titleBox = slide.createTextBox();
        titleBox.setAnchor(new java.awt.Rectangle(50, 50, 500, 50));
        XSLFTextParagraph titleParagraph = titleBox.addNewTextParagraph();
        XSLFTextRun titleRun = titleParagraph.addNewTextRun();
        titleRun.setText(title);
        titleRun.setFontSize(24.0);

        XSLFTextBox contentBox = slide.createTextBox();
        contentBox.setAnchor(new java.awt.Rectangle(50, 120, 500, 400));
        XSLFTextParagraph contentParagraph = contentBox.addNewTextParagraph();
        XSLFTextRun contentRun = contentParagraph.addNewTextRun();
        contentRun.setText(content);
        contentRun.setFontSize(18.0);
    }

}
