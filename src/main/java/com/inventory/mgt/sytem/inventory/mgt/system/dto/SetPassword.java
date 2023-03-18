package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetPassword {
    private String oldPassword;
    private  String newPassword;
    private String confirmPassword;
     private String email;
}
