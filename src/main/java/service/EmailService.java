package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService implements IEmailService {
    @Override
    public void sendEmail(String userSMTP, String passSMTP, String from, String to, String subject, String msg) {
        //Get properties object
        Properties props = new Properties();
        /*
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        */
        /*
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        */
        // TODO: use a config file for SMTP settings
        props.put("mail.smtp.host", "localhost");
        //props.put("mail.smtp.port", "25");
        //props.put("mail.smtp.auth", "true");

        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userSMTP,passSMTP);
                    }
                });

        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addFrom(new InternetAddress[]{ new InternetAddress(from)});
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(subject);
            message.setText(msg);
            //send message
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
