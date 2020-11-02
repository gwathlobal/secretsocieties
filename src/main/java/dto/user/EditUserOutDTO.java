package dto.user;

import models.user.User;

public class EditUserOutDTO {
    public enum ResultEnum { OK, EDIT_FAILED }

    public final ResultEnum result;
    public final int servletCode;
    public final String responseMsg;
    public final User user;

    public EditUserOutDTO(ResultEnum result, int servletCode, String responseMsg, User user) {
        this.result = result;
        this.servletCode = servletCode;
        this.responseMsg = responseMsg;
        this.user = user;
    }
}
