import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

            while (true){

                String request = in.readLine();
                System.out.println("Received : "+ request);
                if(request.contains("Login")){
                    userLogin();
                }
                else if(request.contains("Signup")){
                    userSignup();
                }
                else if(request.contains("Search") && loginflag){
                    numberSearch();
                }
                else if(request.contains("new") && loginflag){
                    addPatient();
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


    //Add new Record
    public  void  addRecord(int p_id) throws IOException, SQLException {
        while (true){
            out.println("Record description: ");
            String description = in.readLine();

            //check inputs
            boolean checkInputs = false;
            Validation validation = new Validation();
            if (validation.fullNameValidation(description)){
                checkInputs = true;
            }
            if(checkInputs){
                Record record = new Record(p_id,description,"");
                PatientData patientData = new PatientData();
                boolean result = patientData.newRecord(record);
                if (result){
                    out.println("Record added");
                    break;
                }
                else {
                    out.println("Something went wrong..");
                    break;
                }
            }
            else {
                out.println("Wrong input");
            }
        }
    }

    //Add new patient
    public  void  addPatient() throws IOException, SQLException {
        while (true){
            out.println("Enter patient name: ");
            String p_name = in.readLine();
            out.println("Enter patient phone number");
            String p_number = in.readLine();
            out.println("Enter patient age: ");
            String p_age = in.readLine();

            //check inputs
            boolean checkInputs = false;
            Validation validation = new Validation();
            if (validation.fullNameValidation(p_name)){
                if (validation.phoneValidation(p_number)){
                    if (validation.ageValidation(p_age)){
                        checkInputs = true;
                    }
                }
            }

            if(checkInputs){
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int newAge = Integer.parseInt(p_age);
                int bornYear = year - newAge;
                Patient patient = new Patient(p_name,0,bornYear,p_number);
                PatientData patientData = new PatientData();
                int p_id = patientData.addPatient(patient);
                if (p_id != 0){
                    out.println("Patient added successfully");
                    addRecord(p_id);
                    break;
                }
            }
            else {
                out.println("Wrong input");
            }
        }
    }

    //Login user
    public void userLogin() throws IOException, SQLException {
        while (true){

            String input = in.readLine();
            ObjectMapper mapper = new ObjectMapper();

            LoginStruct loginStruct = mapper.readValue(input, LoginStruct.class);
            System.out.println(loginStruct.getUsername());
            Login login = new Login(loginStruct.getUsername(),loginStruct.getPassword());
            if (login.isDbConnected()){
                if (login.checkLogin()){
                    loginflag = true;
                    out.println("Logged");
                    break;
                }
                else {
                    out.println("invalid");
                }
            }
            else {
                out.println("Something went wrong!..");
                System.out.println("Database not connected.");
            }
        }
    }

    //user sign up
    public void userSignup() throws IOException, SQLException {

        while (true){

            String input = in.readLine();
            System.out.println(input);
            ObjectMapper mapper = new ObjectMapper();

            LoginStruct loginStruct = mapper.readValue(input, LoginStruct.class);
            System.out.println(loginStruct.getUsername());

            Signup signup = new Signup(loginStruct.getName(), loginStruct.getUsername(), loginStruct.getPassword());
            if (signup.isDbConnected()){
                if (signup.addUser()){
                    loginflag = true;
                    out.println("Logged");
                    break;
                }
                else {
                    out.println("invalid");
                }
            }
            else {
                out.println("Something went wrong!..");
                System.out.println("Database not connected.");
            }
        }

//        while (true){
//            out.println("Name: ");
//            String name = in.readLine();
//            out.println("Username: ");
//            String username = in.readLine();
//            out.println("Password:");
//            String password = in.readLine();
//            Signup signup = new Signup(name,username, password);
//            Validation val = new Validation();
//
//                if (signup.isDbConnected()){
//                    out.println("DB connected..");
//                    if (val.fullNameValidation(name) && val.usernameValidation(username) && val.passwordValidation(password)) {
//                        boolean resultSignup = signup.addUser();
//                        if (resultSignup) {
//                            loginflag = true;
//                            out.println("Signup success");
//                            //go to login..
//                            //userLogin();
//                            break;
//                        } else {
//                            out.println("Something went wrong..");
//                        }
//                    }
//                    else {
//                        out.println("check your inputs");
//                    }
//                }
//                else {
//                    out.println("Database disconnected..");
//                }
//
//        }
    }

    //using for search patient numbers..
    public void numberSearch() throws IOException, SQLException {
//        List<Patient> data = new ArrayList<Patient>();
        boolean flag = true;
        while (flag){
            out.println("Enter patient phone number: ");
            String phone = in.readLine();
            Validation validation = new Validation();
            if (validation.phoneValidation(phone)){
                flag=false;
                PatientData patientData = new PatientData();
                List<Patient> data = patientData.customerSet(phone);
                if (data.isEmpty()){
                    out.println("Phone number is not exist");
                }
                else {
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    for (int x = 0; x<data.size(); x++){
                        int age = year - data.get(x).year;
                        out.println(data.get(x).name+ " "+age + "  "+ data.get(x).id);
                    }
                    out.println("Enter patient Id to get records: ");
                    String id = in.readLine();
                    int p_id = Integer.parseInt(id);
                    //checking input value is contains in the array or not
                    boolean checkId = false;
                    String pName = "";
                    int pAge= 0;
                    for (int x = 0; x<data.size(); x++){
                        if (data.get(x).id == p_id){
                            pName = data.get(x).name;
                            pAge = year-data.get(x).year;
                            checkId = true;
                        }
                    }
                    if (checkId){
                        out.println("Patient: "+ pName+ "   "+ "Age: "+ pAge + " Id: "+p_id);
                        List<Record> records = patientData.fetchRecords(p_id);
                        if (records.isEmpty()){
                            out.println("Add records -> Enter 'add'");
                            out.println("Exit records -> press 'n'");
                            String input = in.readLine();
                            System.out.println(input);
                            if (input.contains("n")){
                                continue;
                            }
                            else if (input.contains("add")){
                                addRecord(p_id);
                            }
                        }
                        else {
                            for (int x = 0; x<records.size(); x++){
                                out.println(records.get(x).date);
                                out.println(records.get(x).record);
                                out.println("----------------------------------------------");
                                if (x+1 < records.size()){
                                    out.println("See next record-> press 'y'");
                                }
                                out.println("Add records -> Enter 'add'");
                                out.println("Exit records -> press 'n'");

                                String input = in.readLine();
                                System.out.println(input);
                                if (input.contains("n")){
                                    break;
                                }
                                else if (input.contains("y") && x+1 < records.size()){
                                    continue;
                                }
                                else if (input.contains("add")){
                                    addRecord(p_id);
                                }
                            }
                        }
                    }
                    else {
                        out.println("Wrong input");
                    }
                }

            }
            else {
                out.println("Recheck Phone number");
            }
        }


    }

}
