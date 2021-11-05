import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    private  static final String SERVER = "127.0.0.1";
    private  static int PORT = 9090;
    static Socket socket= null;
    static BufferedReader input=null;
    static PrintWriter out=null;
    static String clientId = "";

    public static boolean loginStatus( BufferedReader in, PrintWriter out ) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);
        Validation validation = new Validation();
        while (true){

            System.out.println("Login or Signup");
            String command = scanner.nextLine();
            if(command.contains("Login")){
                while (true){

                    System.out.println("Username: ");
                    String username = scanner.nextLine();
                    System.out.println("Password: ");
                    String password = scanner.nextLine();

                    //check input validation
                    boolean inputflag = false;
                    if (validation.usernameValidation(username)){
                        if (validation.passwordValidation(password)){
                            inputflag = true;
                        }
                    }
                    if (inputflag){
                        //out.println(command);
                        String loginInputs = "Login/{\"name\":\""+username+"\", \"username\":\""+username+"\", \"password\":\""+password+"\"}";
                        //out.println(loginInputs);
                        String result = checkConnectionOUT(loginInputs, in, out);
                        String[] resultSet = result.split("/");
                        //System.out.println(result);
                        if (result.contains("Logged")){
                            clientId = resultSet[1];
                            return true;
                        }
                        else if (result.contains("invalid")){
                            System.out.println("Check inputs");
                            continue;
                        }
                        else {
                            System.out.println("Something went wrong.. Try again");
                        }
                    }
                    else {
                        System.out.println("invalid Format");
                    }
                }

            }
            else if (command.contains("Signup")){

                while (true){

                    System.out.println("Full name: ");
                    String name = scanner.nextLine();
                    System.out.println("Username: ");
                    String username = scanner.nextLine();
                    System.out.println("Password: ");
                    String password = scanner.nextLine();

                    //check input validation
                    boolean inputflag = false;
                    if (validation.usernameValidation(username)){
                        if (validation.passwordValidation(password)){
                            inputflag = true;
                        }
                    }
                    if (inputflag){
                        //out.println(command);
                        String SignupInputs = "Signup/{\"name\":\""+name+"\", \"username\":\""+username+"\", \"password\":\""+password+"\"}";
                        //out.println(SignupInputs);
                        String result = checkConnectionOUT(SignupInputs, in, out);
                        String[] resultSet = result.split("/");

                        if (result.contains("Logged")){
                            clientId = resultSet[1];
                            return true;
                        }
                        else if (result.contains("Already exist")){
                            System.out.println("Your account already exist.. Try again");
                            break;
                        }
                        else {
                            System.out.println("Something went wrong.. Try again");
                        }

                    }
                    else {
                        System.out.println("check inputs");
                    }
                }
            }
            else if (command.contains("exit")){
                return false;
            }
            else {
                System.out.println("Wrong input");
            }
        }
    }

    public static void searchNumber(BufferedReader input, PrintWriter out) throws IOException, InterruptedException {
        boolean searchAgain= true;
        while (searchAgain){

            System.out.println("Enter User Phone number: ");
            Scanner scanner = new Scanner(System.in);
            String number = scanner.nextLine();

            //check inputs..
            Validation validation = new Validation();
            boolean checkNumber =  validation.phoneValidation(number);
            if (checkNumber){
                String response= checkConnectionOUT("Search/phone%"+number,input,out);
                System.out.println(response);
                if (response.equals("Phone number is not exist")){
                    System.out.println("Try again-> A");
                    System.out.println("Add new-> B");
                    System.out.println("Exit-> exit");
                    String command = scanner.nextLine();
                    if (command.contains("A")){
                        searchAgain=true;
                    }
                    else if (command.contains("B")){
                        addPatient(input, out);
                        searchAgain=false;
                    }
                    else {
                        searchAgain=false;
                    }
                }
                else {

                    List<PatientStruct> patientList = new ArrayList<>();
                    ObjectMapper mapper = new ObjectMapper();
                    //mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    patientList = mapper.readValue(response, new TypeReference<List<PatientStruct>>(){});
                    System.out.println("Patient List");
                    for (int i=0; i<patientList.size(); i++){
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        int age = year - patientList.get(i).year;
                        System.out.println("Id: "+ i);
                        System.out.println("Name: "+patientList.get(i).name);
                        System.out.println("Age: "+age);
                        System.out.println("Phone: "+patientList.get(i).phone);
                        System.out.println("------------------------------------------------\n");
                    }
                    String id = scanner.nextLine();
                    int index = Integer.parseInt(id);
                    String p_id = String.valueOf(patientList.get(index).id);
                    response= checkConnectionOUT("Search/phone%"+number+"/p_id%"+p_id,input,out);

                    // fetch Records for selected patient
                    List<RecordStruct> records = new ArrayList<>();
                    records = mapper.readValue(response, new TypeReference<List<RecordStruct>>(){});
                    System.out.println("Record List for: "+ patientList.get(index).name);
                    for (int i=0; i<records.size(); i++){

                        System.out.println("Date: "+records.get(i).date);
                        System.out.println("Record: "+records.get(i).record);
                        System.out.println("Added by: "+records.get(i).d_name);
                        System.out.println("------------------------------------------------\n");
                        if (i+1 < records.size()){
                            System.out.println("See next record-> press 'y'");
                        }
                        System.out.println("Add records -> Enter 'add'");
                        System.out.println("Exit records -> press 'exit'");
                        String command = scanner.nextLine();
                        if (command.contains("add")){
                            addRecord( p_id, input, out);
                            searchAgain=false;
                            break;
                        }
                        else if (command.contains("exit")){
                            searchAgain=false;
                            break;
                        }
                        else {
                            searchAgain=true;
                            continue;
                        }

                    }

                }

            }
            else {
                System.out.println("invalid number");
            }

        }


    }

    public static String checkConnectionOUT(String request, BufferedReader in, PrintWriter outServer) throws InterruptedException {
        boolean retry=true;
        boolean serverDown= false;
        String reply= "";
        int retryCount= 1;
        //System.out.println("request: "+ request);
        while (retry){
            try {
                if(serverDown){
                    socket = new Socket(SERVER, PORT);
                    socket.setSoTimeout(2*1000);
                    //System.out.println("Come to server down ");
                   // Socket socket = new Socket();
                    //socket.connect(new InetSocketAddress(SERVER, PORT), 1000);
                    //System.out.println("pass server down ");
                    input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    serverDown = false;
                    System.out.println("Connected..");
                }
                out.println(request);
                if (!request.equals("Search")){
                    reply = input.readLine();
                   // System.out.println("reply"+reply);
                }
                retry = false;
            }catch (Exception e){
                //System.out.println(e);
                retryCount++;
                if (retryCount>=3){
                    System.out.println("Retry again? y/n");
                    Scanner scanner = new Scanner(System.in);
                    String command = scanner.nextLine();
                    if (command.contains("n")){
                        retry=false;
                    }
                    else{
                        continue;
                    }

                }

                if (e.toString().contains("java.net.SocketException: Connection reset")){
                    System.out.println("Server down");
                    System.out.println("Retrying...............");
                    TimeUnit.SECONDS.sleep(3);
                    serverDown=true;

            }
        }

    }
        return reply;
}
    public static void addPatient(BufferedReader in, PrintWriter out) throws InterruptedException {
        while (true){
            String name="";
            String age ="";
            String phone="";
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter patient name: ");
            name = scanner.nextLine();
            System.out.println("Enter patient phone number");
            phone = scanner.nextLine();
            System.out.println("Enter patient age: ");
            age = scanner.nextLine();
            String userData = "new/{\"name\":\""+name+"\", \"age\":\""+age+"\", \"phone\":\""+phone+"\"}";

            String response = checkConnectionOUT(userData,in,out);
            String[] responseSet = response.split("/");
            String[] idSet = responseSet[1].split("%");

            addRecord(idSet[1],in,out);

        }
    }

    public static void addRecord( String id, BufferedReader in, PrintWriter out) throws InterruptedException {
        while (true){
            String record="";
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter Record: ");
            record = scanner.nextLine();
            String response = checkConnectionOUT("add/p_id%"+id+"/record%"+record+"/doctorId%"+clientId,in,out);
            System.out.println(response);
            if (response.equals("Record added")){
                break;
            }
        }

    }


    public static  void main (String[] args) throws InterruptedException, IOException {

        boolean loginFalg= false;
        int runs = 1000000;
        for (int x = 0; x<runs; x++){
            try {
                socket = new Socket(SERVER, PORT);
                input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                // loginFalg = loginStatus(input, out);
                System.out.println("run: "+x);

            }catch (IOException e){
                System.err.println(e);
            }

        }


    while (true){
        if (loginFalg){
            System.out.println("If you want to search user, type 'Search'");
            System.out.println("If you want to add new patient user, type 'new'");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            if (command.contains("Search")){
                searchNumber(input, out);
            }
            else if(command.contains("new")){
                addPatient(input,out);
            }
        }
    }
    }



}
