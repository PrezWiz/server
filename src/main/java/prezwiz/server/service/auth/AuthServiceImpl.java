package prezwiz.server.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.common.exception.BizBaseException;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.CustomUserInfoDto;
import prezwiz.server.dto.request.auth.AuthDto;
import prezwiz.server.dto.request.auth.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.entity.Member;
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
            throw new BizBaseException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 탈퇴한 회원일 경우 로그인을 진행할수 없음 -> MemberNotFoundException;
        if (!member.isActive()) {
            throw new BizBaseException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 요청한 암호와 실제 암호가 같은지 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BizBaseException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        CustomUserInfoDto info = new CustomUserInfoDto(member.getId(), member.getEmail(), member.getPassword(), member.getRole());
        String accessToken = jwtUtil.createAccessToken(info);

        ResponseCookie responseCookie = ResponseCookie.from("authorization", accessToken)
                .path("/")
                .maxAge(60 * 60 * 24 * 30)
                .build();

        ResponseDto responseDto = new ResponseDto("success", "로그인에 성공하였습니다.");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(responseDto);
    }

    @Override
    public ResponseDto withdraw() {
        Member member = getCurrentUser();

        // 해당 member의 모든 presentation와의 연관관계를 끊음 -> 고아 엔티티 삭제
        member.getPresentations().clear();
        member.withdraw();
        return ResponseDto.ok();
    }

    @Override
    public ResponseDto modifyPassword(AuthDto.ModifyPasswordReq dto) {
        Member member = getCurrentUser();

        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())){
            throw new BizBaseException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        member.modifyPassword(passwordEncoder.encode(dto.getNewPassword()));
        return ResponseDto.ok();
    }

    /**
     * 현재 로그인 되어있는 유저를 가져오기 위한 private methode
     */
    private Member getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Member member = memberRepository.findMemberByEmail(currentUserEmail);
        if (member == null) {
            throw new BizBaseException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }
}
