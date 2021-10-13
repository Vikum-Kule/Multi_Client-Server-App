import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private  static final String SERVER = "127.0.0.1";
    private  static final int PORT = 9090;


    public static boolean loginStatus( BufferedReader in, PrintWriter out ) throws IOException{
        while (true){
            String sereverResponse = in.readLine();
            System.out.println(sereverResponse);
            if(sereverResponse.contains("DB connected..")){
                sereverResponse = in.readLine();
                System.out.println(sereverResponse);
                if(sereverResponse.equals("Login success") || sereverResponse.equals("Signup success")){
                    return true;
                }
            }
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (command.equals("exit")){
                return false;
            }
            if (!sereverResponse.equals("check your inputs")){
                out.println(command);
            }

        }
    }

    public static  void main (String[] args){
        try {
            Socket socket = new Socket(SERVER, PORT);

            BufferedReader input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //String sereverResponse = input.readLine();
            boolean loginFalg = loginStatus(input, out);
            if(loginFalg){
                System.out.println("If you want to search user, type 'Search'");
                System.out.println("If you want to add new patient user, type 'new'");
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine();
                out.println(command);
                String sereverResponse = input.readLine();
                System.out.println(sereverResponse);
                if (sereverResponse.equals("")){
                    command = scanner.nextLine();
                    out.println(command);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
