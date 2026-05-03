import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

public abstract class Employee extends User {
    protected String employeeId;
    protected double salary;
    protected String department;
    protected List<Message> sentMessages;

    public Employee(String id, String firstName, String lastName, String email,
                    String password, Language language, String employeeId,
                    double salary, String department) {
        super(id, firstName, lastName, email, password, language);
        this.employeeId = employeeId;
        this.salary = salary;
        this.department = department;
        this.sentMessages = new ArrayList<>();
    }

    // Отправить сообщение другому сотруднику
    public void sendMessage(Employee receiver, String text) {
        if (receiver != null && !text.isEmpty()) {
            Message msg = new Message(
                UUID.randomUUID().toString(),
                this,
                receiver,
                text,
                new Date()
            );
            this.sentMessages.add(msg);
        }
    }

    public List<Request> viewRequests() {
        return new ArrayList<>();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
