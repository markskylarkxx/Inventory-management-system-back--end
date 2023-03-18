package com.inventory.mgt.sytem.inventory.mgt.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/***
 * This is a service that provides mail functionality
 * ****/
public interface EmailService {

    public  void sendMail(String toEmail, String subject, String body);

}
