public class RecordFetch {
    int patient_id;
    String record;
    String date;
    String d_name;

    public  RecordFetch(int id,String record,String date, String d_name){
            this.date = date;
            this.record = record;
            this.patient_id = id;
            this.d_name = d_name;
        }
}
