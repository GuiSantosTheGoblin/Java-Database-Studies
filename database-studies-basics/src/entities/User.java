package entities;

public class User implements Comparable<User> {
    private int userID;
    private String username;
    private String password;
    private String email;

    public User(int userID, String username, String password, String email) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getUserID() {
        return userID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(User other) {
        if (this.username.equals(other.getUsername()) && this.email.equals(other.getEmail())) {
            return 0;
        } else if (this.userID == other.getUserID()) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {  
        return "User {" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}