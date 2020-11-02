package dto.user;

public class RegisterUserOutDTO {
    public enum ResultEnum { OK, REGISTER_FAILED, EMAIL_FAILED }

    public final ResultEnum result;
    public final int servletCode;
    public final String responseMsg;
    public final String tokenStr;

    public RegisterUserOutDTO(ResultEnum result, int servletCode, String responseMsg, String tokenStr) {
        this.result = result;
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;
        this.tokenStr = tokenStr;
    }
}
