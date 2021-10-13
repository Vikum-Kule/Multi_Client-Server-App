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

}
