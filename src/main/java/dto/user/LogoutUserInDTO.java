package dto.user;

import utils.sessionstorage.ISessionStorage;

public class LogoutUserInDTO {
    public final ISessionStorage session;

    public LogoutUserInDTO(ISessionStorage session) {
        this.session = session;
    }
}
