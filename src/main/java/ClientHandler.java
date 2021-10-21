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
                else if(link[0].contains("Search")){
                    System.out.println("come search");
                    numberSearch(link);
                }
                else if(link[0].contains("new")){
                    addPatient(link);
                }
                else if(link[0].contains("add")){
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
        String[] d_idSet = link[3].split("%");
        int d_id = Integer.parseInt(d_idSet[1]);

        Record record = new Record(p_id,recordSet[1], "", d_id);

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
                String result = login.checkLogin();
                String[] resultSet = result.split("/");
                if (resultSet[0].contains("true")){
                    loginflag = true;
                    out.println("Logged/"+resultSet[1]);
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
                String result = signup.addUser();
                String[] resultSet = result.split("/");
                if (resultSet[0].contains("true")){
                    loginflag = true;
                    out.println("Logged/"+resultSet[1]);
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
                List<RecordFetch> records = patientData.fetchRecords(p_id);
                String recordsToJson = objectMapper.writeValueAsString(records);
                System.out.println(recordsToJson);
                out.println(recordsToJson);
            }

        }




}
