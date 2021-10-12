import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private  static final String SERVER = "127.0.0.1";
    private  static final int PORT = 9090;

    public static  void main (String[] args){
        try {
            Socket socket = new Socket(SERVER, PORT);

            BufferedReader input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //String sereverResponse = input.readLine();
            while (true){
                String sereverResponse = input.readLine();
                System.out.println(sereverResponse);
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine();
                if (command.equals("exit")){
                    break;
                }
                out.println(command);
//                else {
//                    out.println(command);
//                    //BufferedReader input2 = new BufferedReader();
//                    sereverResponse = input.readLine();
//                    System.out.println(sereverResponse);
//                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
