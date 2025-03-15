package parcial.Class;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
//import java.util.ArrayList;
//import java.util.List;

@Entity
public class Usuario {
    @Id
    private String username;
    private String password;
    private boolean administrator;

    public Usuario(String username, String password, boolean administrator) {
        this.username = username;
        this.password = password;
        this.administrator = administrator;
    }

    public Usuario() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean admin) {
        this.administrator = admin;
    }
}
