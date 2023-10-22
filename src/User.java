
public class User {

    private final String username;

    public User(String username) {
        this.username = username;
    }

    String getUsername() { return this.username; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }

}
