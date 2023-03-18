package com.inventory.mgt.sytem.inventory.mgt.system.extras;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidation {
    public static  boolean validatePassword(String password){

        String regex =  "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{6,10}$";

        // compile this regex
        Pattern pattern = Pattern.compile(regex);

        // match this password with the given regex
        Matcher matcher = pattern.matcher(password);
        boolean matches = matcher.matches();
        return  matches;
    }

}
