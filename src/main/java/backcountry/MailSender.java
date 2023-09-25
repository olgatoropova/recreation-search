package backcountry;

import java.io.IOException;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSender {
    private static MailSender instance = null;
    private final Properties messageProperties;
    private final Properties appProperties;

    private MailSender() {
        appProperties = new Properties();
        try {
            appProperties.load(MailSender.class.getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        messageProperties = new Properties();
        messageProperties.put("mail.smtp.auth", "true");
        messageProperties.put("mail.smtp.starttls.enable", "true");
        messageProperties.put("mail.smtp.host", appProperties.getProperty("mail.host"));
        messageProperties.put("mail.smtp.port", appProperties.getProperty("mail.port"));
    }

    public static MailSender getInstance() {
        if (instance == null) {
            instance = new MailSender();
        }
        return instance;
    }

    public void send(String to, String subject, String text) {

        Session session = Session.getDefaultInstance(messageProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(appProperties.getProperty("mail.sender"),
                                appProperties.getProperty("mail.password"));
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(appProperties.getProperty("mail.sender")));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            System.out.println("Sent message successfully...."); // TODO: use log4j
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
