public class User {

    private final String username;
    private final String id;

    public User(String username, String id) {
        this.username = username;
        this.id = id;
    }

    String getUsername() { return this.username; }
    String getId() { return this.id; }

}
