package com.inventory.mgt.sytem.inventory.mgt.system.service.impl;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import com.inventory.mgt.sytem.inventory.mgt.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private  static JavaMailSender mailSender;


    @Override
    public void sendMail(String toEmail, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("obiora.okwubanego@unionsystems.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            mailSender.send(message);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("Mail is sent.....");

    }

    public static void sendMessage(){
        User  user = new User();
        String plainPassword = "1234567";

        String appUrl = "http://localhost:4000/api/auth/reset?password= " + plainPassword;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("obiora.okwubanego@unionsystems.com");

            message.setTo(user.getEmail());
            message.setText("To reset your password," +
                    "  please  click here\n: " + appUrl);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("message sent......");

    }
}
