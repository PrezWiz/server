package prezwiz.server.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.gptapi.request.GPTRequest;
import prezwiz.server.dto.slide.gptapi.request.Message;
import prezwiz.server.dto.slide.gptapi.response.GPTResponse;
import prezwiz.server.dto.slide.prototype.PrototypeDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GptUtilImpl implements GptUtil{

    private final ObjectMapper objectMapper;

    @Value("${ai.secret_key}")
    private String SECRET_KEY;
    private String URI = "/v1/chat/completions";

    @Override
    public PrototypesDto getPrototypes(String topic) {
        GPTRequest requestBody = makePrototypeRequestBody(topic);
        GPTResponse responseJson = getGptResponse(requestBody);
        return jsonToObject(responseJson, PrototypesDto.class);
    }

    @Override
    public SlidesDto getSlides(PrototypesDto prototypes) {
        GPTRequest slidesRequest = makeSlidesRequestDto(prototypes);
        GPTResponse slidesJson = getGptResponse(slidesRequest);
        return jsonToObject(slidesJson, SlidesDto.class);
    }

    @Override
    public String getScript(SlidesDto slides) {
        GPTRequest scriptRequest = makeScriptRequestBody(slides);
        GPTResponse scriptJson = getGptResponse(scriptRequest);
        return scriptJson.getChoices().get(0).getMessage().getContent();
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

    /**
     * 프로토타입을 가지고 슬라이드구성을 만들어 달라고 요청하기위한 객체를 생성
     */
    private GPTRequest makeSlidesRequestDto(PrototypesDto prototypes) {
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
                objectToJson(prototypes) + "\n다음을 바탕으로 발표자료를 만들어줘."
        );
        return new GPTRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));
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
                objectToJson(slidesDto.getSlides()) + "\n다음을 바탕으로 대본을 만들어줘");
        return new GPTRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));
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

    /**
     * json -> object
     */
    private <T> T jsonToObject(GPTResponse response, Class<T> valueType) {
        String json = response.getChoices().get(0).getMessage().getContent();
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parse problem", e);
        }
    }

    /**
     * object -> json
     */
    private <T> String objectToJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
