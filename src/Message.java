// An internal message between two Employee objects
// Created and printed by Employee.sendMessage(); not stored in DataStorage in this version
import java.util.Date;
import java.util.Objects;

public class Message {

    private String messageId;
    // Set to the calling Employee in Employee.sendMessage()
    private Employee sender;
    private Employee receiver;
    private String content;
    private Date date;

    public Message(String messageId, Employee sender, Employee receiver,
                   String content, Date date) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public Employee getReceiver() {
        return receiver;
    }

    public Employee getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Message{from=" + (sender != null ? sender.getFirstName() + " " + sender.getLastName() : "null")
                + ", to=" + (receiver != null ? receiver.getFirstName() + " " + receiver.getLastName() : "null")
                + ", content='" + content + "', date=" + date + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message m = (Message) o;
        return Objects.equals(messageId, m.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}