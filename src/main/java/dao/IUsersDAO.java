package dao;

import models.user.User;

import java.util.List;

public interface IUsersDAO {
    User findUserById(long id);
    User findUserByLogin(String login);
    User findUserByEmail(String email);
    void add(User user);
    void update(User user);
    void delete(User user);
    List<User> findAllUsers();
}
