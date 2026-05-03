import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist extends Employee {
    private List<Request> handledRequests;

    public TechSupportSpecialist(String id, String firstName, String lastName, String email,
                                  String password, Language language, String employeeId,
                                  double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.handledRequests = new ArrayList<>();
    }

    // Посмотреть новые запросы (со статусом VIEWED)
    public List<Request> viewNewRequests(List<Request> allRequests) {
        List<Request> newRequests = new ArrayList<>();
        if (allRequests != null) {
            for (Request req : allRequests) {
                if (req.getStatus() == RequestStatus.VIEWED) {
                    newRequests.add(req);
                }
            }
        }
        return newRequests;
    }

    // Принять запрос (изменить статус на ACCEPTED)
    public void acceptRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.ACCEPTED);
            handledRequests.add(request);
        }
    }

    // Отклонить запрос (изменить статус на REJECTED)
    public void rejectRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.REJECTED);
            handledRequests.add(request);
        }
    }

    // Отметить как выполненный (изменить статус на DONE)
    public void markAsDone(Request request) {
        if (request != null && request.getStatus() == RequestStatus.ACCEPTED) {
            request.setStatus(RequestStatus.DONE);
        }
    }

    public List<Request> getHandledRequests() {
        return new ArrayList<>(handledRequests);
    }

    @Override
    public String toString() {
        return "TechSupportSpecialist{" +
                "id='" + id + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", department='" + department + '\'' +
                ", handledRequests=" + handledRequests.size() +
                '}';
    }
}
