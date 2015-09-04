package com.gomentr.emailmanager.helpers;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Omar Addam on 2015-09-04.
 */
public class EmailSender {

    //region VARIABLES

    /**
     * The name that will be displayed next to the email address
     */
    protected  String emailPersonalName;

    /**
     * The email address used to send and receive emails
     */
    protected String emailAddress;
    protected String emailPassword;

    /**
     * The email address used to receive emails
     */
    protected String replyToAddress;

    /**
     * This plugin uses SMPT protocol for sending emails
     * We only require the host and the port from the user
     */
    protected String senderHost;
    protected String senderPort;

    //endregion

    //region EMAIL SENDING METHODS

    /**
     * Sends an email to someone
     * We require the email address of the receipt, the subject, and the body
     * The ID is used to identify an email when the user replies back
     */
    public void sendEmail(String id, String to, String subject, String body)
            throws Exception {

        String replyTo = createRecipientWithId(id);
        Properties props = prepareEmailSenderProperties();

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = prepareEmailSenderMessage(session, to, replyTo, subject, body);

        Transport transport = prepareEmailSenderTransport(session);
        transport.connect(senderHost, emailAddress, emailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }



    /**
     * Prepares the reply to for the email sender
     * It will include the email identifier in the reply to
     */
    protected String createRecipientWithId(String id) {
        String[] array = emailAddress.split("@");
        String replyTo = array[0] + "+" + id + "@" + array[1];
        return replyTo;
    }

    /**
     * Prepares the properties for the email sender
     */
    protected Properties prepareEmailSenderProperties() {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.setProperty("mail.smtp.ssl.trust", senderHost);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", senderHost);
        props.put("mail.smtp.user", emailAddress);
        props.put("mail.smtp.password", emailPassword);
        props.put("mail.smtp.port", senderPort);
        return props;
    }

    /**
     * Prepares the message to be sent by the email sender
     */
    protected MimeMessage prepareEmailSenderMessage(Session session, String to, String replyTo, String subject, String body)
            throws Exception {

        MimeMessage message = new MimeMessage(session);

        //From
        InternetAddress fromAddress = new InternetAddress(emailAddress);
        fromAddress.setPersonal(emailPersonalName);
        message.setFrom(fromAddress);

        //Reply to
        InternetAddress[] replyToAddress = new InternetAddress[1];
        replyToAddress[0] = new InternetAddress(replyTo);
        replyToAddress[0].setPersonal(emailPersonalName);
        message.setReplyTo(replyToAddress);

        //To
        InternetAddress toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);

        //Subject/Body
        message.setSubject(subject);
        message.setText(body);

        return message;
    }

    /**
     * Prepares the transport layer for the email sender
     */
    protected Transport prepareEmailSenderTransport(Session session)
            throws Exception {
        Transport transport = session.getTransport("smtp");
        return transport;
    }

    //endregion

    //region CONSTRUCTORS

    public EmailSender(String emailPersonalName, String emailAddress, String emailPassword, String replyToAddress,
                       String senderHost, String senderPort) {
        this.emailPersonalName = emailPersonalName;
        this.emailAddress = emailAddress;
        this.replyToAddress = replyToAddress;
        this.emailPassword = emailPassword;

        this.senderHost = senderHost;
        this.senderPort = senderPort;
    }

    //endregion

}
