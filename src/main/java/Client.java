import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
                        out.println(command);
                        String loginInputs = "{\"name\":\""+username+"\", \"username\":\""+username+"\", \"password\":\""+password+"\"}";
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
                        String SignupInputs = "{\"name\":\""+name+"\", \"username\":\""+username+"\", \"password\":\""+password+"\"}";
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

//        while (true){
//            String sereverResponse = in.readLine();
//            System.out.println(sereverResponse);
//            if(sereverResponse.contains("DB connected..")){
//                sereverResponse = in.readLine();
//                System.out.println(sereverResponse);
//                if(sereverResponse.equals("Login success") || sereverResponse.equals("Signup success")){
//                    return true;
//                }
//            }
//            if (!sereverResponse.equals("check your inputs") && !sereverResponse.equals("Invalid input") ){
//                Scanner scanner = new Scanner(System.in);
//                String command = scanner.nextLine();
//                if (command.equals("exit")){
//                    return false;
//                }
//                out.println(command);
//            }
//
//        }
    }

    public static void searchNumber(BufferedReader input, PrintWriter out) throws IOException{
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true){

            String sereverResponse = input.readLine();
            if (sereverResponse.equals(null)){
                break;
            }
            System.out.println(sereverResponse);
            if (sereverResponse.contains("Enter patient phone number: ")){
                command = scanner.nextLine();
                out.println(command);
            }
            else if(sereverResponse.equals("Phone number is not exist")|| sereverResponse.equals("Record added")|| sereverResponse.equals("Something went wrong..")){
                break;
            }
            else if (sereverResponse.equals("Enter patient Id to get records: ")|| sereverResponse.equals("Record description: ")|| sereverResponse.equals("Wrong input")){
                command = scanner.nextLine();
                out.println(command);
            }
            else if (sereverResponse.contains("Exit records -> press 'n'")){
                command = scanner.nextLine();
                if (command.contains("y") || command.contains("add")){
                    out.println(command);
                }
                else if (command.contains("n")){
                    break;
                }

            }

        }
    }

    public static  void main (String[] args){
        try {
            Socket socket = new Socket(SERVER, PORT);

            BufferedReader input = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //String sereverResponse = input.readLine();
            boolean loginFalg = loginStatus(input, out);
            if(loginFalg){
                while (true){
                    System.out.println("If you want to search user, type 'Search'");
                    System.out.println("If you want to add new patient user, type 'new'");
                    Scanner scanner = new Scanner(System.in);
                    String command = scanner.nextLine();
                    out.println(command);
                    if (command.contains("Search")){
                        searchNumber(input, out);
                    }
                    else if (command.contains("new")){
                        while (true){
                            String serevrResponse = input.readLine();
                            System.out.println(serevrResponse);
                            if (serevrResponse.contains("Record added")){
                                System.out.println("Break");
                                break;
                            }
                            if (!serevrResponse.equals("Wrong input") && !serevrResponse.contains("Patient added successfully")){
                                command = scanner.nextLine();
                                out.println(command);
                            }


                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
