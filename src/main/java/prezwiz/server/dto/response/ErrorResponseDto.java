package prezwiz.server.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Error가 발생하면 다음 responseDto의 형식을 따름
 */
@Getter
@Setter
public class ErrorResponseDto {

    private int status;
    private String message;
    private LocalDateTime time;

    public ErrorResponseDto(int status, String message, LocalDateTime time) {
        this.status = status;
        this.message = message;
        this.time = time;
    }

    @Override
    public String toString() {
        return "{" +
                "\"status\":" + status + "," +
                "\"message\":\"" + message + "\"," +
                "\"time\":\"" + time.toString() + "\"" +
                "}";
    }
}
