package com.inventory.mgt.sytem.inventory.mgt.system.controller;


import com.inventory.mgt.sytem.inventory.mgt.system.dto.SetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.LoginRequest;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.SignupRequest;
import com.inventory.mgt.sytem.inventory.mgt.system.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
     private AuthenticationService service;

    // SIGN UP USER
    @PostMapping("/signup")
    public  ResponseEntity<?> signUp(@Valid @RequestBody SignupRequest request){
        return ResponseEntity.ok(service.signUpUser(request));
    }

    //COMPLETE REGISTRATION

    @PostMapping("/set_password")
    public ResponseEntity<?> createPassword(@RequestBody SetPassword createPassword){
        return  new ResponseEntity<>(service.createPassword(createPassword), HttpStatus.CREATED);
    }


       // LOGIN
    @PostMapping("/login")
    public  ResponseEntity<?> login(@RequestBody LoginRequest request){
        return  ResponseEntity.ok(service.authenticateUser(request));
    }

}
