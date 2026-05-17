// A tech support request; status flows from VIEWED to ACCEPTED or REJECTED then to DONE
// Created with VIEWED status; TechSupportSpecialist drives all further transitions
import java.util.Date;
import java.util.Objects;

public class Request {

    private String requestId;
    private String description;
    // Starts as VIEWED; transitions enforced in TechSupportSpecialist methods
    private RequestStatus status;
    // Any User can submit a request (Student or Employee)
    private User createdBy;
    private Date date;

    public Request(String requestId, String description, User createdBy) {
        this.requestId = requestId;
        this.description = description;
        this.createdBy = createdBy;
        this.status = RequestStatus.VIEWED;
        this.date = new Date();
    }

    // Read by TechSupportSpecialist.viewNewRequests() acceptRequest() rejectRequest() markAsDone()
    public RequestStatus getStatus() {
        return status;
    }

    // Called by TechSupportSpecialist methods to advance the status
    public void setStatus(RequestStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    @Override
    public String toString() {
        return "Request{id='" + requestId + "', desc='" + description
                + "', status=" + status
                + ", by=" + (createdBy != null ? createdBy.getFirstName() + " " + createdBy.getLastName() : "null")
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request r = (Request) o;
        return Objects.equals(requestId, r.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}
