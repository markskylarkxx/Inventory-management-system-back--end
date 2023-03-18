package com.inventory.mgt.sytem.inventory.mgt.system.extras;

import java.util.Random;

public class PasswordGeneration {
    private static   String password = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$&@?|%#";
    private  static  Random random = new Random();

    public  static String randomPassword(int n){
         StringBuffer stringBuffer = new StringBuffer(n);
         for (int i = 0; i < n; i++){
             stringBuffer.append(password.charAt(random.nextInt(password.length())));
         }
         return stringBuffer.toString();
    }


}
