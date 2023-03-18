package com.inventory.mgt.sytem.inventory.mgt.system.service.impl;

import com.inventory.mgt.sytem.inventory.mgt.system.dto.ResetPassword;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.AppException;
import com.inventory.mgt.sytem.inventory.mgt.system.exception.ResourceNotFoundException;
import com.inventory.mgt.sytem.inventory.mgt.system.extras.PasswordValidation;
import com.inventory.mgt.sytem.inventory.mgt.system.model.PasswordResetToken;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.TokenRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.repository.UserRepository;
import com.inventory.mgt.sytem.inventory.mgt.system.service.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {


    @Value("${app.token}")
    private String resetToken;
    @Value("${app.sender}")
    private String sender;
   private   static LocalDateTime getTokenExpiryTime(){
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(2);
        return dateTime;

    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;
    @Override
    public String forgotPass(User user) {

       // look up the user in the dbase
        Optional<User> existingUser = userRepository.findUserByEmail(user.getEmail());
        System.out.println(LocalDateTime.now());
        if (!existingUser.isPresent()){
            throw  new ResourceNotFoundException("No user found with email " + user.getEmail());
        }else {
            //get the user;
            User presentUser = existingUser.get();
            //todo; get list of unused token from the token table and delete them;
            //List<PasswordResetToken> optional = tokenRepository.findByUsed(Boolean.FALSE);

            //PasswordResetToken passwordResetToken = tokenRepository.findById(presentUser.getId()).get();

            // generate the token
            final  int size = 7;
            String tok = "1234567";
            PasswordResetToken token = PasswordResetToken.builder().
                    token(tok).
                    dateCreated(LocalDateTime.now()).
                    expiryDate(getTokenExpiryTime()).
                    date_used(null).used(false).
                    user(presentUser).build();
            token.setToken(encoder.encode(token.getToken()));
            tokenRepository.save(token);


            // create a url link
            String appUrl = "http://localhost:4000/api/auth/reset?token=" + token.getToken();


            //Todo; create an email message
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(sender);
                message.setTo("kenechukwubanego@gmail.com");
                message.setText("To complete your password reset," +
                        "  please  click here\n: " + appUrl);

                mailSender.send(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("message sent......");
            return "A password reset link has been  sent to " + user.getEmail();


        }


    }



    @Override
    @Transactional
    public String resetPassword(ResetPassword resetPassword) {
           // check if the email the token was sent to match the email in the db
        User user = userRepository.
                findUserByEmail(resetPassword.getEmail()).
                orElseThrow(() -> new ResourceNotFoundException("email not found"));

        List<PasswordResetToken> list = tokenRepository.findByUserIdAndUsed(user, Boolean.FALSE);
        PasswordResetToken token = null;
        //todo; iterate through the list and match the token supplied to the token in the database;
        if (!ObjectUtils.isEmpty(list)) {// List is not empty, loop through the list using for each loop

            for (PasswordResetToken tok : list) {

                token = tok;
                if (!encoder.matches(resetPassword.getToken(), token.getToken())) {
                    throw new ResourceNotFoundException("Token supplied did not match!");
                }
                //CHECK IF TOKEN IS EXPIRED
                if (isTokenExpired(token)) {
                    System.out.println(token);
                    throw new AppException("Token is expired,");
                }

//

                boolean equals = resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword());
              //  System.out.println(equals);
                   // VERIFY NEW AND CONFIRM PASSWORD

                if (!verifyNewAndConfirmPassword(resetPassword.getNewPassword(), resetPassword.getConfirmPassword())){
                    throw new AppException("Password do not match");
                }
                //VALIDATE PASSWORD;

                if (!PasswordValidation.validatePassword(resetPassword.getNewPassword())){
                    throw  new AppException("Password is invalid! \n password should contain " +
                            "\n at least one uppercase character" +
                            " \n at least one lower case character " +
                            "\n at least one digit \n at least one special character" +
                            "\n minimum of 6  and maximum of 10 characters");
                }
            }
//            //todo; set the used status of the token to true and also set the token value to null;
            token.setUsed(true);
            token.setDate_used(Instant.now());
            token.setToken(null);
            token.setDateCreated(null);
            token.setExpiryDate(null);
            tokenRepository.deleteByUsed(Boolean.TRUE);

//            // todo; save the new password to the user table
            user.setPassword(encoder.encode(resetPassword.getNewPassword()));
            userRepository.save(user);
        }
        return "Your password has been successfully reset";
    }

   private static  boolean isTokenExpired(PasswordResetToken token){
        return LocalDateTime.now().isAfter(token.getExpiryDate());
    }
    private  static  boolean verifyNewAndConfirmPassword(String newPassword, String confirmPassword){
       return Objects.equals(newPassword, confirmPassword);

    }
}

