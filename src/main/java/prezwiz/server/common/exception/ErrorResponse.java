package prezwiz.server.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Getter
@ToString
public class ErrorResponse<T> {

  private final LocalDateTime timestamp = LocalDateTime.now();
  private final String path = getCurrentPath();
  private final String message;
  private final String code;
  private final T errors;

  public ErrorResponse(String message, String code) {
    this(message, code, null);
  }

  public ErrorResponse(String message, String code, T errors) {
    this.message = message;
    this.code = code;
    this.errors = errors;
  }

  public static String getCurrentPath() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
      return request.getRequestURI();
    }
    return null;
  }
}
