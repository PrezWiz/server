package prezwiz.server.dto.response;

import lombok.Data;

/**
 * client에게 요청결과를 알려주기 위해 사용
 */
@Data
public class ResponseDto {

    private String status;
    private String message;

    public ResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDto() {
        this.status = "200";
        this.message = "success";
    }

    public static ResponseDto ok(){
        return new ResponseDto();
    }
}
