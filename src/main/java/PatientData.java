import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientData {
    Connection connection;
    PatientData(){
        connection = Database.ConnectDB();
        if (connection == null){
            System.exit(1);
        }
    }

    //return set of customers according to phone number
    public List<Patient> customerSet(String phone){
        String patientName = "";
        int id = 0;
        int year = 0;
        List<Patient> data = new ArrayList<Patient>();
        try {
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //Connection con = DriverManager.getConnection(connectionUrl);
            String query = "select p_id,p_name,born_year from patient where phone = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, phone);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                System.out.println("have");
                patientName = set.getString("p_name");
                System.out.println(patientName);
                id = set.getInt("p_id");
                year = set.getInt("born_year");
                data.add( new Patient(patientName, id, year,phone));
            }

            statement.close();
            set.close();
            for(int i = 0; i < data.size(); i++){
                System.out.println(data.get(i).toString());
            }

            // Return the List
            return data;

        } catch (SQLException ex) {
            return null;
        }
    }

    public List<RecordFetch> fetchRecords(int p_id){
        String record = "";
        int id = 0;
        String date = null;
        String d_name=null;
        List<RecordFetch> data = new ArrayList<RecordFetch>();
        try {

//            String query = "select date,Record from patientRecords where patient_id = ?";
            String query = "SELECT date,Record,full_name FROM patientRecords " +
                    "INNER JOIN user ON user.user_id =patientRecords.doctorId where patient_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, p_id);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                System.out.println("have");
                record = set.getString("Record");
                date = set.getString("date");
                d_name = set.getString("full_name");

                System.out.println(date);
                data.add( new RecordFetch(p_id,record,date,d_name));
            }

            statement.close();
            set.close();

            // Return the List
            return data;

        } catch (SQLException ex) {
            return null;
        }
    }

    //add patient..
    public int addPatient(Patient patient) throws SQLException{

        try {

            String query = "INSERT INTO patient(p_name, born_year, phone) VALUES(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,patient.name );
            statement.setInt(2, patient.year);
            statement.setString(3, patient.phone);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int generatedKey = 0;
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
            statement.close();
            return generatedKey;

        } catch (SQLException ex) {
            System.out.println(ex);
            return 0;
        }

    }

    //add record..
    public boolean newRecord(Record record) throws SQLException{

        try {

            String query = "INSERT INTO patientRecords(patient_id, Record, doctorId, date) VALUES(?,?,?,CURRENT_DATE)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, record.patient_id);
            statement.setString(2, record.record);
            statement.setInt(3, record.d_id);
            statement.executeUpdate();
            statement.close();
            return true;

        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }

    }
}
