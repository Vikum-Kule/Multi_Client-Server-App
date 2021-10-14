import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Signup {
    private String username;
    private String password;
    private String name;

    Connection connection;

    Signup(String name, String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
        connection = Database.ConnectDB();
        if (connection == null){
            System.exit(1);
        }
    }

    public boolean isDbConnected(){
        try {
            return !connection.isClosed();
        }catch (SQLException e){
            System.err.println(e);
            return false;
        }
    }
    public boolean addUser() throws SQLException{

        try {

            String query = "INSERT INTO user(full_name, username, password) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.executeUpdate();
            statement.close();
            return true;

        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }

    }

}

