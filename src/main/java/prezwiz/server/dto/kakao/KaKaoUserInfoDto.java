package prezwiz.server.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class KaKaoUserInfoDto {

    // 회원 번호
    @JsonProperty("id")
    private Long id;

    // 서비스에 연결 완료된 시각.
    @JsonProperty("connected_at")
    private Date connectedAt;

    @JsonProperty("kakao_account")
    private KaKaoAccount kaKaoAccount;


    @Data
    @NoArgsConstructor
    public class KaKaoAccount {

        @JsonProperty("has_email")
        private Boolean hasEmail;

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;

        @JsonProperty("email")
        private String email;
    }
}
