package prezwiz.server.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CustomUserInfoDto {

    private Long memberId;
    private String email;
    private String password;
    private String role;

    public CustomUserInfoDto(Long memberId, String email, String password, String role) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
