import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String messageId;
    protected User sender;
    protected User receiver;
    protected String content;
    protected Date date;
    protected MessageType type;

    public Message(String messageId, User sender, User receiver, String content, Date date) {
        this(messageId, sender, receiver, content, date, MessageType.REGULAR);
    }

    protected Message(String messageId, User sender, User receiver, String content,
                      Date date, MessageType type) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date != null ? date : new Date();
        this.type = type;
    }

    public String getMessageId() { return messageId; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }

    @Override
    public String toString() {
        return "Message{from=" + formatUser(sender) + ", to=" + formatUser(receiver)
                + ", content='" + content + "'}";
    }

    protected String formatUser(User user) {
        if (user == null) return "-";
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        return Objects.equals(messageId, ((Message) o).messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}
