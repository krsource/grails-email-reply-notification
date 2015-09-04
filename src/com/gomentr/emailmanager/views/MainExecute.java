package com.gomentr.emailmanager.views;

import com.gomentr.emailmanager.helpers.EmailHelper;

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
        EmailHelper emailSenderOnly = new EmailHelper(emailPersonalName, emailAddress, emailPassword, emailReplyTo, senderHost, senderPort);
        //emailSenderOnly.SendEmail("11-12", "omaddam@gmail.com", "Testing my sender plugin", "I told you that i am just testing it!!!!");

        //Email Receiver Only
        EmailHelper emailReceiverOnly = new EmailHelper(emailPersonalName, emailAddress, emailPassword, emailReplyTo, receivingHost, receivingPort, inboxFolderName);
        /*List<ReceivedMessageModel> emails = emailReceiverOnly.readEmails(false);
        System.out.println("Total: " + emails.size());
        for(ReceivedMessageModel message : emails) {
            System.out.println("****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************");
            System.out.println("ID: " + message.getID().toString());
            System.out.println("From: " + message.getFromAddress().toString());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Parsed Content:" + message.getParsedContent());// + "\n** Unparsed:" + message.getContent());
        }*/

        //Email Full Package
        EmailHelper emailFullPackage = new EmailHelper(emailPersonalName, emailAddress, emailPassword, emailReplyTo, senderHost, senderPort, receivingHost, receivingPort, inboxFolderName, processedEmailsFolderName, errorEmailsFolderName);
        //emailFullPackage.SendEmail("11-12", "omaddam@gmail.com", "Testing my sender plugin", "I told you that i am just testing it!!!!");
        /*List<ReceivedMessageModel> emails = emailFullPackage.readEmails(true);
        System.out.println("Total: " + emails.size());
        for(ReceivedMessageModel message : emails) {
            System.out.println("****************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************");
            System.out.println("ID: " + message.getID().toString());
            System.out.println("From: " + message.getFromAddress());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Parsed Content:" + message.getParsedContent());// + "\n** Unparsed:" + message.getContent());
        }*/
    }

}
