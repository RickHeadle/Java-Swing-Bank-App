package controller;

import model.DatabaseConnection;
import view.AccessAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connects to the bank database and adds the client if it doesn't exist
 * It creates an account number, logs into the account, and displays the Access Account interface
 */

public class CreateClientAccount {
    //TESTING PURPOSES

    private static int accountNumber;
    private static final Logger LOGGER = Logger.getLogger(CreateClientAccount.class.getName());
    private DatabaseConnection bankConnection = new DatabaseConnection();
    private String firstName;
    private String lastName;
    private String social;

    public CreateClientAccount(String firstName, String lastName, String social, int accountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.social = social;
        CreateClientAccount.accountNumber = ++accountNumber; //increments acct for next user
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, String.format("Acct # to be assigned: %d", CreateClientAccount.accountNumber));
        }

        addClientInfo(firstName, lastName, social);
    }

    private void addClientInfo(String firstName, String lastName, String social) {
        try {
            Connection sqlConnection = bankConnection.createConnectionToDatabase();
            String createClientStatement = "INSERT INTO clients (first_name, last_name, social, account_number) values (?, ?, ?, ?)";
            try (PreparedStatement preparedStatementClient = sqlConnection.prepareStatement(createClientStatement)) {
                preparedStatementClient.setString(1, firstName);
                preparedStatementClient.setString(2, lastName);
                preparedStatementClient.setString(3, social);
                preparedStatementClient.setInt(4, accountNumber);
                preparedStatementClient.execute();
                addCheckingInfo(sqlConnection, accountNumber, 0.0, social);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "При добавлении информации о клиенте в БД получено необрабатываемое исключение", e);
        }
    }// close addClientInfo

    private void addCheckingInfo(Connection bankConnection, int ACCOUNT_NUMBER, double balance, String social) {
        // Connection connection = bankConnection.createConnectionToDatabase();
        try {
            String checkingStatement = "INSERT INTO checking_account (account_number, account_balance, social) values(?,?,?)";
            PreparedStatement preparedStatement = bankConnection.prepareStatement(checkingStatement);
            preparedStatement.setInt(1, ACCOUNT_NUMBER);
            preparedStatement.setDouble(2, balance);
            preparedStatement.setString(3, social);
            preparedStatement.execute();
            new AccessAccount(ACCOUNT_NUMBER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}