package dto.user;

public class LogoutUserOutDTO {

    public enum ResultEnum { OK, NO_USER_FOUND }

    public final ResultEnum result;
    public final String userLogin;

    public LogoutUserOutDTO(ResultEnum result, String userLogin) {
        this.result = result;
        this.userLogin = userLogin;
    }
}
