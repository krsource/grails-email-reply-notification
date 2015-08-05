package Models;

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

    public String getID() {
        return this.id;
    }
    public void setID(String ID) {
        this.id = ID;
    }

    public Address getFromAddress() {
        return this.fromAddress;
    }
    public void setFromAddress(Address FromAddress) {
        this.fromAddress = FromAddress;
    }
    public Address getToAddress() {
        return this.toAddress;
    }
    public void setToAddress(Address ToAddress) {
        this.toAddress = ToAddress;
    }

    public String getSubject() {
        return this.subject;
    }
    public void setSubject(String Subject) {
        this.subject = Subject;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String Content) {
        this.content = Content;
    }
    public String getParsedContent() {
        return this.parsedContent;
    }
    public void setParsedContent(String ParsedContent) {
        this.parsedContent = ParsedContent;
    }


    public Date getSentDate() {
        return this.sentDate;
    }
    public void setSentDate(Date SentDate) {
        this.sentDate = SentDate;
    }

    public Message getMessage() {
        return this.message;
    }
    public void setMessage(Message Message) {
        this.message = Message;
    }

    //endregion

    //region Constructors

    public ReceivedMessageModel(String ID,
                                Address FromAddress, Address ToAddress,
                                String Subject, String Content, String ParsedContent,
                                Date SentDate, Message Message) {
        setID(ID);

        setFromAddress(FromAddress);
        setToAddress(ToAddress);

        setSubject(Subject);
        setContent(Content);
        setParsedContent(ParsedContent);

        setMessage(Message);
    }

    //endregion

}
