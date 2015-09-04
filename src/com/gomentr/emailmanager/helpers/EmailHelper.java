package com.gomentr.emailmanager.helpers;

import com.gomentr.emailmanager.models.ReceivedMessageModel;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Created by Omar Addam on 2015-04-12.
 */
public class EmailHelper {

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

    /**
     * This plugin uses IMAP protocol for receiving emails
     * We only requrie the host and the port from the user
     */
    protected String receivingHost;
    protected String receivingPort;

    /**
     * The folder that will be accessed to retrieve the
     * emails to be processed
     */
    protected String inboxFolderName;
    /**
     * Successfully processed emails will be moved to this
     * folder and deleted from the inbox folder
     */
    protected String processedEmailsFolderName;
    /**
     * Emails that fail the process will be moved to this
     * folder and deleted from the inbox folder
     */
    protected String errorEmailsFolderName;

    //endregion

    //region EMAIL SENDING METHODS

    /**
     * Sends an email to someone
     * We require the email address of the receipt, the subject, and the body
     * The ID is used to identify an email when the user replies back
     */
    public void SendEmail(String ID, String To, String Subject, String Body)
            throws Exception {

        String ReplyTo = CreateRecipientWithId(ID);
        Properties props = PrepareEmailSenderProperties();

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = PrepareEmailSenderMessage(session, To, ReplyTo, Subject, Body);

        Transport transport = PrepareEmailSenderTransport(session);
        transport.connect(senderHost, emailAddress, emailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }



    /**
     * Prepares the reply to for the email sender
     * It will include the email identifier in the reply to
     */
    protected String CreateRecipientWithId(String ID) {
        String[] array = emailAddress.split("@");
        String replyTo = array[0] + "+" + ID + "@" + array[1];
        return replyTo;
    }

    /**
     * Prepares the properties for the email sender
     */
    protected Properties PrepareEmailSenderProperties() {
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
    protected MimeMessage PrepareEmailSenderMessage(Session Session, String To, String ReplyTo, String Subject, String Body)
            throws Exception {

        MimeMessage message = new MimeMessage(Session);

        //From
        InternetAddress fromAddress = new InternetAddress(emailAddress);
        fromAddress.setPersonal(emailPersonalName);
        message.setFrom(fromAddress);

        //Reply to
        InternetAddress[] replyTo = new InternetAddress[1];
        replyTo[0] = new InternetAddress(ReplyTo);
        replyTo[0].setPersonal(emailPersonalName);
        message.setReplyTo(replyTo);

        //To
        InternetAddress toAddress = new InternetAddress(To);
        message.addRecipient(Message.RecipientType.TO, toAddress);

        //Subject/Body
        message.setSubject(Subject);
        message.setText(Body);

        return message;
    }

    /**
     * Prepares the transport layer for the email sender
     */
    protected Transport PrepareEmailSenderTransport(Session Session)
            throws Exception {
        Transport transport = Session.getTransport("smtp");
        return transport;
    }

    //endregion

    //region EMAIL RECEIVING METHODS

    public List<ReceivedMessageModel> readEmails(boolean MoveEmailsAfterProcess)
            throws Exception {

        Properties props = PrepareEmailReaderProperties();
        Session session = Session.getDefaultInstance(props, null);

        Store store = PrepareEmailReaderStore(session);
        store.connect(receivingHost, emailAddress, emailPassword);

        Folder inboxFolder = PrepareEmailReaderFolder(store, inboxFolderName);
        Folder processedEmailsFolder = processedEmailsFolderName != null ? PrepareEmailReaderFolder(store, processedEmailsFolderName) : null;
        Folder errorEmailsFolder = errorEmailsFolderName != null ? PrepareEmailReaderFolder(store, errorEmailsFolderName) : null;

        List<ReceivedMessageModel> processedEmails = new ArrayList();
        Message[] messages = inboxFolder.getMessages();
        for(Message message : messages) {
            ReceivedMessageModel processedEmail = ProcessEmailReaderMessage(message);

            if (processedEmail != null)
                processedEmails.add(processedEmail);

            if (MoveEmailsAfterProcess && processedEmail == null && errorEmailsFolder != null)
                MoveMessageToAnotherFolder(message, inboxFolder, errorEmailsFolder);
            else if (MoveEmailsAfterProcess && processedEmail != null && processedEmailsFolder != null)
                MoveMessageToAnotherFolder(message, inboxFolder, processedEmailsFolder);
        }

        store.close();
        return processedEmails;
    }




    /**
     * Prepares the properties for the email reader
     */
    protected Properties PrepareEmailReaderProperties() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.ssl.enable", "true");
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.put("mail.imap.ssl.checkserveridentity", "false");
        props.put("mail.imap.ssl.trust", "*");
        props.put("mail.imaps.ssl.checkserveridentity", "false");
        props.put("mail.imaps.ssl.trust", "*");
        if (receivingPort != null)
            props.put("mail.imap.port", receivingPort);
        return props;
    }

    /**
     * Prepares the store for the email reader
     */
    protected Store PrepareEmailReaderStore(Session Session)
            throws Exception {
        Store store = Session.getStore("imaps");
        return store;
    }

    /**
     * Prepares a folder for the email reader
     */
    protected Folder PrepareEmailReaderFolder(Store Store, String FolderName)
            throws Exception {
        Folder folder = Store.getFolder(FolderName);
        folder.open(Folder.READ_WRITE);
        return folder;
    }

    /*
     * Moves a message to a folder
     */
    protected  void MoveMessageToAnotherFolder(Message Message, Folder SourceFolder, Folder DestinationFolder)
            throws Exception {
        Message[] msgs = new Message[1];
        msgs[0] = Message;
        SourceFolder.copyMessages(msgs, DestinationFolder);
        Message.setFlag(Flags.Flag.DELETED, true);
    }

    /**
     * Processes the messages that are fetched by the email reader
     */
    protected ReceivedMessageModel ProcessEmailReaderMessage(Message Message)
            throws Exception {

        String ID = ProcessEmailReaderMessageID(Message);
        if (ID == null)
            return null;

        String content = ProcessEmailReaderMessageContent(Message);
        if (content == null)
            return null;
        String parsedContent = ParseEmailReaderMessageContent(content);

        Address fromAddress = Message.getFrom()[0];
        Address toAddress = Message.getAllRecipients()[0];

        String subject = Message.getSubject();
        Date sentDate = Message.getSentDate();

        return new ReceivedMessageModel(ID, fromAddress, toAddress, subject, content, parsedContent, sentDate, Message);
    }

    /**
     * Processes the content of a message
     */
    protected String ProcessEmailReaderMessageContent(Message Message)
            throws Exception {
        String content = null;
        Object msgContent = Message.getContent();
        if (msgContent instanceof Multipart) {
            Multipart multipart = (Multipart) msgContent;
            for (int j = 0; j < multipart.getCount(); j++)
                content = getText(multipart.getBodyPart((j)));
        }
        else
            content = Message.getContent().toString();
        return content;
    }
    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            boolean textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    /**
     * Parse the conentent of a message
     * Clean the message and get the reply body only
     */
    protected String ParseEmailReaderMessageContent(String Content) {
        String parsedContent = Content;

        parsedContent = ParseEmailReaderMessageContent_Parser1(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser2(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser3(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser4(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser5(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser6(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser7(parsedContent);
        parsedContent = ParseEmailReaderMessageContent_Parser8(parsedContent);


        parsedContent = ParseEmailReaderMessageContent_ParserFinal(parsedContent);

        return parsedContent;
    }

    //Parse Text Reply
    private String ParseEmailReaderMessageContent_Parser1(String Content) {
        String parsedContent = "";

        String[] lines = Content.split("\n");
        for (String line : lines) {
            if (line == "")
                continue;
            if (!line.startsWith(">"))
                parsedContent += line + "\n";
        }

        if (parsedContent.length() > 0 && parsedContent.charAt(parsedContent.length()-1)=='\n')
            parsedContent = parsedContent.substring(0, parsedContent.length() - 1);

        return parsedContent;
    }
    //Remove <blockquote> and its content
    private String ParseEmailReaderMessageContent_Parser2(String Content) {
        String parsedContent = Content;

        String patternString = "(?s)(<|&lt;)blockquote.*(<|&lt;)/blockquote(>|&gt)";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove <div class="gmail_extra"> and its content
    private String ParseEmailReaderMessageContent_Parser3(String Content) {
        String parsedContent = Content;

        String patternString = "(?s)(<|&lt;)div class=\"gmail_extra\".*";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove <style"> and its content
    private String ParseEmailReaderMessageContent_Parser4(String Content) {
        String parsedContent = Content;

        String patternString = "(?s)(<|&lt;)style.*(<|&lt;)/style(>|&gt)";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove <script"> and its content
    private String ParseEmailReaderMessageContent_Parser5(String Content) {
        String parsedContent = Content;

        String patternString = "(?s)(<|&lt;)script.*(<|&lt;)/script(>|&gt)";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove <head"> and its content
    private String ParseEmailReaderMessageContent_Parser6(String Content) {
        String parsedContent = Content;

        String patternString = "(?s)(<|&lt;)head.*(<|&lt;)/head(>|&gt)";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove <html> and <body> and <span> tags
    private String ParseEmailReaderMessageContent_Parser7(String Content) {
        String parsedContent = Content;

        String patternString = "(<html[^>]*>)|(<body[^>]*>)|(<span[^>]*>)|(</html>)|(</body>)|(</span>)";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove style and class attributes
    private String ParseEmailReaderMessageContent_Parser8(String Content) {
        String parsedContent = Content;

        String patternString = "( class=\"[^\"]*\")|( style=\"[^\"]*\")";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }
    //Remove [On .... wrote:] | [From ...]
    private String ParseEmailReaderMessageContent_ParserFinal(String Content) {
        String parsedContent = Content;

        /** general spacers for time and date */
        String spacers = "[\\s,/\\.\\-]";

        /** matches times */
        String timePattern  = "(?:[0-2])?[0-9]:[0-5][0-9](?::[0-5][0-9])?(?:(?:\\s)?[AP]M)?";

        /** matches day of the week */
        String dayPattern   = "(?:(?:Mon(?:day)?)|(?:Tue(?:sday)?)|(?:Wed(?:nesday)?)|(?:Thu(?:rsday)?)|(?:Fri(?:day)?)|(?:Sat(?:urday)?)|(?:Sun(?:day)?))";

        /** matches day of the month (number and st, nd, rd, th) */
        String dayOfMonthPattern = "[0-3]?[0-9]" + spacers + "*(?:(?:th)|(?:st)|(?:nd)|(?:rd))?";

        /** matches months (numeric and text) */
        String monthPattern = "(?:(?:Jan(?:uary)?)|(?:Feb(?:uary)?)|(?:Mar(?:ch)?)|(?:Apr(?:il)?)|(?:May)|(?:Jun(?:e)?)|(?:Jul(?:y)?)" +
                "|(?:Aug(?:ust)?)|(?:Sep(?:tember)?)|(?:Oct(?:ober)?)|(?:Nov(?:ember)?)|(?:Dec(?:ember)?)|(?:[0-1]?[0-9]))";

        /** matches years (only 1000's and 2000's, because we are matching emails) */
        String yearPattern  = "(?:[1-2]?[0-9])[0-9][0-9]";

        /** matches a full date */
        String datePattern     = "(?:" + dayPattern + spacers + "+)?(?:(?:" + dayOfMonthPattern + spacers + "+" + monthPattern + ")|" +
                "(?:" + monthPattern + spacers + "+" + dayOfMonthPattern + "))" +
                spacers + "+" + yearPattern;

        /** matches a date and time combo (in either order) */
        String dateTimePattern = "((?:" + datePattern + "[\\s,]*((?:(?:at)|(?:@))?\\s*" + timePattern + "))?|" +
                "(?:" + timePattern + "[\\s,]*(?:on)?\\s*"+ datePattern + "))";

        /** matches a leading line such as
         * ----Original Message----
         * or simply
         * ------------------------
         */
        String leadInLine    = "-+\\s*(?:Original(?:\\sMessage)?)?\\s*-+";

        /** matches a header line indicating the date */
        String dateLine    = "(?:(?:date)|(?:sent)|(?:time)):\\s*"+ dateTimePattern + ".*";

        /** matches a subject or address line */
        String subjectOrAddressLine    = "((?:from)|(?:subject)|(?:b?cc)|(?:to)):.*((?:from)|(?:subject)|(?:b?cc)|(?:to)):.*";

        /** matches gmail style quoted text beginning, i.e.
         * On Mon Jun 7, 2010 at 8:50 PM, Simon wrote:
         */
        String gmailQuotedTextBeginning = "(On\\s+" + dateTimePattern + ".*wrote:)";


        /** matches the start of a quoted section of an email */
        Pattern QUOTED_TEXT_BEGINNING = Pattern.compile("(?i)(?:(?:" + leadInLine + ")?" +
                        "(?:(?:" +subjectOrAddressLine + ")|(?:" + dateLine + ")){2,6})|(?:" + gmailQuotedTextBeginning + ")"
        );

        String patternString = "(" + QUOTED_TEXT_BEGINNING.toString() + ").*";
        parsedContent = parsedContent.replaceAll(patternString, "");

        return parsedContent;
    }


    /**
     * Processes the ID passed by the email sender and collected by email reader
     * By default, it extracts it from the reply email
     * The default pattern is /\+[A-Za-z0-9-_]*\@/: GUID
     */
    protected String ProcessEmailReaderMessageID(Message Message)
    throws Exception {

        Address toAddress = Message.getAllRecipients()[0];

        String patternString = "\\+[A-Za-z0-9-_]*@";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(toAddress.toString());
        boolean matches = matcher.find(0);

        if (!matches)
            return null;

        String id = matcher.group(0).substring(1, matcher.group(0).length() - 1);
        return id;
    }

    //endregion

    //region CONSTRUCTORS

    /**
     * The full package
     */
    public EmailHelper(String EmailPersonalName, String EmailAddress, String EmailPassword, String ReplyToAddress,
                       String SenderHost, String SenderPort,
                       String ReceivingHost, String ReceivingPort,
                       String InboxFolderName, String ProcessedEmailsFolderName, String ErrorEmailsFolderName) {

        this.emailPersonalName = EmailPersonalName;
        this.emailAddress = EmailAddress;
        this.replyToAddress = ReplyToAddress;
        this.emailPassword = EmailPassword;

        this.senderHost = SenderHost;
        this.senderPort = SenderPort;

        this.receivingHost = ReceivingHost;
        this.receivingPort = ReceivingPort;

        this.inboxFolderName = InboxFolderName;
        this.processedEmailsFolderName = ProcessedEmailsFolderName;
        this.errorEmailsFolderName = ErrorEmailsFolderName;
    }

    /**
     * Sending and receiving emails without moving or deleting them
     */
    public EmailHelper(String EmailPersonalName, String EmailAddress, String EmailPassword, String ReplyToAddress,
                       String SenderHost, String SenderPort,
                       String ReceivingHost, String ReceivingPort,
                       String InboxFolderName) {
        this(EmailPersonalName, EmailAddress, EmailPassword, ReplyToAddress,
                SenderHost, SenderPort,
                ReceivingHost, ReceivingPort,
                InboxFolderName, null, null);
    }

    /**
     * Sending emails only
     */
    public EmailHelper(String EmailPersonalName, String EmailAddress, String EmailPassword, String ReplyToAddress,
                       String SenderHost, String SenderPort) {

        this(EmailPersonalName, EmailAddress, EmailPassword, ReplyToAddress,
                SenderHost, SenderPort,
                null, null,
                null, null, null);
    }

    /**
     * Receiving emails only
     */
    public EmailHelper(String EmailPersonalName, String EmailAddress, String EmailPassword, String ReplyToAddress,
                       String ReceivingHost, String ReceivingPort,
                       String InboxFolderName) {
        this(EmailPersonalName, EmailAddress, EmailPassword, ReplyToAddress,
                null, null,
                ReceivingHost, ReceivingPort,
                InboxFolderName, null, null);
    }

    //endregion

}
