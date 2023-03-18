package com.inventory.mgt.sytem.inventory.mgt.system.extras;

import java.util.Random;

public class TokenGeneration {
    public  static   String getTokenSize(int n){
        String token = "123456789";
        Random  random = new Random();

        StringBuffer sb = new StringBuffer(n);
        for (int i=0; i < n; i++){
            int index = random.nextInt(9);
            sb.append(token.charAt(index));
        }
        return sb.toString();
    }
}
