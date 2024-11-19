package prezwiz.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BizBaseException extends RuntimeException {
    private HttpStatus status;
    private String code;
    private String msg;
    private LocalDateTime timestamp = LocalDateTime.now();

    public BizBaseException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }
}
