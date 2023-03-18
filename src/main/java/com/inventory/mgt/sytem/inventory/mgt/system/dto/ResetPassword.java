package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassword {
    private  String email;
    private  String token;
    private  String newPassword;
    private String confirmPassword;

}
