// Abstract base for all university staff
// Extends User with salary department and employee id
// Teacher Manager Admin and TechSupportSpecialist all extend this class
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class Employee extends User {

    protected String employeeId;
    protected double salary;
    protected String department;

    public Employee(String id, String firstName, String lastName, String email,
                    String password, Language language, String employeeId,
                    double salary, String department) {
        super(id, firstName, lastName, email, password, language);
        this.employeeId = employeeId;
        this.salary = salary;
        this.department = department;
    }

    // Creates a Message object and prints it; any Employee can message any other Employee
    // Message constructor expects two Employee references as sender and receiver
    public void sendMessage(Employee receiver, String text) {
        if (receiver != null && text != null && !text.isEmpty()) {
            Message msg = new Message(
                UUID.randomUUID().toString(),
                this,
                receiver,
                text,
                new Date()
            );
            System.out.println("[MESSAGE] " + msg);
        }
    }

    // Base implementation returns empty list; TechSupportSpecialist overrides the full version
    public List<Request> viewRequests() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Employee{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}
