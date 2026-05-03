// Специалист технической поддержки.
// Просматривает входящие запросы и меняет их статус:
// VIEWED → ACCEPTED/REJECTED → DONE.
import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist extends Employee {

    // Конструктор — все поля приходят из Employee
    public TechSupportSpecialist(String id, String firstName, String lastName, String email,
                                  String password, Language language, String employeeId,
                                  double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
    }

    // Получить список новых запросов со статусом VIEWED
    public List<Request> viewNewRequests(List<Request> allRequests) {
        List<Request> result = new ArrayList<>();
        if (allRequests == null) return result;
        for (Request req : allRequests) {
            // Отбираем только запросы в статусе VIEWED
            if (req.getStatus() == RequestStatus.VIEWED) {
                result.add(req);
            }
        }
        return result;
    }

    // Принять запрос: статус VIEWED → ACCEPTED
    public void acceptRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.ACCEPTED);
        }
    }

    // Отклонить запрос: статус VIEWED → REJECTED
    public void rejectRequest(Request request) {
        if (request != null && request.getStatus() == RequestStatus.VIEWED) {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    // Пометить запрос как выполненный: статус ACCEPTED → DONE
    public void markAsDone(Request request) {
        if (request != null && request.getStatus() == RequestStatus.ACCEPTED) {
            request.setStatus(RequestStatus.DONE);
        }
    }

    // Строковое представление специалиста
    @Override
    public String toString() {
        return "TechSupportSpecialist{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}
