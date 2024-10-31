package prezwiz.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import prezwiz.server.dto.request.JoinRequestDto;
import prezwiz.server.dto.request.LoginRequestDto;
import prezwiz.server.dto.response.ResponseDto;
import prezwiz.server.service.auth.AuthService;
import prezwiz.server.service.auth.KaKaoAuthService;
import prezwiz.server.service.auth.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final KaKaoAuthService kaKaoAuthService;
    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/api/member")
    public ResponseEntity<ResponseDto> join(@RequestBody JoinRequestDto request) {
        return memberService.saveMember(request);
    }

    /**
     * 로그인
     */
    @PostMapping("/api/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/api/member/{id}")
    public ResponseDto withdraw(@PathVariable Long id) {
        return authService.withDraw(id);
    }

    /**
     * 카카오 로그인
     */
    @GetMapping("/api/kakaoauth")
    public RedirectView kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        return kaKaoAuthService.kakaoAuth(code, response);
    }
}
