package dto.user;

import models.user.User;

public class AddUserOutDTO {
    public enum ResultEnum { OK, REGISTER_FAILED }

    public final ResultEnum result;
    public final int servletCode;
    public final String responseMsg;
    public final String tokenStr;
    public final User user;

    public AddUserOutDTO(ResultEnum result, int servletCode, String responseMsg, String tokenStr, User user) {
        this.result = result;
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;
        this.tokenStr = tokenStr;
        this.user = user;
    }
}
