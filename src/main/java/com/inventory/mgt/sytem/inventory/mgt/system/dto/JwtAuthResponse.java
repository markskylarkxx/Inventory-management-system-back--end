package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String access_token;
    private  String token_type = "Bearer ";

    public  JwtAuthResponse(String access_token){
        this.access_token = access_token;
    }
}
