package prezwiz.server.service.prez;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrezServiceTest {

    @Autowired
    PrezService prezService;

    @Test
    @DisplayName("전반 테스트")
    void crTest() {
    }

}