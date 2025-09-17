package com.example.web_ban_banh.Config.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender=new JavaMailSenderImpl();
        mailSender.setHost("${spring.mail.host}");
        mailSender.setPort(587);

        mailSender.setUsername("${spring.mail.username}");
        mailSender.setPassword("${spring.mail.password}");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "${spring.mail.properties.mail.smtp.auth}");
        props.put("mail.smtp.starttls.enable", "${spring.mail.properties.mail.smtp.starttls.enable}");

        return mailSender;
    }
    //Mấy cái trong ${....} là lấy ở application.properties
}
