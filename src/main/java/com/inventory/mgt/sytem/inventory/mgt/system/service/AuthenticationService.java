package com.inventory.mgt.sytem.inventory.mgt.system.service;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.*;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.extras.PasswordGeneration;
import com.inventory.mgt.sytem.inventory.mgt.system.extras.PasswordValidation;
import com.inventory.mgt.sytem.inventory.mgt.system.model.LoginHistory;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Role;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.LoginHistoryRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.RoleRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
//import com.inventory.mgt.sytem.inventory.mgt.system.utils.TokenProvider;
import com.inventory.mgt.sytem.inventory.mgt.system.session.SessionService;
import com.inventory.mgt.sytem.inventory.mgt.system.utils.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@Configuration

@Slf4j
public class AuthenticationService {
    final int size = 10;

    @Value("${app.sender}")
    private String sender;
  @Autowired
    UserRepository repository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  LoginHistoryRepo loginHistoryRepo;
  @Autowired
  PasswordEncoder encoder;
  @Autowired
    SessionService sessionService;

  @Autowired
    JavaMailSender mailSender;
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  TokenProvider provider;
           //SIGNUP
   public String signUpUser(SignupRequest request) {


      // check if user already exist;
      Optional<User> username = repository.findUserByUsername(request.getUsername());
      if (username.isPresent()){
        throw  new AppException("Sorry, username already exist!");
      }

      Optional<User> email = repository.findUserByEmail(request.getEmail());
      if (email.isPresent()){
        throw new AppException("Sorry, email already exist!");
      }

      // REGISTER THE USER
      User user = new User(request.getUsername(),
              request.getFirstName(),
              request.getLastName(),
              request.getEmail(),
              request.getUserPhoneNo(),
              request.getUserAddress(),
              request.getGender());

       //todo; generate an automatic password;
      String plainPassword = PasswordGeneration.randomPassword(size);




       // todo; encode this plain password;

       String encodedPassword = encoder.encode(plainPassword);
       //save this password
       user.setPassword(encodedPassword);


       // todo; assign User role to the new user;

       Role role = roleRepository.findByRoleName(RoleName.ROLE_USER).get();
       user.setRoles(Collections.singleton(role));

       log.info("User role has been assigned to user with username " + request.getUsername());
       user.setActive(Boolean.FALSE);
       repository.save(user);

       // email the user to reset their password;
       // create  url link, send the plain text password to the user's email
       String appUrl = "http://localhost:4000/api/auth/reset?password= " + plainPassword;

       //Todo; create an email message
       try {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(sender);
           message.setTo(request.getEmail());
           message.setText("To complete your registration" +
                   "  please  click here\n: " + appUrl);

           mailSender.send(message);

       } catch (Exception e) {
           e.printStackTrace();
       }
       System.out.println("message sent......");

       return "A registration link has been sent to " +request.getEmail();
   }
      // COMPLETE USER REGISTRATION(SET PASSWORD)
    public String createPassword(SetPassword setPassword) {
        //check if this user exist
        User user = repository.findUserByEmail(setPassword.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email is Incorrect. User not found!"));
       // log.error("Incorrect Email");


        String password = user.getPassword();

        String oldPassword =setPassword.getOldPassword();
        password = oldPassword;

        //compare the plain password sent to the user's email and the encoded password;
        boolean matches = encoder.matches(setPassword.getOldPassword(), user.getPassword());
        System.out.println(">>>" + matches);
        if (matches == Boolean.FALSE){
            log.warn("password do not match");
            return "password do not match!";
        }

        // check the old and new password, to see if they match.
        if    (verifyOldAndNewPassword(setPassword.getOldPassword(), setPassword.getNewPassword())){
            log.warn("old and new password should not match");
            throw  new AppException("Old and new password cannot be the same!");
        }

        // check if new password and confirm  password match;
        if (!verifyNewAndConfirmPassword(setPassword.getNewPassword(), setPassword.getConfirmPassword())){
            boolean equals = setPassword.getNewPassword().equals(setPassword.getOldPassword());
            System.out.println(equals);
            log.warn("new and confirm password do not match!");
            throw  new AppException("new and confirm password do not match");
        }
        // validate the new password;
        if (!PasswordValidation.validatePassword(setPassword.getNewPassword())){
            log.error("Password is invalid! \n password should contain \n at least one uppercase character \n" +
                    " at least one lower case character \n at least one digit \n at least one special character" +
                    "\n minimum of 6 and maximum of 10 characters");
            throw  new AppException("password is invalid! \n password should contain \n at least one uppercase character" +
                    " \n at least one lower case character \n at least one digit \n at least one special character" +
                    "\n minimum of 6 and maximum of 10 characters");
        }
        // update the password
        user.setPassword(encoder.encode(setPassword.getNewPassword()));

        user.setActive(Boolean.TRUE);
        repository.save(user) ;
        log.info("You have been successfully registered.");
        return  "Congratulations!  You have been registered.";

    }

    private  boolean verifyOldAndNewPassword(String newPassword, String oldPassword) {
        return Objects.equals(newPassword, oldPassword);
    }
    private boolean verifyNewAndConfirmPassword(String newPassword, String confirmPassword){
        return Objects.equals(newPassword, confirmPassword);
    }



    // AUTHENTICATION
  public JwtAuthResponse authenticateUser(LoginRequest request) {


   Authentication authentication =
           authenticationManager.authenticate(new
                   UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      User user = repository.findUserByUsername(request.getUsername()).get();

    if (authentication.isAuthenticated()) {
      System.out.println(authentication.isAuthenticated());
      SecurityContextHolder.getContext().setAuthentication(authentication);

          //GET ROLE OF LOGGED IN USER
        SimpleGrantedAuthority authority = authentication.getAuthorities().
                stream().map(role -> new SimpleGrantedAuthority(role.getAuthority())).findFirst().get();

        //  GENERATE THE  JW_TOKEN
      String token = provider.generateJwtToken(authentication);
      log.info("user with role " +  authority + " and username " + user.getUsername() + ", has logged in");



          // LOGIN HISTORY FOR ADMINS ONLY!
      boolean hasAdminRole = authentication.getAuthorities().
              stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
      if (hasAdminRole == Boolean.TRUE){

        LoginHistory loginHistory = LoginHistory.builder().
                success(Boolean.TRUE).username(user.getUsername()).
                email(user.getEmail()).loggedInTime(LocalDateTime.now()).build();
        loginHistoryRepo.save(loginHistory);
      }
         sessionService.startSession(user);

      return new JwtAuthResponse(token);

    }
    return  null;
  }


}
