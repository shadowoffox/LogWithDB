public interface AuthService {
    void close()throws Exception;

    boolean AuthUser(String username, String password);

    User getAuthUser(String username, String password);

    void changeName(String oldName, String newName);
}
