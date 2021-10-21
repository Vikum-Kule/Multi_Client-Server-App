import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    private String username;
    private String password;

    Connection connection;

    Login(String username, String password){
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
    public String checkLogin() throws SQLException{
        System.out.println("Username: "+ username);
        System.out.println("Password: "+ password);
        int userId = 0;
        //initializing stetement and result set..
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        //Set the Quary
        String quary = "select * from user where username = ? and password = ?";

        try {
            preparedStatement = connection.prepareStatement(quary);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                userId = resultSet.getInt("user_id");
                String result = "true/"+userId;
                return result;
                //return true;
            }
            else{
                return "false";
            }
        } catch (SQLException e) {
            System.out.println( e );
            return "false";
        } finally{

            preparedStatement.close();
            resultSet.close();

        }

    }

}
