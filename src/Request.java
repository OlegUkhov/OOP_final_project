import java.util.Date;
import java.util.Objects;

public class Request {
    private String requestId;
    private String description;
    private RequestStatus status;
    private User createdBy;
    private Date date;

    public Request(String requestId, String description, User createdBy) {
        this.requestId = requestId;
        this.description = description;
        this.createdBy = createdBy;
        this.status = RequestStatus.VIEWED;
        this.date = new Date();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getDescription() {
        return description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdBy=" + (createdBy != null ? createdBy.getFirstName() + " " + createdBy.getLastName() : "null") +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return Objects.equals(requestId, request.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}
