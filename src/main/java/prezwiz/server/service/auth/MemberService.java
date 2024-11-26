package prezwiz.server.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.common.exception.BizBaseException;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.request.auth.JoinRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.entity.Member;
import prezwiz.server.repository.MemberRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    private boolean isNull(String email, String password) {
        return email == null || password == null;
    }

    private boolean isEmailDuplicate(String email) {
        return memberRepository.findMemberByEmail(email) != null;
    }

    // 회원가입
    @Transactional
    public ResponseEntity<ResponseDto> saveMember(JoinRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();
        String role = requestDto.getRole();

        if (isNull(email, password)) {
            log.error("잘못된 요청");
            throw new BizBaseException(ErrorCode.INVALID_VALUE);
        } else if (isEmailDuplicate(email)) {
            log.error("중복 이메일");
            throw new BizBaseException(ErrorCode.INVALID_VALUE);
        } else {
            String memberRole = role == null ? "user" : role;
            Member member = new Member(email, encoder.encode(password), memberRole);
            memberRepository.save(member);
            ResponseDto responseDto = new ResponseDto("success", "계정이 생성되었습니다. 로그인을 진행해주세요");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
    }

}
