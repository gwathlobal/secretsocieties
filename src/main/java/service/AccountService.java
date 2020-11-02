package service;

import com.google.inject.Inject;
import dao.IConfirmationTokenDAO;
import dao.IUsersDAO;
import dto.user.*;
import guice.ConfirmationTokenDAO;
import guice.UsersDAO;
import models.user.ConfirmationToken;
import models.user.User;
import models.helper.RoleSet;
import org.mindrot.jbcrypt.BCrypt;
import servlets.ConfirmAccountServlet;
import utils.sessionstorage.ISessionStorage;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

public class AccountService implements IAccountService {

    @Inject
    @UsersDAO
    private IUsersDAO usersDao;

    @Inject
    @ConfirmationTokenDAO
    private IConfirmationTokenDAO confirmationTokenDAO;

    private static final String userAttr = "user";

    @Override
    public User createUserWithHashedPass(String login, String password, String email) {
        return new User(login, BCrypt.hashpw(password, BCrypt.gensalt()), email, new RoleSet(false, true));
    }

    @Override
    public User findDBUserById(long id) {
        return usersDao.findUserById(id);
    }

    @Override
    public User findDBUserByLogin(String login) {
        return usersDao.findUserByLogin(login);
    }

    @Override
    public User findDBUserByEmail(String email) { return usersDao.findUserByEmail(email); }

    @Override
    public void saveDBUser(User user) {
        usersDao.add(user);
    }

    @Override
    public void deleteDBUser(User user) {
        usersDao.delete(user);
    }

    @Override
    public void deleteDBUserById(long id) {
        User user = usersDao.findUserById(id);
        usersDao.delete(user);
    }

    @Override
    public void updateDBUser(User user) {
        usersDao.update(user);
    }

    @Override
    public List<User> findAllDBUsers() {
        return usersDao.findAllUsers();
    }

    @Override
    public User findLoginUserBySession(ISessionStorage session) { return (User) session.getValue(userAttr); }

    @Override
    public void saveLoginUser(ISessionStorage session, User user) { session.setValue(userAttr, user); }

    @Override
    public void deleteLoginUserBySession(ISessionStorage session) { session.remValue(userAttr); }

    @Override
    public ConfirmationToken findDBTokenById(long id) {
        return confirmationTokenDAO.findTokenById(id);
    }

    @Override
    public ConfirmationToken findDBTokenByTokenStr(String token) {
        return confirmationTokenDAO.findTokenByTokenStr(token);
    }

    @Override
    public void saveDBToken(ConfirmationToken token) {
        confirmationTokenDAO.add(token);
    }

    @Override
    public void deleteDBToken(ConfirmationToken token) {
        confirmationTokenDAO.delete(token);
    }

    @Override
    public void updateDBToken(ConfirmationToken token) {
        confirmationTokenDAO.update(token);
    }

    @Override
    public LoginUserOutDTO doLoginUser(LoginUserInDTO in) {

        if (in.username == null || in.username.isBlank() || in.password == null || in.password.isBlank()) {
            return new LoginUserOutDTO(HttpServletResponse.SC_BAD_REQUEST, "Bad login or password");
        }

        User user = findDBUserByLogin(in.username);

        if (user == null || !BCrypt.checkpw(in.password, user.getHashedPassword())) {
            return new LoginUserOutDTO(HttpServletResponse.SC_UNAUTHORIZED, "Bad login or password");
        }

        if (!user.getEnabled()) {
            return new LoginUserOutDTO(HttpServletResponse.SC_UNAUTHORIZED, "Account inactive, please activate using an email link");
        }

        saveLoginUser(in.session, user);
        return new LoginUserOutDTO(HttpServletResponse.SC_OK, null);
    }

    @Override
    public LogoutUserOutDTO doLogoutUser(LogoutUserInDTO in) {
        User loggedInUser = findLoginUserBySession(in.session);
        if (loggedInUser != null) {
            deleteLoginUserBySession(in.session);

            return new LogoutUserOutDTO(LogoutUserOutDTO.ResultEnum.OK, loggedInUser.getLogin());
        }
        else
            return new LogoutUserOutDTO(LogoutUserOutDTO.ResultEnum.NO_USER_FOUND, null);
    }

    @Override
    public RegisterUserOutDTO doRegisterUser(RegisterUserInDTO in) {
        AddUserOutDTO addResult = doAddUser(new AddUserInDTO(in.username,in.password,in.usermail));

        if (addResult.result == AddUserOutDTO.ResultEnum.REGISTER_FAILED) {
            return new RegisterUserOutDTO(RegisterUserOutDTO.ResultEnum.REGISTER_FAILED, addResult.servletCode,
                    addResult.responseMsg, null);
        }

        try {
            in.emailService.sendEmail("admin@secretsocieties.tk", "32167","admin@secretsocieties.tk", in.usermail, "Registration Activation",
                    String.format("To confirm your account, please click here: http://%s/%s?%s=%s",
                            in.hostname,
                            ConfirmAccountServlet.path.substring(1),
                            ConfirmAccountServlet.tokenParam,
                            addResult.tokenStr));
            return new RegisterUserOutDTO(RegisterUserOutDTO.ResultEnum.OK, HttpServletResponse.SC_OK,null, addResult.tokenStr);
        }
        catch (RuntimeException e) {
            return new RegisterUserOutDTO(RegisterUserOutDTO.ResultEnum.EMAIL_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    String.format("Failed to send confirmation email to %s: %s", in.usermail, e.toString()), null);
        }
    }

    @Override
    public ConfirmRegOutDTO doConfirmRegisterUser(ConfirmRegInDTO in) {

        if (in.tokenStr == null || in.tokenStr.isBlank()) {
            return new ConfirmRegOutDTO(ConfirmRegOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Error! Token required.");
        }

        ConfirmationToken token = findDBTokenByTokenStr(in.tokenStr);

        if (token == null) {
            return new ConfirmRegOutDTO(ConfirmRegOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Error! Token not found.");
        }

        User user = token.getUser();

        Calendar cal = Calendar.getInstance();
        if ((token.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            token = new ConfirmationToken(user);

            saveDBToken(token);

            try {
                in.emailService.sendEmail("admin@secretsocieties.tk", "32167","admin@secretsocieties.tk", user.getEmail(), "Registration Activation",
                        String.format("To confirm your account, please click here: https://%s/%s?%s=%s",
                                in.hostname,
                                ConfirmAccountServlet.path.substring(1),
                                ConfirmAccountServlet.tokenParam,
                                token.getToken()));

                return new ConfirmRegOutDTO(ConfirmRegOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_OK,
                        String.format("Error! Token expired. A new one is sent to %s. Click the link there to finish the registration.", user.getEmail()));
            }
            catch (RuntimeException e) {
                return new ConfirmRegOutDTO(ConfirmRegOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                        String.format("Error! Token expired, but the System failed to send confirmation email to %s: %s", user.getEmail(), e.toString()));
            }
        }

        user.setEnabled(true);
        updateDBUser(user);

        return new ConfirmRegOutDTO(ConfirmRegOutDTO.ResultEnum.OK, HttpServletResponse.SC_OK,
                "Registration confirmed! You can login now.");
    }

    @Override
    public RemoveUserOutDTO doRemoveUser(RemoveUserInDTO in) {

        try {
            deleteDBUserById(in.userId);
            return new RemoveUserOutDTO(RemoveUserOutDTO.ResultEnum.OK,HttpServletResponse.SC_OK, String.format("Successfully removed user with id = %s!", in.userId));
        }
        catch (RuntimeException e) {
            return new RemoveUserOutDTO(RemoveUserOutDTO.ResultEnum.REMOVE_FAILED_NO_USER,HttpServletResponse.SC_BAD_REQUEST, String.format("Failed tp remove user with id = %s!", in.userId));
        }

    }

    @Override
    public AddUserOutDTO doAddUser(AddUserInDTO in) {
        if (in.username == null || in.username.isBlank() || in.password == null || in.password.isBlank()) {
            return new AddUserOutDTO(AddUserOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Bad login or password", null, null);
        }

        if (in.usermail == null || in.usermail.isBlank()) {
            return new AddUserOutDTO(AddUserOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "E-mail is obligatory", null, null);
        }

        User user = findDBUserByLogin(in.username);

        if (user != null) {
            return new AddUserOutDTO(AddUserOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Username already exists", null, null);
        }

        user = findDBUserByEmail(in.usermail);

        if (user != null) {
            return new AddUserOutDTO(AddUserOutDTO.ResultEnum.REGISTER_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "E-mail already used", null, null);
        }

        user = createUserWithHashedPass(in.username, in.password, in.usermail);
        saveDBUser(user);

        ConfirmationToken token = new ConfirmationToken(user);

        saveDBToken(token);

        return new AddUserOutDTO(AddUserOutDTO.ResultEnum.OK, HttpServletResponse.SC_OK,
                "User successfully added!", token.getToken(), user);
    }

    @Override
    public EditUserOutDTO doEditUser(EditUserInDTO in) {

        if (in.username == null || in.username.isBlank()) {
            return new EditUserOutDTO(EditUserOutDTO.ResultEnum.EDIT_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Bad login", null);
        }

        if (in.usermail == null || in.usermail.isBlank()) {
            return new EditUserOutDTO(EditUserOutDTO.ResultEnum.EDIT_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "E-mail is obligatory", null);
        }
        User editedUser = findDBUserById(in.userId);

        if (editedUser == null) {
            return new EditUserOutDTO(EditUserOutDTO.ResultEnum.EDIT_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Unable to find edited user", null);
        }

        User anyUserWithName = findDBUserByLogin(in.username);

        if (anyUserWithName != null && !anyUserWithName.equals(editedUser) ) {
            return new EditUserOutDTO(EditUserOutDTO.ResultEnum.EDIT_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "Username already exists", null);
        }

        User anyUserWithEmail = findDBUserByEmail(in.usermail);

        if (anyUserWithEmail != null && !anyUserWithEmail.equals(editedUser)) {
            return new EditUserOutDTO(EditUserOutDTO.ResultEnum.EDIT_FAILED, HttpServletResponse.SC_BAD_REQUEST,
                    "E-mail already used", null);
        }

        editedUser.setLogin(in.username);
        editedUser.setEmail(in.usermail);
        editedUser.setEnabled(in.enabled);

        updateDBUser(editedUser);

        return new EditUserOutDTO(EditUserOutDTO.ResultEnum.OK, HttpServletResponse.SC_OK,
                "User successfully edited!", editedUser);
    }
}