package org.flomintv.accounts.money.transfer.database;

import org.flomintv.accounts.money.transfer.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.flomintv.accounts.money.transfer.database.DBUtils.*;

public class UserDAO {

    private DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User getUserData(Integer userId) {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            selectStatement = connection.prepareStatement("SELECT firstName, middleName, lastName FROM users where userId = ?");
            selectStatement.setInt(1, userId);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString(1);
                String middleName = resultSet.getString(2);
                String lastName = resultSet.getString(3);

                User user = new User();
                user.setUserId(userId);
                user.setFirstName(firstName);
                user.setMiddleName(middleName);
                user.setLastName(lastName);

                return user;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeResultSet(resultSet);
            closeStatement(selectStatement);
            closeConnection(connection);
        }

        return null;
    }

}
