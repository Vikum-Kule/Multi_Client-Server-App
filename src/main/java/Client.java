import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    private  static final String SERVER = "127.0.0.1";
    private  static final int PORT = 9090;


    public static boolean loginStatus( BufferedReader in, PrintWriter out ) throws IOException{

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
                        out.println(loginInputs);
                        String result = in.readLine();
                        if (result.contains("Logged")){
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
                        out.println(command);
                        String SignupInputs = "Signup/{\"name\":\""+name+"\", \"username\":\""+username+"\", \"password\":\""+password+"\"}";
                        out.println(SignupInputs);
                        String result = in.readLine();
                        if (result.contains("Logged")){
                            return true;
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
        while (true){

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
                        continue;
                    }
                    else if (command.contains("B")){

                    }
                    else {
                        break;
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
                    p_id = "Search/phone%"+number+"/p_id%"+p_id;
                    response= checkConnectionOUT(p_id,input,out);

                    // fetch Records for selected patient
                    List<RecordStruct> records = new ArrayList<>();
                    records = mapper.readValue(response, new TypeReference<List<RecordStruct>>(){});
                    System.out.println("Record List for: "+ patientList.get(index).name);
                    for (int i=0; i<records.size(); i++){

                        System.out.println("Date: "+records.get(i).date);
                        System.out.println("Record: "+records.get(i).record);
                        System.out.println("------------------------------------------------\n");
                        if (i+1 < records.size()){
                            System.out.println("See next record-> press 'y'");
                        }
                        System.out.println("Add records -> Enter 'add'");
                        System.out.println("Exit records -> press 'exit'");
                        String command = scanner.nextLine();
                        if (command.contains("add")){

                        }
                        else if (command.contains("exit")){
                            break;
                        }
                        else {
                            continue;
                        }

                    }

                }

            }
            else {
                System.out.println("invalid number");
            }

        }



//        Scanner scanner = new Scanner(System.in);
//        String command;
//        while (true){
//
//            String sereverResponse = input.readLine();
//            if (sereverResponse.equals(null)){
//                break;
//            }
//            System.out.println(sereverResponse);
//            if (sereverResponse.contains("Enter patient phone number: ")){
//                command = scanner.nextLine();
//                out.println(command);
//            }
//            else if(sereverResponse.equals("Phone number is not exist")|| sereverResponse.equals("Record added")|| sereverResponse.equals("Something went wrong..")){
//                break;
//            }
//            else if (sereverResponse.equals("Enter patient Id to get records: ")|| sereverResponse.equals("Record description: ")|| sereverResponse.equals("Wrong input")){
//                command = scanner.nextLine();
//                out.println(command);
//            }
//            else if (sereverResponse.contains("Exit records -> press 'n'")){
//                command = scanner.nextLine();
//                if (command.contains("y") || command.contains("add")){
//                    out.println(command);
//                }
//                else if (command.contains("n")){
//                    break;
//                }
//
//            }
//
//        }
    }

    public static String checkConnectionOUT(String input, BufferedReader in, PrintWriter out) throws InterruptedException {
        boolean retry=true;
        String reply= "";
        int retryCount= 1;
        int i=1;
        StringBuilder sb = new StringBuilder();
        while (retry){
            try {
                out.println(input);
                if (!input.equals("Search")){
                    reply = in.readLine();
                }
                retry = false;
            }catch (IOException e){
                if (e.toString().contains("java.net.SocketException: Connection reset")){
                    System.out.println("Server down");
                    System.out.println("Retrying");
                    TimeUnit.SECONDS.sleep(3);
                    retryCount++;
                    if (retryCount==3){
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
            String response = checkConnectionOUT("add/p_id%"+id+"/record%"+record,in,out);
            System.out.println(response);
            if (response.equals("Record added")){
                break;
            }
        }

    }


    public static  void main (String[] args) throws InterruptedException, IOException {
        BufferedReader input=null;
        PrintWriter out=null;
        boolean loginFalg= false;
        try {
            Socket socket = new Socket(SERVER, PORT);
            input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            loginFalg = loginStatus(input, out);

        }catch (IOException e){
            System.err.println(e);
        }


        if (loginFalg){
            System.out.println("If you want to search user, type 'Search'");
            System.out.println("If you want to add new patient user, type 'new'");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
//            String response = checkConnectionOUT(command+"/", input,out);
            if (command.contains("Search")){
                searchNumber(input, out);
            }
            else if(command.contains("new")){
                addPatient(input,out);
            }
        }
//        while (retry){
//            try {
//                if(loginFalg){
//                    while (true){
//                        System.out.println("If you want to search user, type 'Search'");
//                        System.out.println("If you want to add new patient user, type 'new'");
//                        String command = "new";
//                        Scanner scanner = new Scanner(System.in);
//                        checkConnectionOUT(input,out);
//                        //out.println(command);
//                        if (command.contains("Search")){
//                            searchNumber(input, out);
//                        }
//                        else if (command.contains("new")){
//
//                            while (true){
//                                String serevrResponse = input.readLine();
//                                System.out.println(serevrResponse);
//                                if (serevrResponse.contains("Record added")){
//                                    System.out.println("Break");
//                                    break;
//                                }
//                                if (!serevrResponse.equals("Wrong input") && !serevrResponse.contains("Patient added successfully")){
//                                    command = scanner.nextLine();
//                                    out.println(command);
//                                }
//
//                            }
//                        }
//
//                    }
//                }
//
//            } catch (IOException e) {
//                if (e.toString().contains("java.net.SocketException: Connection reset")){
//                    System.out.println("Server down");
//                    System.out.println("Retrying");
//                    TimeUnit.SECONDS.sleep(3);
//                    retryCount++;
//                    if (retryCount==3){
//                        System.out.println("Retry again? y/n");
//                        Scanner scanner = new Scanner(System.in);
//                        String command = scanner.nextLine();
//                        if (command.contains("n")){
//                            retry=false;
//                        }
//                        else{
//                            continue;
//                        }
//
//                    }
//
//                }
//            }
//        }

    }



}
