package services.authentication_and_authorization_service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthenticationAuthorizationService {

    private static final String URL = "jdbc:mysql://localhost:3306/mini_project";
    private static final String USER = "root";
    private static final String PASSWORD = "2023*";

    // Authenticate user by verifying username and password and return ArrayList containing username and role
    public ArrayList<String> authenticateUser(String username, String password) throws SQLException {
        ArrayList<String> userDetails = null;
        String query = "SELECT username, role FROM users WHERE username = ? AND password = ?";
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userDetails = new ArrayList<>();
                    userDetails.add(resultSet.getString("username"));
                    userDetails.add(resultSet.getString("role"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }


}