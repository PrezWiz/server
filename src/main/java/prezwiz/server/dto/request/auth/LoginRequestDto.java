package prezwiz.server.dto.request.auth;

import lombok.Data;

/**
 * login을 위한 requestDto
 */
@Data
public class LoginRequestDto {

    private String email;
    private String password;

}
