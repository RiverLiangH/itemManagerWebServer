package com.manager.utility;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    public static void sendVerificationEmail(String recipientEmail, String verificationLink) {
        final String username = "1833150937@qq.com";
        final String password = "svbxwkpihxcmciif";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("1833150937@qq.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Email verification");
            message.setText("Please click the following link to verify your email: \n" + verificationLink);

            Transport.send(message);
            System.out.println("Verification email has been sent.");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
