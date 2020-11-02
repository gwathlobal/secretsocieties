package dto.user;

public class AddUserInDTO {
    public final String username;
    public final String password;
    public final String usermail;

    public AddUserInDTO(String username, String password, String usermail) {
        this.username = username;
        this.password = password;
        this.usermail = usermail;
    }
}
