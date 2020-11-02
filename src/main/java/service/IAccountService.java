package service;

import dto.user.*;
import models.user.ConfirmationToken;
import models.user.User;
import utils.sessionstorage.ISessionStorage;

import java.util.List;

public interface IAccountService {
    // low-level methods
    User createUserWithHashedPass(String login, String password, String email);

    User findDBUserById(long id);
    User findDBUserByLogin(String login);
    User findDBUserByEmail(String email);
    void saveDBUser(User user);
    void deleteDBUser(User user);
    void deleteDBUserById(long id);
    void updateDBUser(User user);
    List<User> findAllDBUsers();

    User findLoginUserBySession(ISessionStorage session);
    void saveLoginUser(ISessionStorage session, User user);
    void deleteLoginUserBySession(ISessionStorage session);

    ConfirmationToken findDBTokenById(long id);
    ConfirmationToken findDBTokenByTokenStr(String token);
    void saveDBToken(ConfirmationToken token);
    void deleteDBToken(ConfirmationToken token);
    void updateDBToken(ConfirmationToken token);

    // high-level methods
    LoginUserOutDTO doLoginUser(LoginUserInDTO in);
    LogoutUserOutDTO doLogoutUser(LogoutUserInDTO in);
    RegisterUserOutDTO doRegisterUser(RegisterUserInDTO in);
    ConfirmRegOutDTO doConfirmRegisterUser(ConfirmRegInDTO in);
    RemoveUserOutDTO doRemoveUser(RemoveUserInDTO in);
    AddUserOutDTO doAddUser(AddUserInDTO in);
    EditUserOutDTO doEditUser(EditUserInDTO in);
}
