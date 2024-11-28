package prezwiz.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  // 1000~1999 : 공통적으로 일어나는 에러
  INVALID_VALUE(HttpStatus.BAD_REQUEST, "1001", "잘못된 요청입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "1002", "존재하지 않는 경로입니다."),

  // 2000~2999 : 회원가입, 로그인 관련
  CONFLICT_EXIST_EMAIL(HttpStatus.CONFLICT, "2001", "이미 사용중인 이메일입니다"),
  MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "2002", "존재하지 않는 유저입니다."),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "2003", "비밀번호가 일치하지 않습니다."),
  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "2004", "이메일 형식이 올바르지 않습니다."),
  INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "2005", "비밀번호는 최소 8자리 이상, 영어 대문자,소문자,숫자,특수문자를 포함해주세요"),
  EMPTY_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "2006", "이메일 또는 비밀번호가 비어 있습니다."),

  // 3000~3999 : presentation 관련
  AUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "3001", "지정한 리소스에 대한 엑세스 권한이 없습니다."),
  PRESENTATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "3002", "존재하지 않는 presentation입니다."),

  // 5000~5999 : 서버에서 일어나는 오류 ( httpStatus 5xx )
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "internal server error"),
  GPT_API_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "gpt api와 통신중 오류가 발생했습니다. 5xx"),
  GPT_API_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5002", "gpt api와 통신중 오류가 발생했습니다. 4xx");

  private final HttpStatus status;
  private final String code;
  private final String msg;
}
