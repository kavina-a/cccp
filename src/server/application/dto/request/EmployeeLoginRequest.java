package server.application.dto.request;

public class EmployeeLoginRequest {
    private String username;
    private String password;

    public EmployeeLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
