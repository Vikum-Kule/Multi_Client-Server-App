import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataServer {
    private  static  final  int PORT = 9090;
    private static final int MAX_LIMIT=3;
    private static final String HOSTNAME = "127.0.0.1";
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(MAX_LIMIT);
    //private static ArrayList<Socket> waitingList = new ArrayList<>();
    private static  LinkedList<Socket> waitingList = new LinkedList<>();
    private static int _activeCount = 0;

    private  static  void checkSockets(){
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    //System.out.println("Check..");
                    for (int i = 0; i<clients.size(); i++){
                        boolean disconnect = false;
                        if(!clients.get(i).isAlive){
                            clients.remove(i);
                            _activeCount--;
                        }
//                        else {
//                            System.out.println("Client: "+clients.get(i).toString());
//                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private  static  void addSockets( ServerSocket socket){

        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        System.out.println("processing..: "+ waitingList.size());
                        if (_activeCount<MAX_LIMIT && !waitingList.isEmpty()){
                            System.out.println("processing..");
                            ClientHandler clienThread = new ClientHandler(waitingList.get(0));
                            clients.add(clienThread);
                            pool.execute(clienThread);
                            _activeCount++;
                            waitingList.remove(0);
                         }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static  void main (String[] args) throws IOException{
        ServerSocket socket = new ServerSocket(PORT);
        try {
            checkSockets();
            addSockets( socket);
//            Selector selector = Selector.open();
//            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//            serverSocketChannel.configureBlocking(false);
//            serverSocketChannel.bind(new InetSocketAddress(HOSTNAME, PORT));
//            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//            SelectionKey key = null;
//
//            while (true){
//                if(selector.select()<=0){
//                    continue;
//                }
//                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//                Iterator<SelectionKey> iterator = selectionKeys.iterator();
//
//                while (){
//
//                }
//
//            }
            int x =0;

            while (true){
                //System.out.println("processing..: "+ waitingList.size());
                System.out.println("waiting for client.....");
                Socket client = socket.accept();
                waitingList.addLast(client);
                System.out.println("Connected..");

            }


        } catch (IOException e) {
            for (int i = 0; i<clients.size(); i++){
                System.out.println("Client: "+clients.get(i).toString());
            }
        }
    }
}
