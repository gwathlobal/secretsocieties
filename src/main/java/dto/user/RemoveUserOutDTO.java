package dto.user;

public class RemoveUserOutDTO {
    public enum ResultEnum { OK, REMOVE_FAILED_NO_USER }

    public final ResultEnum result;
    public final int servletCode;
    public final String responseMsg;

    public RemoveUserOutDTO(ResultEnum result, int servletCode, String responseMsg) {
        this.result = result;
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;
    }
}
