import org.sqlite.util.StringUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthServiceImpl implements AuthService{

//public Map<String,String> users = new HashMap<>();
  static {
      try {
          Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
}
        private Connection connection;
        private PreparedStatement authPreparedStatement;
        private PreparedStatement changeNamePreparedStatement;

    public AuthServiceImpl(String basePath)  {
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:" + basePath);
            authPreparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE user = ? AND password = ?");
            changeNamePreparedStatement = connection.prepareStatement("UPDATE USERS SET user = ? WHERE user = ?");
            } catch (SQLException e){
            e.printStackTrace();
          //  throw new AuthException("Filed to connect to database",e);
        }

        }
    @Override
    public void close()throws Exception{
        connection.close();
}

    @Override
    public boolean AuthUser(String username, String password) {
        if (username == null||password==null){
            return false;
        }
        try {
            authPreparedStatement.setString(1,username);
            authPreparedStatement.setString(2,password);
            ResultSet resultSet = authPreparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            throw new AuthException("Filed to write user",e);
        }
    }

    @Override
    public User getAuthUser(String usersname, String pass) {
        if (usersname == null||pass==null){
            return null;
        }
        try {
            authPreparedStatement.setString(1,usersname);
            authPreparedStatement.setString(2,pass);
            ResultSet resultSet = authPreparedStatement.executeQuery();
            while (resultSet.next()){
                int ID = resultSet.getShort(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);
                return new User(0,username,password);
            }
        } catch (SQLException e){
            throw new AuthException("Filed to write user",e);
        }
        return null;
    }
    @Override
    public void changeName(String oldName, String newName){
        try {
            changeNamePreparedStatement.setString(1,newName);
            changeNamePreparedStatement.setString(2,oldName);
            changeNamePreparedStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
