// Tech support specialist; processes Request objects through a strict status flow
// VIEWED -> ACCEPTED or REJECTED -> DONE
import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist extends Employee {

    public TechSupportSpecialist(String id, String firstName, String lastName, String email,
                                  String password, Language language, String employeeId,
                                  double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
    }

    // Filters the full request list passed in; only returns requests in VIEWED status
    // In a full system the list would come from DataStorage.getRequests()
    public List<Request> viewNewRequests(List<Request> allRequests) {
        List<Request> result = new ArrayList<>();
        if (allRequests == null) return result;
        for (Request req : allRequests) {
            if (req.getStatus() == RequestStatus.VIEWED) {
                result.add(req);
            }
        }
        return result;
    }

    // Guard ensures only VIEWED requests can be accepted
    public void acceptRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.ACCEPTED);
        }
    }

    // Guard ensures only VIEWED requests can be rejected
    public void rejectRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    // Can only mark as DONE after the request has been ACCEPTED
    public void markAsDone(Request request) {
        if (request != null && request.getStatus() == RequestStatus.ACCEPTED) {
            request.setStatus(RequestStatus.DONE);
        }
    }

    @Override
    public String toString() {
        return "TechSupportSpecialist{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}
