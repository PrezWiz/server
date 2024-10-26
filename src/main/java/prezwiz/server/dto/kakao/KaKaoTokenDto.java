package prezwiz.server.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KaKaoTokenDto {

    @JsonProperty("access_token")
    public String accessToken;
    @JsonProperty("token_type")
    public String tokenType;
    @JsonProperty("refresh_token")
    public String refreshToken;
    @JsonProperty("expires_in")
    public Integer expiresIn;
    @JsonProperty("scope")
    public String scope;
    @JsonProperty("refresh_token_expires_in")
    public Integer refreshTokenExpiresIn;

}
