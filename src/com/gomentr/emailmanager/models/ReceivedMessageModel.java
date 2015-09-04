package com.gomentr.emailmanager.models;

import javax.mail.Address;
import javax.mail.Message;
import java.util.Date;

/**
 * Created by Omar Addam on 2015-04-12.
 */
public class ReceivedMessageModel {

    //region Variables

    private String id;

    private Address fromAddress;
    private Address toAddress;

    private String subject;
    private String content;
    private String parsedContent;

    private Date sentDate;

    private Message message;

    //endregion

    //region Setters and getters

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Address getFromAddress() {
        return this.fromAddress;
    }
    public void setFromAddress(Address fromAddress) {
        this.fromAddress = fromAddress;
    }
    public Address getToAddress() {
        return this.toAddress;
    }
    public void setToAddress(Address toAddress) {
        this.toAddress = toAddress;
    }

    public String getSubject() {
        return this.subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getParsedContent() {
        return this.parsedContent;
    }
    public void setParsedContent(String parsedContent) {
        this.parsedContent = parsedContent;
    }


    public Date getSentDate() {
        return this.sentDate;
    }
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Message getMessage() {
        return this.message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }

    //endregion

    //region Constructors

    public ReceivedMessageModel(String id,
                                Address fromAddress, Address toAddress,
                                String subject, String content, String parsedContent,
                                Date sentDate, Message message) {
        setId(id);

        setFromAddress(fromAddress);
        setToAddress(toAddress);

        setSubject(subject);
        setContent(content);
        setParsedContent(parsedContent);

        setMessage(message);
    }

    //endregion

}
