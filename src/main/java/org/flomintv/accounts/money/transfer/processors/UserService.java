package org.flomintv.accounts.money.transfer.processors;

import org.flomintv.accounts.money.transfer.database.UserDAO;
import org.flomintv.accounts.money.transfer.model.User;

public class UserService {

    private UserDAO userDAO;

    public User getUser(Integer userId) {
        return userDAO.getUserData(userId);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

}
