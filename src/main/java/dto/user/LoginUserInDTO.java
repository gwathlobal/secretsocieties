package dto.user;

import utils.sessionstorage.ISessionStorage;

public class LoginUserInDTO {
    public final String username;
    public final String password;
    public final ISessionStorage session;

    public LoginUserInDTO(String username, String password, ISessionStorage session)
    {
        this.username = username;
        this.password = password;
        this.session = session;
    }
}
