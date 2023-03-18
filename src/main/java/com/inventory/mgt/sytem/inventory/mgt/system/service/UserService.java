package com.inventory.mgt.sytem.inventory.mgt.system.service;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.SetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.Deactivate;
import com.inventory.mgt.sytem.inventory.mgt.system.dto.SignupRequest;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.RoleName;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.extras.PasswordGeneration;
import com.inventory.mgt.sytem.inventory.mgt.system.extras.PasswordValidation;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Deactivation;
import com.inventory.mgt.sytem.inventory.mgt.system.model.LoginHistory;
import com.inventory.mgt.sytem.inventory.mgt.system.model.Role;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.DeactivationRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.LoginHistoryRepo;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.RoleRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {
    @Value("${app.sender}")
    private String sender;
    final int size = 10;
     @Autowired
    UserRepository repository;
     @Resource
    JavaMailSender mailSender;
     @Autowired
     DeactivationRepository deactivationRepository;
     @Autowired
    RoleRepository roleRepository;

     @Autowired
    PasswordEncoder encoder;
     @Autowired
    LoginHistoryRepo loginHistoryRepo;

    public Page<User> getAllUsers(Integer pageNumber, Integer pageSize) {
       PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").ascending());
        Page<User> page =repository.findAll(pageRequest);


        return page;
    }

    public User getUser(Long id) {
        Optional<User> optionalUser = repository.findById(id);
        return optionalUser.isPresent()? optionalUser.get() : null;
    }




    public String deactivateUser(Deactivate deactivate, Long id, Principal principal) {
        //todo; find the user you want to deactivate by the user id;
        Optional<User> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new AppException("user not found");
        } else {
            User u = optional.get();
            //todo check role of the user;
            Boolean adminRole= u.getRoles().stream().
                    map(m -> m.getRoleName() == RoleName.ROLE_ADMIN).
                    findFirst().get();
            if (adminRole == Boolean.FALSE) {
                throw new AppException(" role not admin");
            } else {


                //todo; proceed to  disable the admin
                u.setActive(Boolean.FALSE);
                log.info("Admin has been deactivated!");
                repository.save(u);
                Deactivation deactivation = Deactivation.
                        builder().reason_for_deactivation(deactivate.getDeactivationReason()).
                        userId(u).date(LocalDateTime.now()).username(principal.getName()).build();
                //save in deactivation table;
                deactivationRepository.save(deactivation);
            }
            return "Admin has been deactivated";
        }
    }


    public String activateUser(Long id) {
        Deactivation deactivation = deactivationRepository.findById(id).orElse(null);
        User user = deactivation.getUserId();
        user.setActive(Boolean.TRUE);
        repository.save(user);
        //delete the row;
        deactivationRepository.delete(deactivation);
   return "Admin has been activated";
    }

    public User getLoggedInUser(Principal principal) {
       return repository.findUserByUsername(principal.getName()).get();
    }

    public String createUser(SignupRequest request, Principal principal) {


        Optional<User> optional = repository.findUserByEmail(request.getEmail());
        if (optional.isPresent()) {
            throw new AppException("email already exist");
        }
        Optional<User> username = repository.findUserByUsername(request.getUsername());
        if (username.isPresent()) {
            throw new AppException("username already exist");
        }
        User user = new User(
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUserPhoneNo(),
                request.getUserAddress(),
                request.getGender());

        //todo; generate an automatic password;

        String plainPassword = PasswordGeneration.randomPassword(size);

        //String plainPassword = "1234567";

        // todo; encode this plain password;

        String encodedPassword = encoder.encode(plainPassword);
        //save this password
        user.setPassword(encodedPassword);

        // todo; assign admin role to the user;

        Role role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN).get();
        user.setRoles(Collections.singleton(role));
        log.info("admin role has been assigned to user with username " + user.getUsername());
        user.setActive(Boolean.FALSE);
        repository.save(user);

        // email the user to reset their password;
        // create  url link, send the plain text password to the user's email
        String appUrl = "http://localhost:4000/api/auth/reset?password= " + plainPassword;

        //Todo; create an email message
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(user.getEmail());
            message.setText("To reset your password," +
                    "  please  click here\n: " + appUrl);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("message sent......");

        return "A password reset link has been  sent to " + user.getEmail();
    }


    public String resetPassword(SetPassword setPassword) {
        //check if this user exist
        User user = repository.findUserByEmail(setPassword.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

           String password = user.getPassword();
           String oldPassword = setPassword.getOldPassword();
           password = oldPassword;

                //compare the plain password sent to the user's email and the encoded password;
        boolean matches = encoder.matches(setPassword.getOldPassword(), user.getPassword());

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
                        log.error("Password is invalid!");
                        throw  new AppException("Password is invalid! \n password should contain " +
                                "\n at least one uppercase character" +
                                " \n at least one lower case character " +
                                "\n at least one digit \n at least one special character" +
                                "\n minimum of 6  and maximum of 10 characters");
                    }
                    // update the password
                    user.setPassword(encoder.encode(setPassword.getNewPassword()));

                   user.setActive(Boolean.TRUE);
                   repository.save(user) ;
                   return  "Congratulations! Your password has been successfully reset!";

    }

    private  boolean verifyOldAndNewPassword(String newPassword, String oldPassword) {
       return Objects.equals(newPassword, oldPassword);
    }
    private boolean verifyNewAndConfirmPassword(String newPassword, String confirmPassword){
        return Objects.equals(newPassword, confirmPassword);
    }


    public List<LoginHistory> adminLoginHistory() {
       return  loginHistoryRepo.findAll();
    }


}

