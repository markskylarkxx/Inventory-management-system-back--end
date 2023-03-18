package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotEmpty(message = "username cannot be empty")
    private String username;
    @NotBlank(message = "firstname cannot be empty or null")
    private String firstName;
    @NotBlank(message = "lastname cannot be empty or null")
    private String lastName;
    private String password;
    @NotBlank(message = "email cannot be empty or null")
    @Email(message = "Invalid email address")
    private String email;
   @NotNull()
   @Pattern(regexp = "^\\d{11}$", message = "invalid phone number")
    private String userPhoneNo;
    private String  userAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private  Character gender;
}
