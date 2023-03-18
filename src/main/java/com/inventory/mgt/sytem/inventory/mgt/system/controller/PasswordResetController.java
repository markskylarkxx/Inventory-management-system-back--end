package com.inventory.mgt.sytem.inventory.mgt.system.controller;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.ResetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping(value = "/forgot")
    public String forgotPass(@RequestBody User user){

        return  passwordResetService.forgotPass(user);
    }



    @PutMapping(value = "/resetPassword")
    public  String resetPassword(@RequestBody ResetPassword resetPassword ){
        return  passwordResetService.resetPassword(resetPassword);
    }
}
