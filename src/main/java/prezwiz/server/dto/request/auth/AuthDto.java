package prezwiz.server.dto.request.auth;

import lombok.Data;

public class AuthDto {

    @Data
    public static class ModifyPasswordReq {
        private String currentPassword;
        private String newPassword;
    }
}
