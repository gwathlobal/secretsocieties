package dto.user;

public class LoginUserOutDTO {
    public final int servletCode;
    public final String responseMsg;

    public LoginUserOutDTO(int servletCode, String responseMsg){
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;

    }
}
