import java.util.Date;

public class Record {
    int patient_id;
    String record;
    String date;
    int d_id;

    public  Record(int id,String record,String date, int d_id){
        this.date = date;
        this.record = record;
        this.patient_id = id;
        this.d_id = d_id;
    }
}
