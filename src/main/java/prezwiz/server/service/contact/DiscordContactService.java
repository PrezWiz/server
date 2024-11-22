package prezwiz.server.service.contact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import prezwiz.server.dto.discord.DiscordRequestDto;
import prezwiz.server.dto.request.ContactMessageRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.entity.Contact;
import prezwiz.server.entity.Member;
import prezwiz.server.repository.ContactRepository;
import prezwiz.server.repository.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordContactService implements ContactService {

    private final MemberRepository memberRepository;
    private final ContactRepository contactRepository;

    @Value("${web_hook.contact}")
    private final String WEBHOOK_URI;

    @Override
    @Transactional
    public ResponseEntity<ResponseDto> handleMessage(String email, ContactMessageRequestDto request) {
        String message = request.getMessage();

        DiscordRequestDto discordRequest = new DiscordRequestDto();
        discordRequest.setContent(
                "email : " + email + "\n" +
                "message : " + message);

        WebClient.create().post()
                .uri(WEBHOOK_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(discordRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Member findMember = memberRepository.findMemberByEmail(email);
        Contact contact = new Contact(message, findMember, LocalDateTime.now());
        contactRepository.save(contact);

        ResponseDto responseDto = new ResponseDto("success", "메세지를 전송했습니다. 확인후 등록된 이메일로 회신하겠습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
