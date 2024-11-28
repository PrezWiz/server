package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import prezwiz.server.common.annotation.ApiErrorCodeExample;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.request.auth.AuthDto;
import prezwiz.server.dto.request.auth.JoinRequestDto;
import prezwiz.server.dto.request.auth.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.auth.AuthService;
import prezwiz.server.service.auth.KaKaoAuthService;
import prezwiz.server.service.auth.MemberService;

@Tag(name="member", description = "member 관련 controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final AuthService authService;
    private final KaKaoAuthService kaKaoAuthService;
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/member")
    @Operation(summary = "회원가입")
    @ApiErrorCodeExample({
            ErrorCode.CONFLICT_EXIST_EMAIL,
            ErrorCode.EMPTY_EMAIL_OR_PASSWORD,
            ErrorCode.INVALID_EMAIL_FORMAT,
            ErrorCode.INVALID_PASSWORD_FORMAT})
    public ResponseEntity<ResponseDto> join(@RequestBody JoinRequestDto request) {
        return memberService.saveMember(request);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiErrorCodeExample({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.PASSWORD_NOT_MATCH})
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/member")
    @Operation(summary = "회원탈퇴")
    @ApiErrorCodeExample({ErrorCode.MEMBER_NOT_FOUND})
    public ResponseDto withdraw(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return authService.withdraw(email);
    }

    /**
     * 비밀번호 수정
     */
    @PatchMapping("/member/password")
    @Operation(summary = "비밀번호 수정")
    @ApiErrorCodeExample({ErrorCode.MEMBER_NOT_FOUND, ErrorCode.PASSWORD_NOT_MATCH})
    public ResponseDto modifyPassword(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestBody AuthDto.ModifyPasswordReq dto) {
        String email = userDetails.getUsername();
        return authService.modifyPassword(email, dto);
    }


    /**
     * 카카오 로그인
     */
    @GetMapping("/kakaoauth")
    @Operation(summary = "카카오 로그인")
    @ApiErrorCodeExample({ErrorCode.INVALID_VALUE})
    public RedirectView kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return kaKaoAuthService.kakaoAuth(code, response);
    }
}
