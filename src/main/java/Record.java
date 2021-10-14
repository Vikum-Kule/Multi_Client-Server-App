import java.util.Date;

public class Record {
    int patient_id;
    String record;
    String date;

    public  Record(int id,String record,String date){
        this.date = date;
        this.record = record;
        this.patient_id = id;
    }
}
