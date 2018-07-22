package org.flomintv.accounts.money.transfer.database;

import org.flomintv.accounts.money.transfer.model.Transfer;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

import static org.flomintv.accounts.money.transfer.database.DBUtils.*;

public class TransferDAO {

    private DataSource dataSource;

    public TransferDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertNewTransfer(Transfer transfer, int transferId) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);

            psInsert = connection.prepareStatement("insert into transfers values (?, ?, ?, ?, ?)");

            psInsert.setInt(1, transferId);
            psInsert.setInt(2, transfer.getUserIdFrom());
            psInsert.setInt(3, transfer.getUserIdTo());
            psInsert.setBigDecimal(4, transfer.getAmount());
            psInsert.setString(5, transfer.getComment());
            psInsert.executeUpdate();

            connection.commit();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeStatement(psInsert);
            closeConnection(connection);
        }
    }

    public Transfer getTransfer(int transferId) {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            selectStatement = connection.prepareStatement("SELECT userIdFrom, userIdTo, amount, comment FROM transfers where transferId = ?");
            selectStatement.setInt(1, transferId);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int userIdFrom = resultSet.getInt(1);
                int userIdTo = resultSet.getInt(2);
                BigDecimal amount = resultSet.getBigDecimal(3);
                String comment = resultSet.getString(4);

                Transfer transfer = new Transfer();
                transfer.setUserIdFrom(userIdFrom);
                transfer.setUserIdTo(userIdTo);
                transfer.setAmount(amount);
                transfer.setComment(comment);

                return transfer;
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
