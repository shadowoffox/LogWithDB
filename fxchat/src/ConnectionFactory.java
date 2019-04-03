import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static BasicDataSource dataSource;
    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException{
            dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:sqlite:For_logging");
            dataSource.setDriverClassName("org.sqlite.JDBC");


        return dataSource.getConnection();
    }
}
