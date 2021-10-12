import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSoc;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler (Socket clientSoc) throws IOException{
        this.clientSoc = clientSoc;
        out = new PrintWriter(clientSoc.getOutputStream(),true);
        in  = new BufferedReader( new InputStreamReader( clientSoc.getInputStream()));
    }


    @Override
    public void run() {

        try {
            out.println(" Login or Sign up ");
            while (true){
                System.out.println(" Login ");
                String request = in.readLine();
                System.out.println("Received : "+ request);
                if(request.contains("Login")){
                    out.println(" Go to login ");
                    break;
                }
                else if(request.contains("Sign up")){
                    out.println(" Go to Sign up ");
                    break;
                }
                else {
                    out.println(" Wrong input ");
                }
            }

        } catch (IOException e) {
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
}
