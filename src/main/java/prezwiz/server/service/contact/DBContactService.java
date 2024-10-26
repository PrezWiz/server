package prezwiz.server.service.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.dto.request.ContactMessageRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.entity.Contact;
import prezwiz.server.entity.Member;
import prezwiz.server.repository.ContactRepository;
import prezwiz.server.repository.MemberRepository;

import java.time.LocalDateTime;

/**
 *  message를 DB에 저장하여 처리하는 방법, 이후에 변경 가능성 있음
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DBContactService implements ContactService {

    private final MemberRepository memberRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public ResponseEntity<ResponseDto> handleMessage(String email, ContactMessageRequestDto request) {
        String message = request.getMessage();
        Member findMember = memberRepository.findMemberByEmail(email);
        Contact contact = new Contact(message, findMember, LocalDateTime.now());
        contactRepository.save(contact);

        ResponseDto responseDto = new ResponseDto("success", "메세지를 전송했습니다. 확인후 등록된 이메일로 회신하겠습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
