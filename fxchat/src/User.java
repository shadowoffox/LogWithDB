import java.util.Objects;

public class User {
    public int ID;
    public String username;
    public String password;

    public User(int ID, String username, String password){
        this.ID=ID;
        this.username=username;
        this.password=password;

    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null|| getClass() != o.getClass()) return false;
        User user = (User) o;
        return ID == user.ID &&
                Objects.equals(username,user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode(){
        return Objects.hash(ID,username,password);
    }
}
