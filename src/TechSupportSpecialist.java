import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist extends Employee {

    private static final long serialVersionUID = 1L;

    public TechSupportSpecialist(String id, String firstName, String lastName, String email,
                                 String password, Language language, String employeeId,
                                 double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
    }

    public List<Request> viewNewRequests() {
        List<Request> result = new ArrayList<>();
        for (Request req : DataStorage.getInstance().getRequests()) {
            if (req.getStatus() == RequestStatus.VIEWED) {
                result.add(req);
            }
        }
        return result;
    }

    public void acceptRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.ACCEPTED);
        }
    }

    public void rejectRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    public void markAsDone(Request request) {
        if (request != null && request.getStatus() == RequestStatus.ACCEPTED) {
            request.setStatus(RequestStatus.DONE);
        }
    }

    @Override
    public String toString() {
        return "TechSupportSpecialist{id='" + id + "', name='" + firstName + " " + lastName + "'}";
    }
}
