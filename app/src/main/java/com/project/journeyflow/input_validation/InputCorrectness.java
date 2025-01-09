package com.project.journeyflow.input_validation;

import android.util.Patterns;

import java.util.Date;

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

    /*
    public boolean checkDistanceCorrectness(Double distanceFrom, Double distanceTo){

    }

    public boolean checkDurationCorrectness(Double durationFrom, Double durationTo){

    }

    public boolean checkDateCorrectness(Date dateFrom, Date dateTo){

    }

     */
}
