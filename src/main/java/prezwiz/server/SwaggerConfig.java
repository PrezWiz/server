package prezwiz.server;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import prezwiz.server.common.annotation.ApiErrorCodeExample;
import prezwiz.server.common.exception.BizBaseException;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.common.exception.ErrorResponse;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("PrezWiz API DOCUMENT")
                        .description("description ... ")
                        .version("1.0").contact(new Contact().name("kim jun young")
                                .email("wnsdud4949@naver.com").url("www.prezwiz.com"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);

            ApiResponses responses = operation.getResponses();
            if (apiErrorCodeExample != null) {
                addErrorCodeExamplesToResponses(responses, apiErrorCodeExample.value());
            }

            return operation;
        };
    }

    private void addErrorCodeExamplesToResponses(ApiResponses responses, ErrorCode[] errorCodes) {
        ApiResponse apiResponse = new ApiResponse();
        Content content = new Content();
        MediaType mediaType = new MediaType();

        for (ErrorCode errorCode : errorCodes)
            mediaType.addExamples(errorCode.getCode(), makeExample(errorCode));

        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);
        responses.addApiResponse("error", apiResponse);
    }

    private Example makeExample(ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getMsg(), errorCode.getCode());
        Example example = new Example();
        example.description(errorCode.getMsg());
        example.setValue(errorResponse);
        return example;
    }
}
