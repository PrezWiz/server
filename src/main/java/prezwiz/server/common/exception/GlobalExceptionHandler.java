package prezwiz.server.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

//  private final SlackNotificator slackNotificator;

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleException(Exception e) {
    log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e),
        e.getMessage());

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    ErrorResponse errorResponse = new ErrorResponse(
        errorCode.getMsg(),
        errorCode.getCode()
    );
    return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity handleClientException(IllegalArgumentException e) {
    log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e),
        e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        e.getMessage(),
        "x400"
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(BizBaseException.class)
  public ResponseEntity handleBizException(BizBaseException e) {
    log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e),
        e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        e.getMsg(),
        e.getCode()
    );
    return ResponseEntity.status(e.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e),
        e.getMessage());

    Map<String, String> fieldErrorMap = e.getBindingResult().getFieldErrors()
        .stream()
        .collect(
            Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() == null ? "" : fe.getDefaultMessage()
            )
        );

    ErrorResponse errorResponse = new ErrorResponse(
        ErrorCode.INVALID_VALUE.getMsg(),
        ErrorCode.INVALID_VALUE.getCode(),
        fieldErrorMap
    );

    return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
  }
}
