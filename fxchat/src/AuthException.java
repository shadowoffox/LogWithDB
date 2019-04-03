import java.sql.SQLException;

public class AuthException extends RuntimeException {


    public AuthException(String filed_to_connect_to_database, SQLException e) {
    }

    public AuthException() {

    }
}
