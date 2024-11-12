package prezwiz.server.service.prez;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import prezwiz.server.common.annotation.ExeTimer;
import prezwiz.server.dto.request.CreateRequestDto;
import prezwiz.server.dto.response.PresentationResponseDto;
import prezwiz.server.dto.slide.SlideDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypeDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.dto.slide.gptapi.request.GPTRequest;
import prezwiz.server.dto.slide.gptapi.request.Message;
import prezwiz.server.dto.slide.gptapi.response.GPTResponse;
import prezwiz.server.entity.Member;
import prezwiz.server.entity.Presentation;
import prezwiz.server.repository.MemberRepository;
import prezwiz.server.repository.PresentationRepository;
import prezwiz.server.common.util.PrezStorage;
import prezwiz.server.common.util.SlideUtil;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GPTPrezService implements PrezService {

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final PresentationRepository presentationRepository;
    private final SlideUtil slideUtil;
    private final PrezStorage prezStorage;

    @Value("${ai.secret_key}")
    private String SECRET_KEY;
    private String URI = "/v1/chat/completions";

    @Override
    public List<PresentationResponseDto> getAllPrez(String email) {
        Member member = memberRepository.findMemberByEmail(email);
        return member.getPresentations().stream().map(
                presentation -> new PresentationResponseDto(
                        presentation.getId(),
                        presentation.getTopic(),
                        presentation.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    public PresentationResponseDto getPrez(String email, Long presentationId) {
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        if (presentationOptional.isEmpty()) {
            throw new RuntimeException("본인의 presentation만 접근할수 있습니다.");
        }
        Presentation prez = presentationOptional.get();
        return new PresentationResponseDto(prez.getId(), prez.getTopic(), prez.getCreatedAt());
    }

    @ExeTimer
    @Override
    public PrototypesDto makePrototype(String topic) {
        GPTRequest requestBody = makePrototypeRequestBody(topic);
        GPTResponse response = getGptResponse(requestBody);
        return jsonToPrototypesDto(response);
    }

    /**
     * chat gpt api 에게 슬라이드 프로토타입 구성을 만들어 달라고 요청하기 위한 requestBody를 생성함
     */
    private GPTRequest makePrototypeRequestBody(String topic) {
        Message systemMessage = new Message(
                "system",
                "JSON 형식은 반드시 아래 형식을 따르세요:\n" +
                        "{\n" +
                        "  \"slides\": [\n" +
                        "    {\"slide_number\": 1, \"title\": \"PPT 제목\", \"description\": \"프레젠테이션의 전체적인 주제 설명\"},\n" +
                        "    {\"slide_number\": 2, \"title\": \"목차\", \"description\": \"\"},\n" +
                        "    {\"slide_number\": 3, \"title\": \"슬라이드 제목\", \"description\": \"슬라이드에 대한 간단한 설명\"}\n" +
                        "  ]\n" +
                        "}\n" +
                        "첫 번째 슬라이드는 PPT 제목을 포함하고, 두 번째 슬라이드는 목차로 구성하되, " +
                        "목차 슬라이드의 description은 비워 두세요. 이후의 슬라이드 구성은 주제를 기반으로 만들어주세요."
        );

        Message userMessage = new Message(
                "user",
                topic + "에 대한 PPT를 만들건데, 각 슬라이드 구성을 만들어줘"
        );
        return new GPTRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));
    }

    @ExeTimer
    @Override
    @Transactional
    public PresentationResponseDto makePrez(CreateRequestDto requestDto, String email) {
        // slidesDto 생성
        GPTRequest slidesRequestBody = makeSlidesRequestDto(requestDto.getSlides());
        GPTResponse slidesResponse = getGptResponse(slidesRequestBody);
        SlidesDto slidesDto = slidesJsonToSlides(slidesResponse);

        // scriptContent 생성
        GPTRequest scriptRequestBody = makeScriptRequestBody(slidesDto);
        GPTResponse scriptResponse = getGptResponse(scriptRequestBody);
        String scriptContent = scriptResponse.getChoices().get(0).getMessage().getContent();

        // 슬라이드 생성
        XMLSlideShow xmlSlideShow = slideUtil.makeSlide(slidesDto.getSlides());

        // 슬라이드,대본 저장
        String pttLocation = prezStorage.saveSlide(xmlSlideShow);
        String scriptLocation = prezStorage.saveScript(scriptContent);

        // 슬라이드 정보 테이블에 저장
        String topic = requestDto.getTopic();
        Member member = memberRepository.findMemberByEmail(email);
        Presentation presentation = new Presentation(topic, pttLocation, scriptLocation);
        member.addPresentation(presentation);
        presentationRepository.save(presentation);

        // 슬라이드 정보 반환
        return new PresentationResponseDto(presentation.getId(), presentation.getTopic(), presentation.getCreatedAt());
    }

    /**
     * gpt api 에게 String으로 받은 결과를 slidePrototype으로 mapping
     */
    private PrototypesDto jsonToPrototypesDto(GPTResponse response) {
        String prototypesJson = response.getChoices().get(0).getMessage().getContent();
        try {
            return objectMapper.readValue(prototypesJson, PrototypesDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parse problem", e);
        }
    }

    /**
     * 슬라이드 구성(String) -> 슬라이드 구성 객체(Slides);
     */
    private SlidesDto slidesJsonToSlides(GPTResponse response) {
        String slidesJsonString = response.getChoices().get(0).getMessage().getContent();
        try {
            return objectMapper.readValue(slidesJsonString, SlidesDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parse problem", e);
        }
    }

    /**
     * 프로토타입을 가지고 슬라이드구성을 만들어 달라고 요청하기위한 객체를 생성
     */
    private GPTRequest makeSlidesRequestDto(List<PrototypeDto> prototypes) {
        Message systemMessage = new Message(
                "system",
                "JSON 형식은 반드시 다음을 따르세요: {\n" +
                        "  \"slides\": [\n" +
                        "    {\"title\": \"슬라이드 1 제목\", \"content\": \"슬라이드 1 내용이 여기에 들어갑니다.\"},\n" +
                        "    {\"title\": \"슬라이드 2 제목\", \"content\": \"슬라이드 2 내용이 여기에 들어갑니다.\"}\n" +
                        "  ]\n" +
                        "}\n" +
                        "사용자가 주는 정보를 바탕으로 PPT의 각 슬라이드의 제목과 내용을 'title'과 'content'에 채워 넣으세요. " +
                        "대상 청중은 대학교 2학년 수준의 청중입니다. 각 슬라이드의 내용을 구체적이고 학문적인 어조로 작성하세요."
        );
        Message userMessage = new Message(
                "user",
                prototypeToJson(prototypes) + "\n다음을 바탕으로 발표자료를 만들어줘."
        );
        return new GPTRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));
    }


    /**
     * 프로토타입객체 -> Json
     */
    private String prototypeToJson(List<PrototypeDto> slides) {
        try {
            return objectMapper.writeValueAsString(slides);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 슬라이드 구성을 가지고 발표 자료를 만들어 달라고 요청하기 위한 객체 생성
     */
    private GPTRequest makeScriptRequestBody(SlidesDto slidesDto) {
        Message systemMessage = new Message("system",
                "사용자가 PPT에 대한 정보를 주면, 그를 바탕으로 대본을 작성하세요" +
                        "각 슬라이드에 맞는 설명이 있어야 합니다." +
                        "응답에는 발표대본만을 포함하세요.");
        Message userMessage = new Message("user",
                slidesToJson(slidesDto.getSlides()) + "\n다음을 바탕으로 대본을 만들어줘");
        return new GPTRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));
    }

    /**
     * slides 객체 -> Json
     */
    private String slidesToJson(List<SlideDto> slides) {
        try {
            return objectMapper.writeValueAsString(slides);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * GPT에게 결과 요청
     * param : GPTRequest
     * return: GTPResponse
     */
    private GPTResponse getGptResponse(GPTRequest request) {
        return WebClient.create("https://api.openai.com").post()
                .uri(URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + SECRET_KEY) // GPT SECRET_KEY
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                .body(Mono.just(request), GPTRequest.class)
                .retrieve()
                .bodyToMono(GPTResponse.class)
                .block();
    }

    @Override
    public ResponseEntity<InputStreamResource> getScript(String email, Long presentationId) {
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        if (presentationOptional.isEmpty()) {
            throw new RuntimeException("존재하지 않는 presentation 입니다.");
        }

        Presentation presentation = presentationOptional.get();

        if (!presentation.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인의 presentation만 접근할수 있습니다.");
        }

        String scriptLocation = presentation.getScriptLocation();
        File file = prezStorage.getScript(scriptLocation);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=script.txt");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> getSlide(String email, Long presentationId) {
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        if (presentationOptional.isEmpty()) {
            throw new RuntimeException("존재하지 않는 presentation 입니다.");
        }

        Presentation presentation = presentationOptional.get();

        if (!presentation.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인의 presentation만 접근할 수 있습니다.");
        }

        String slideLocation = presentation.getPptLocation();
        XMLSlideShow slide = prezStorage.getSlide(slideLocation);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 슬라이드 데이터를 ByteArray로 변환
            slide.write(outputStream);
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            // HTTP 헤더 설정 및 파일 응답
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=slide.pptx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("PPT 파일 생성 중 오류가 발생했습니다.", e);
        }
    }
}
