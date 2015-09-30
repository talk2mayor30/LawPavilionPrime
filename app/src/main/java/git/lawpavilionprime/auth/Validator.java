package git.lawpavilionprime.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by don_mayor on 7/24/2015.
 */
public class Validator {



    public static String emailErrorMessage;
    public static String contactErrorMessage;
    public static String passwordErrorMessage;
    public static String defaultErrorMessage;
    public static String stateErrorMessage;

    public boolean isValidEmail(String emailAddress){

        if(isEmpty(emailAddress)){
            emailErrorMessage = "Email is empty";
            return false;
        }

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (!matcher.matches()) {
            emailErrorMessage = "Email Address is invalid";
            return false;
        }

        return true;
    }

    public boolean isEmpty(String data){

        if(data.isEmpty() || data == null){
            defaultErrorMessage = "Field cannot be empty";
            return true;
        }

        return false;
    }

    public boolean stateSelected(String data){

        if(data.isEmpty() || data.equalsIgnoreCase("Select states")){
            stateErrorMessage = "State is not selected";
            return false;
        }

        return true;
    }

//    public boolean isValidContact(String data){
//
//        if(isEmpty(data)){
//            errorMessage = "Contact is empty";
//            return false;
//        }
//
//        return true;
//    }

    public boolean passwordConfirmed(String password, String confirmPassword){

        if(!password.equals(confirmPassword)){
            passwordErrorMessage = "Password and confirm password do not match";
            return false;
        }


        return true;
    }
}
