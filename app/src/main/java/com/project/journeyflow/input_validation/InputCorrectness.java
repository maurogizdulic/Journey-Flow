package com.project.journeyflow.input_validation;

import android.util.Patterns;

public class InputCorrectness {

    public boolean checkPasswordCorrectness(String password){

        if (password.length() <= 7){
            return false;
        }
        else {
            String pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!?.,/()*:;<>{}]).+$";
            return password.matches(pattern);
        }
    }

    public boolean checkEmailCorrectness(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean checkPasswordAndConfirmPasswordCorrectness(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
}
