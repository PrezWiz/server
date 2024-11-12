package prezwiz.server.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@Slf4j
public class LocalPrezStorage implements PrezStorage {

    @Value("${ppt.file_path}")
    private String filePath;

    @Override
    public String saveSlide(XMLSlideShow slideShow) {
        String location = filePath + "/" + makeFileName("pptx");
        try (FileOutputStream out = new FileOutputStream(location)) {
            slideShow.write(out);
        } catch (IOException e) {
            throw new RuntimeException("PPT 파일 저장 중 오류가 발생했습니다", e);
        }
        return location;
    }

    @Override
    public String saveScript(String scriptContent) {
        String location = filePath + "/" + makeFileName("txt");
        try (FileWriter writer = new FileWriter(location)) {
            writer.write(scriptContent);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다", e);
        }
        return location;
    }

    private String makeFileName(String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime = LocalDateTime.now().format(formatter);
        return currentTime + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }

    @Override
    public File getScript(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("대본 불러오기 실패: LocalPrezStorage");
        }
        return file;
    }

    @Override
    public XMLSlideShow getSlide(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            return new XMLSlideShow(fis);
        } catch (IOException e) {
            throw new RuntimeException("슬라이드 불러오기 실패: LocalPrezStorage ",e);
        }
    }
}
