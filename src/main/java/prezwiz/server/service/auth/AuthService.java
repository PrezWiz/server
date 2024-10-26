package prezwiz.server.service.auth;

import org.springframework.http.ResponseEntity;
import prezwiz.server.dto.request.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;

/**
 * login 처리를 위한 service
 */
public interface AuthService {
    ResponseEntity<ResponseDto> login(LoginRequestDto request);
}
