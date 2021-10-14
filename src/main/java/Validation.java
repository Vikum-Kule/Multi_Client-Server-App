public class Validation {

    Validation(){

    }

    public boolean passwordValidation( String input){
        if(input.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    public  boolean usernameValidation( String input){
        if(input.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean fullNameValidation( String input){
        if(input.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean phoneValidation( String phone){
        if (!phone.matches("[0-9]+") && phone.length()!=10) {
            return false;
        }
        else {
            return true;
        }
    }
    public boolean ageValidation(String age ){
        if (!age.matches("[0-9]+") && age.length()!=3) {
            return false;
        }
        else {
            return true;
        }
    }

}
