package prezwiz.server.service.auth;

import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;
import prezwiz.server.common.exception.BizBaseException;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.dto.CustomUserInfoDto;
import prezwiz.server.dto.kakao.KaKaoTokenDto;
import prezwiz.server.dto.kakao.KaKaoUserInfoDto;
import prezwiz.server.dto.PropertyKeysBuilder;
import prezwiz.server.entity.Member;
import prezwiz.server.repository.MemberRepository;
import prezwiz.server.security.JwtUtil;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KaKaoAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${kakao.api_key}")
    private String KAKAO_API_KEY;
    @Value("${kakao.redirect_uri}")
    private String REDIRECT_URI;
    @Value("${service.client_uri}")
    private String CLIENT_URI;


    public RedirectView kakaoAuth(String code, HttpServletResponse response) {
        String kaKaoAccessToken = getKaKaoAccessToken(code);
        String email = getEmail(kaKaoAccessToken);

        Member savedMember = null;
        if (isExistMember(email)) {
            savedMember = saveNewMember(email);
        } else {
            savedMember = getMember(email);
        }

        addCookieToResponse(response, savedMember);
        return new RedirectView(CLIENT_URI);
    }

    /**
     *  kakaoApi를 사용하기 위해서 code를 이용해 token을 요청함
     */
    private String getKaKaoAccessToken(String code) {
        KaKaoTokenDto token = WebClient.create("https://kauth.kakao.com/oauth/token").post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", KAKAO_API_KEY)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                // Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BizBaseException(ErrorCode.KAKAO_AUTH_CLIENT_ERROR)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new BizBaseException(ErrorCode.KAKAO_AUTH_INTERNAL_ERROR)))
                .bodyToMono(KaKaoTokenDto.class)
                .block();

        return token.getAccessToken();
    }

    /**
     * token을 이용해서 kakaoApi에 사용자의 email을 요청함
     * -- propertyKeys 생산하는 부분이 마음에 안들음, buildPattern을 적용하면 좋을거 같음
     */
    private String getEmail(String accessToken) {
        PropertyKeysBuilder propertyKeysBuilder = new PropertyKeysBuilder();
        propertyKeysBuilder.add("kakao_account.email");
        String propertyKeys = propertyKeysBuilder.build();


        KaKaoUserInfoDto userInfo = WebClient.create("https://kapi.kakao.com/v2/user/me").get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("property_keys", propertyKeys)
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                // Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BizBaseException(ErrorCode.KAKAO_AUTH_CLIENT_ERROR)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new BizBaseException(ErrorCode.KAKAO_AUTH_INTERNAL_ERROR)))
                .bodyToMono(KaKaoUserInfoDto.class)
                .block();
        return userInfo.getKaKaoAccount().getEmail();
    }

    /**
     * 저장되거나 가져온 member객체를 통해 jwt를 생성해 response header에 cookie로 추가
     */
    private void addCookieToResponse(HttpServletResponse response, Member savedMember) {
        if (savedMember != null) {
            CustomUserInfoDto info = new CustomUserInfoDto(savedMember.getId(), savedMember.getEmail(), savedMember.getPassword(), savedMember.getRole());
            String accessToken = jwtUtil.createAccessToken(info);
            Cookie cookie = new Cookie("authorization", accessToken);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            throw new RuntimeException("savedMember가 존재하지 않음");
        }
    }

    /**
     * email을 이용해 저장된 member를 가져옴
     */
    private Member getMember(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    /**
     * email을 이용해 회원가입, 비밀번호의 경우 uuid를 암호화함
     */
    private Member saveNewMember(String email) {
        String uuid = UUID.randomUUID().toString();
        Member newMember = new Member(email, passwordEncoder.encode(uuid), "user");
        return memberRepository.save(newMember);
    }

    private boolean isExistMember(String email) {
        return memberRepository.findMemberByEmail(email) == null;
    }
}
