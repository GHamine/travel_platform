package services.authentication_and_authorization_service;


public class AuthService {

    public String authenticate(String username, String password) {
        if ("admin".equals(username) && "password".equals(password)) {
            return "Authentication successful";
        }
        return "Authentication failed";
    }
}
