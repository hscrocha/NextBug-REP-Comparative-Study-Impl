/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bugapp.connection;

import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Henrique
 */
public class GmailConnection {
    
    private String UserName;
    private String Password;
    private Properties MailProperties;
    
    public GmailConnection(String Username, String Password){
        this.UserName = Username;
        this.Password = Password;
    }
    
    private void setTSLProperties(){
        MailProperties = new Properties();
        MailProperties.put("mail.smtp.auth", "true");
        MailProperties.put("mail.smtp.starttls.enable", "true");
        MailProperties.put("mail.smtp.host", "smtp.gmail.com");
        MailProperties.put("mail.smtp.port", "587");
    }
    
    private void setSSLProperties(){
        MailProperties = new Properties();
        MailProperties.put("mail.smtp.host", "smtp.gmail.com");
        MailProperties.put("mail.smtp.socketFactory.port", "465");
        MailProperties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        MailProperties.put("mail.smtp.auth", "true");
        MailProperties.put("mail.smtp.port", "465");
    }
    
    public void sendTSL(String FromAddress, String ToAddress, String Subject, String MailContent) throws MessagingException{
        setTSLProperties();
        send(FromAddress, ToAddress, Subject, MailContent);
    }
    
    public void sendSSL(String FromAddress, String ToAddress, String Subject, String MailContent) throws MessagingException{
        setSSLProperties();
        send(FromAddress, ToAddress, Subject, MailContent);
    }    
 
    private void send(String FromAddress, String ToAddress, String Subject, String MailContent) throws MessagingException{
        
        Session session = Session.getInstance(MailProperties,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(UserName, Password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FromAddress));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(ToAddress));
        message.setSubject(Subject);
        message.setText(MailContent);

        Transport.send(message);
    }
    
    
}
