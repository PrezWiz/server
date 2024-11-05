package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import prezwiz.server.dto.request.auth.AuthDto;
import prezwiz.server.dto.request.auth.JoinRequestDto;
import prezwiz.server.dto.request.auth.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.security.JwtUtil;
import prezwiz.server.service.auth.AuthService;
import prezwiz.server.service.auth.KaKaoAuthService;
import prezwiz.server.service.auth.MemberService;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final KaKaoAuthService kaKaoAuthService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @PostMapping("/member")
    @Operation(summary = "회원가입")
    public ResponseEntity<ResponseDto> join(@RequestBody JoinRequestDto request) {
        return memberService.saveMember(request);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/member")
    @Operation(summary = "회원탈퇴")
    public ResponseDto withdraw(@RequestHeader("authorization") String bearer) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return authService.withdraw(email);
    }

    /**
     * 비밀번호 수정
     */
    @PatchMapping("/member/password")
    @Operation(summary = "비밀번호 수정")
    public ResponseDto modifyPassword(@RequestHeader("authorization") String bearer,
                                      @RequestBody AuthDto.ModifyPasswordReq dto) {
        String email = jwtUtil.getEmail(bearer.substring(7));
        return authService.modifyPassword(email, dto);
    }


    /**
     * 카카오 로그인
     */
    @GetMapping("/kakaoauth")
    @Operation(summary = "카카오 로그인")
    public RedirectView kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return kaKaoAuthService.kakaoAuth(code, response);
    }
}
