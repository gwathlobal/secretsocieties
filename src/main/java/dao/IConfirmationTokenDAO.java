package dao;

import models.user.ConfirmationToken;

public interface IConfirmationTokenDAO {
    ConfirmationToken findTokenById(long id);
    ConfirmationToken findTokenByTokenStr(String token);
    void add(ConfirmationToken token);
    void update(ConfirmationToken token);
    void delete(ConfirmationToken token);
}
