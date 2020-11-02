package dto.user;

public class ConfirmRegOutDTO {

    public enum ResultEnum { OK, REGISTER_FAILED }

    public final ConfirmRegOutDTO.ResultEnum result;
    public final int servletCode;
    public final String responseMsg;

    public ConfirmRegOutDTO(ResultEnum result, int servletCode, String responseMsg) {
        this.result = result;
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;
    }
}
