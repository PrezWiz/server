package prezwiz.server.service.auth;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.dto.CustomUserInfoDto;
import prezwiz.server.dto.request.auth.AuthDto;
import prezwiz.server.dto.request.auth.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.entity.Member;
import prezwiz.server.exception.MemberNotFoundException;
import prezwiz.server.repository.MemberRepository;
import prezwiz.server.security.JwtUtil;

/**
 * 로그인을 위한 service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ResponseDto> login(LoginRequestDto dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findMemberByEmail(email);

        // 요청한 이메일과 같은 유저가 존재하는지 확인
        if (member == null) {
            ResponseDto responseDto = new ResponseDto("fail", "유저를 찾을수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }

        // 요청한 암호와 실제 암호가 같은지 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            ResponseDto responseDto = new ResponseDto("fail", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }

        CustomUserInfoDto info = new CustomUserInfoDto(member.getId(), member.getEmail(), member.getPassword(), member.getRole());
        String accessToken = jwtUtil.createAccessToken(info);

        ResponseCookie responseCookie = ResponseCookie.from("authorization", accessToken)
                .path("/")
                .httpOnly(true)
                .maxAge(60 * 24 * 3)
                .build();

        ResponseDto responseDto = new ResponseDto("success", "로그인에 성공하였습니다.");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(responseDto);
    }

    @Override
    public ResponseDto withdraw(String email) {
        Member member = memberRepository.findMemberByEmail(email);

        if (member == null){
            throw new MemberNotFoundException();
        }

        member.withdraw();
        return ResponseDto.ok();
    }

    @Override
    public ResponseDto modifyPassword(String email, AuthDto.ModifyPasswordReq dto) {
        Member member = memberRepository.findMemberByEmail(email);

        if (member == null){
            throw new MemberNotFoundException();
        }


        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())){
            throw new IllegalArgumentException("password not matched");
        }

        member.modifyPassword(passwordEncoder.encode(dto.getNewPassword()));
        return ResponseDto.ok();
    }
}
