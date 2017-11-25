package kivaaz.com.driverapp;

/**
 * Created by Muguntan on 11/25/2017.
 */

public class UserType {
    String email;
    String role;

    public UserType() {
    }

    public UserType(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
