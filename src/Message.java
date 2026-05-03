import java.util.Date;
import java.util.Objects;

public class Message {
    private String messageId;
    private Employee sender;
    private Employee receiver;
    private String content;
    private Date date;

    public Message(String messageId, Employee sender, Employee receiver, String content, Date date) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public Employee getSender() {
        return sender;
    }

    public Employee getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", sender=" + sender.getFirstName() + " " + sender.getLastName() +
                ", receiver=" + receiver.getFirstName() + " " + receiver.getLastName() +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(messageId, message.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}
