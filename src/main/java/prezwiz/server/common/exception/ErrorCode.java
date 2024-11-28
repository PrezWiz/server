package prezwiz.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  CONFLICT_EXIST_EMAIL(HttpStatus.CONFLICT, "2001", "이미 사용중인 이메일입니다"),

  INVALID_VALUE(HttpStatus.BAD_REQUEST, "4000", "잘못된 요청입니다."),
  AUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "4001", "지정한 리소스에 대한 엑세스 권한이 없습니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "4004", "존재하지 않는 경로입니다."),
  MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "4005", "존재하지 않는 유저입니다."),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "4006", "비밀번호가 일치하지 않습니다."),
  PRESENTATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "4008", "존재하지 않는 presentation입니다."),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "internal server error");

  private final HttpStatus status;
  private final String code;
  private final String msg;
}
