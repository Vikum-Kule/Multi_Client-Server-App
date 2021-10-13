import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSoc;
    private BufferedReader in;
    private PrintWriter out;
    private boolean loginflag;

    public ClientHandler (Socket clientSoc) throws IOException{
        this.clientSoc = clientSoc;
        out = new PrintWriter(clientSoc.getOutputStream(),true);
        in  = new BufferedReader( new InputStreamReader( clientSoc.getInputStream()));
        loginflag = false;
    }


    @Override
    public void run() {

        try {
            out.println(" Login or Sign up ");
            while (true){

                String request = in.readLine();
                System.out.println("Received : "+ request);
                if(request.contains("Login")){
                    userLogin();
                }
                else if(request.contains("Sign up")){
                    userSignup();
                }
                else if(request.contains("Search") && loginflag){
                    out.println("Searching...");
                    break;
                }
                else {
                    out.println(" Wrong input ");
                }
            }

        } catch (IOException | SQLException e) {
                e.printStackTrace();
        }
        finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Login user
    public void userLogin() throws IOException, SQLException {
        while (true){
            out.println("Username: ");
            String username = in.readLine();
            out.println("Password:");
            String password = in.readLine();
            Login login = new Login(username, password);
            Validation validation = new Validation();
            if (login.isDbConnected()){
                out.println("DB connected..");
                if(validation.usernameValidation(username) && validation.passwordValidation(password)){
                    boolean resultLogin = login.checkLogin();
                    if(resultLogin){
                        loginflag = true;
                        out.println("Login success");
                        break;
                    }
                    else {
                        out.println("Invalid input");
                    }
                }
                else {
                    out.println("check your inputs");
                }
            }
            else {
                out.println("Database disconnected..");
            }

        }
    }

    //user sign up
    public void userSignup() throws IOException, SQLException {
        while (true){
            out.println("Name: ");
            String name = in.readLine();
            out.println("Username: ");
            String username = in.readLine();
            out.println("Password:");
            String password = in.readLine();
            Signup signup = new Signup(name,username, password);
            Validation val = new Validation();

                if (signup.isDbConnected()){
                    out.println("DB connected..");
                    if (val.fullNameValidation(name) && val.usernameValidation(username) && val.passwordValidation(password)) {
                        boolean resultSignup = signup.addUser();
                        if (resultSignup) {
                            loginflag = true;
                            out.println("Signup success");
                            //go to login..
                            //userLogin();
                            break;
                        } else {
                            out.println("Something went wrong..");
                        }
                    }
                    else {
                        out.println("check your inputs");
                    }
                }
                else {
                    out.println("Database disconnected..");
                }

        }
    }

    //using for search patient numbers..
    public void numberSearch() throws IOException{
        out.println("Enter patient phone number: ");
        String name = in.readLine();


    }

}
