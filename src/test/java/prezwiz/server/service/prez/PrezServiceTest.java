package prezwiz.server.service.prez;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import prezwiz.server.dto.request.CreateRequestDto;
import prezwiz.server.dto.request.auth.JoinRequestDto;
import prezwiz.server.dto.response.PresentationResponseDto;
import prezwiz.server.dto.slide.prototype.PrototypeDto;
import prezwiz.server.service.auth.MemberService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrezServiceTest {

    @Autowired
    PrezService prezService;
    @Autowired
    MemberService memberService;

    private final String TEST_EMAIL = "testEmail@test.com";

    @BeforeEach
    void beforeEach() {
        // 모든 테스트를 시작하기 전에 멤버를 만들어서 저장함
        JoinRequestDto requestDto = new JoinRequestDto();
        requestDto.setEmail(TEST_EMAIL);
        requestDto.setPassword("test1234");
        requestDto.setRole("user");
        memberService.saveMember(requestDto);
    }

    @Test
    @DisplayName("makePrez()")
    void makePrez() {
        // 요청을 위한 dto 생성
        List<PrototypeDto> prototypeDtoList = new ArrayList<>();
        prototypeDtoList.add(makePrototype(1L, "객체지향 프로그래밍", "객체지향 프로그래밍의 기본 개념과 원리를 설명하는 프레젠테이션"));
        prototypeDtoList.add(makePrototype(2L, "목차", ""));
        prototypeDtoList.add(makePrototype(3L, "객체지향 프로그래밍이란?", "객체지향 프로그래밍(OOP)의 정의와 중요성에 대해 설명합니다."));
        prototypeDtoList.add(makePrototype(4L, "OOP의 기본 개념", "OOP의 네 가지 주요 개념인 클래스, 객체, 상속, 다형성에 대해 설명합니다."));
        prototypeDtoList.add(makePrototype(5L, "클래스와 객체", "클래스와 객체의 차이점과 그 관계에 대해 알아봅니다."));
        prototypeDtoList.add(makePrototype(6L, "상속", "상속의 개념과 이를 통해 코드 재사용이 어떻게 이루어지는지를 설명합니다."));
        prototypeDtoList.add(makePrototype(7L, "다형성", "다형성이란 무엇인지, 어떻게 사용되는지를 예제를 통해 설명합니다."));
        prototypeDtoList.add(makePrototype(8L, "캡슐화", "캡슐화의 개념과 데이터 보호의 중요성에 대해 설명합니다."));
        prototypeDtoList.add(makePrototype(9L, "OOP의 장점", "객체지향 프로그래밍의 장점, 즉 코드의 유지보수성과 확장성에 대해 논의합니다."));
        prototypeDtoList.add(makePrototype(10L, "결론", "객체지향 프로그래밍의 중요성과 이점에 대한 요약을 제공합니다."));
        CreateRequestDto requestDto = new CreateRequestDto();
        requestDto.setTopic("객체지향");
        requestDto.setSlides(prototypeDtoList);

        // 요청후에 presentationId를 받아옴
        Long presentationId = prezService.makePrez(requestDto, TEST_EMAIL).getId();

        // prez테이블이 존재하면 성공으로 간주함
        PresentationResponseDto prez = prezService.getPrez(TEST_EMAIL, presentationId);
        Assertions.assertThat(prez).isNotNull();

        // 저장된 ppt 삭제?
    }

    private PrototypeDto makePrototype(Long slideNumber, String title, String description) {
        PrototypeDto prototypeDto = new PrototypeDto();
        prototypeDto.setSlideNumber(slideNumber);
        prototypeDto.setTitle(title);
        prototypeDto.setDescription(description);
        return prototypeDto;
    }

}