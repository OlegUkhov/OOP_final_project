import java.util.Date;

public class Request extends Message {

    private RequestStatus status;

    public Request(String messageId, User sender, String description) {
        super(messageId, sender, null, description, new Date(), MessageType.REQUEST);
        this.status = RequestStatus.VIEWED;
    }

    public RequestStatus getStatus() { return status; }

    public void setStatus(RequestStatus status) {
        if (status != null) this.status = status;
    }

    @Override
    public String toString() {
        return "Request{desc='" + content + "', status=" + status
                + ", by=" + formatUser(sender) + "}";
    }
}
