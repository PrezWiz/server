package prezwiz.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  CONFLICT_EXIST_EMAIL(HttpStatus.CONFLICT, "2001", "이미 사용중인 이메일입니다"),

  INVALID_VALUE(HttpStatus.BAD_REQUEST, "4000", "잘못된 요청입니다."),
  AUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "4001", "지정한 리소스에 대한 엑세스 권한이 없다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "4004", "존재하지 않는 경로입니다."),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "internal server error"),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "파일 업로드 중 오류가 발생했습니다"),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "5002", "파일 삭제 중 오류가 발생했습니다");

  private final HttpStatus status;
  private final String code;
  private final String msg;
}
