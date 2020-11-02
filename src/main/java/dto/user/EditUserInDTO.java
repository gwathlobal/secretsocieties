package dto.user;

public class EditUserInDTO {
    public final long userId;
    public final String username;
    public final String usermail;
    public boolean enabled;

    public EditUserInDTO(long userId, String username, String usermail, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.usermail = usermail;
        this.enabled = enabled;
    }
}
