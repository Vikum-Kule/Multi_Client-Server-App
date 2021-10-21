import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServer {
    private  static  final  int PORT = 9090;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static  void main (String[] args) throws IOException{
        ServerSocket socket = new ServerSocket(PORT);
        try {

            while (true){
                System.out.println("waiting for client.....");
                Socket client = socket.accept();
                System.out.println("Connected..");

                ClientHandler clienThread = new ClientHandler(client);
                clients.add(clienThread);
                pool.execute(clienThread);
                for (int i = 0; i<clients.size(); i++){
                    System.out.println("Client: "+clients.get(i).toString());
                }
            }


        } catch (IOException e) {
            for (int i = 0; i<clients.size(); i++){
                System.out.println("Client: "+clients.get(i).toString());
            }
        }
    }
}
