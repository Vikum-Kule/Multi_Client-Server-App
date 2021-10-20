import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
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
                System.out.println(request);
                String[] link = request.split("/");
                System.out.println("Received : "+ link[0]);

                if(link[0].contains("Login")){
                    userLogin(link);
                }
                else if(link[0].contains("Signup")){
                    userSignup(link);
                }
                else if(link[0].contains("Search") && loginflag){
                    System.out.println("come search");
                    numberSearch(link);
                }
                else if(link[0].contains("new") && loginflag){
                    addPatient(link);
                }
                else if(link[0].contains("add") && loginflag){
                    addRecord(link);
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
    public  void  addRecord(String[] link) throws IOException, SQLException {
        String[] p_idSet = link[1].split("%");
        int p_id = Integer.parseInt(p_idSet[1]);
        String[] recordSet = link[2].split("%");
        Record record = new Record(p_id,recordSet[1], "");

        PatientData patientData = new PatientData();
        boolean result = patientData.newRecord(record);
        if(result){
            out.println("Record added");
        }
        else {
            out.println("Something went wrong");
        }

    }

    //Add new patient
    public  void  addPatient(String[] link) throws IOException, SQLException {

        String input = link[1];
        System.out.println(input);
        ObjectMapper mapper = new ObjectMapper();

        PatientStructServer patient = mapper.readValue(input, PatientStructServer.class);
        String p_name = patient.getName();
        String p_age = patient.getAge();
        String p_phone = patient.getPhone();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int newAge = Integer.parseInt(p_age);
        int bornYear = year - newAge;
        Patient patientAdd = new Patient(p_name,0,bornYear,p_phone);
        PatientData patientData = new PatientData();
        int p_id = patientData.addPatient(patientAdd);
        if (p_id != 0){
            out.println("Patient added successfully/p_id%"+p_id);
        }
        else {
            out.println("Something went wrong...");
        }

    }

    //Login user
    public void userLogin(String[] link) throws IOException, SQLException {
            for (int x=0; x<link.length; x++){
                System.out.println(link[x]);
            }
            ObjectMapper mapper = new ObjectMapper();

            LoginStruct loginStruct = mapper.readValue(link[1], LoginStruct.class);
            System.out.println(loginStruct.getUsername());
            Login login = new Login(loginStruct.getUsername(),loginStruct.getPassword());
            if (login.isDbConnected()){
                if (login.checkLogin()){
                    loginflag = true;
                    out.println("Logged");
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

    //user sign up
    public void userSignup(String[] link) throws IOException, SQLException {


            String input = link[1];
            System.out.println(input);
            ObjectMapper mapper = new ObjectMapper();

            LoginStruct loginStruct = mapper.readValue(input, LoginStruct.class);
            System.out.println(loginStruct.getUsername());

            Signup signup = new Signup(loginStruct.getName(), loginStruct.getUsername(), loginStruct.getPassword());
            if (signup.isDbConnected()){
                if (signup.addUser()){
                    loginflag = true;
                    out.println("Logged");
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

    //using for search patient numbers..
    public void numberSearch(String []link) throws IOException, SQLException {
//        List<Patient> data = new ArrayList<Patient>();
        ObjectMapper objectMapper = new ObjectMapper();
        PatientData patientData = new PatientData();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        if (link.length==2) {
            String[] Num_inputs = link[1].split("%");
            String phone = Num_inputs[1];
            List<Patient> data = patientData.customerSet(phone);
            if (data.isEmpty()) {
                out.println("Phone number is not exist");
            } else {
                //Set pretty printing of json
                //objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


                //1. Convert List of Person objects to JSON
                String arrayToJson = objectMapper.writeValueAsString(data);
                System.out.println("1. Convert List of person objects to JSON :");
                System.out.println(arrayToJson);
                out.println(arrayToJson);
            }

        }else {
                //patient id -> get records
                String[] id_inputs = link[2].split("%");
                String id = id_inputs[1];
                int p_id = Integer.parseInt(id);
                List<Record> records = patientData.fetchRecords(p_id);
                String recordsToJson = objectMapper.writeValueAsString(records);
                System.out.println(recordsToJson);
                out.println(recordsToJson);
            }

        }

//        boolean flag = true;
//        while (flag){
//            out.println("Enter patient phone number: ");
//            String phone = in.readLine();
//            Validation validation = new Validation();
//            if (validation.phoneValidation(phone)){
//                flag=false;
//                PatientData patientData = new PatientData();
//                List<Patient> data = patientData.customerSet(phone);
//                if (data.isEmpty()){
//                    out.println("Phone number is not exist");
//                }
//                else {
//                    int year = Calendar.getInstance().get(Calendar.YEAR);
//                    for (int x = 0; x<data.size(); x++){
//                        int age = year - data.get(x).year;
//                        out.println(data.get(x).name+ " "+age + "  "+ data.get(x).id);
//                    }
//                    out.println("Enter patient Id to get records: ");
//                    String id = in.readLine();
//                    int p_id = Integer.parseInt(id);
//                    //checking input value is contains in the array or not
//                    boolean checkId = false;
//                    String pName = "";
//                    int pAge= 0;
//                    for (int x = 0; x<data.size(); x++){
//                        if (data.get(x).id == p_id){
//                            pName = data.get(x).name;
//                            pAge = year-data.get(x).year;
//                            checkId = true;
//                        }
//                    }
//                    if (checkId){
//                        out.println("Patient: "+ pName+ "   "+ "Age: "+ pAge + " Id: "+p_id);
//                        List<Record> records = patientData.fetchRecords(p_id);
//                        if (records.isEmpty()){
//                            out.println("Add records -> Enter 'add'");
//                            out.println("Exit records -> press 'n'");
//                            String input = in.readLine();
//                            System.out.println(input);
//                            if (input.contains("n")){
//                                continue;
//                            }
//                            else if (input.contains("add")){
//                                addRecord(p_id);
//                            }
//                        }
//                        else {
//                            for (int x = 0; x<records.size(); x++){
//                                out.println(records.get(x).date);
//                                out.println(records.get(x).record);
//                                out.println("----------------------------------------------");
//                                if (x+1 < records.size()){
//                                    out.println("See next record-> press 'y'");
//                                }
//                                out.println("Add records -> Enter 'add'");
//                                out.println("Exit records -> press 'n'");
//
//                                String input = in.readLine();
//                                System.out.println(input);
//                                if (input.contains("n")){
//                                    break;
//                                }
//                                else if (input.contains("y") && x+1 < records.size()){
//                                    continue;
//                                }
//                                else if (input.contains("add")){
//                                    addRecord(p_id);
//                                }
//                            }
//                        }
//                    }
//                    else {
//                        out.println("Wrong input");
//                    }
//                }
//
//            }
//            else {
//                out.println("Recheck Phone number");
//            }
//        }




}
