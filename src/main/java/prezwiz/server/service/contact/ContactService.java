package prezwiz.server.service.contact;

import org.springframework.http.ResponseEntity;
import prezwiz.server.dto.request.ContactMessageRequestDto;
import prezwiz.server.dto.response.ResponseDto;

/**
 * client로부터 오는 message를 처리하는 service
 */
public interface ContactService {

    ResponseEntity<ResponseDto> handleMessage(String email, ContactMessageRequestDto request);

}
