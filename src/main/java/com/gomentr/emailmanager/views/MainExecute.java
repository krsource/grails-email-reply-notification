package com.gomentr.emailmanager.views;

import com.gomentr.emailmanager.helpers.EmailReceiver;
import com.gomentr.emailmanager.helpers.EmailSender;
import com.gomentr.emailmanager.models.ReceivedMessageModel;

import java.util.List;

public class MainExecute {


    public static void main(String[] args)
            throws Exception {
        CodeExample();
    }

    private static void CodeExample()
            throws Exception {

        String emailPersonalName = "GoMentr Omar";
        String emailAddress = "oaddam@gomentr.com";
        String emailPassword = "";
        String emailReplyTo = "oaddam@gomentr.com";

        String senderHost = "smtp.gmail.com";
        String senderPort = "587";

        String receivingHost = "imap.gmail.com";
        String receivingPort = null;

        String inboxFolderName = "inbox";
        String processedEmailsFolderName =  "Processed";
        String errorEmailsFolderName = "UnProcessed";

        //Email Sender Only
        EmailSender emailSenderOnly = new EmailSender(emailPersonalName, emailAddress, emailPassword, emailReplyTo, senderHost, senderPort);
        //emailSenderOnly.sendEmail("11-12", "omaddam@gmail.com", "Testing my sender plugin", "I told you that i am just testing it!!!!");

        //Email Receiver Only
        EmailReceiver emailReceiverOnly = new EmailReceiver(emailPersonalName, emailAddress, emailPassword, emailReplyTo, receivingHost, receivingPort, inboxFolderName, processedEmailsFolderName, errorEmailsFolderName);
        List<ReceivedMessageModel> emails = emailReceiverOnly.readEmails(false);
        System.out.println("Total: " + emails.size());
        for(ReceivedMessageModel message : emails) {
            System.out.println("****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************");
            System.out.println("ID: " + message.getId().toString());
            System.out.println("From: " + message.getFromAddress().toString());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Parsed Content:" + message.getParsedContent());// + "\n** Unparsed:" + message.getContent());
        }
    }

}
