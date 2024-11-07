package prezwiz.server.dto.request.auth;

import lombok.Data;

/**
 * 회원가입을 위한 requestDto
 */
@Data
public class JoinRequestDto {

    private String email;
    private String password;
    private String role;
}
