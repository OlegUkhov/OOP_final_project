import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class Employee extends User {

    private static final long serialVersionUID = 1L;

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

    public String getEmployeeId() { return employeeId; }
    public double getSalary() { return salary; }
    public String getDepartment() { return department; }

    public void sendMessage(Employee receiver, String text) {
        if (receiver != null && text != null && !text.isEmpty()) {
            Message msg = new Message(
                    UUID.randomUUID().toString(), this, receiver, text, new Date());
            DataStorage.getInstance().addMessage(msg);
            System.out.println("[MESSAGE] " + msg);
        }
    }

    public List<Request> viewRequests() {
        return new ArrayList<>();
    }

    public void viewAllNews() {
        DataStorage.getInstance().printNewsFeed(this);
    }

    public void submitRequest(String description) {
        if (description == null || description.isEmpty()) return;
        Request req = new Request(UUID.randomUUID().toString(), this, description);
        DataStorage.getInstance().addMessage(req);
        System.out.println(t("Request submitted.", "Өтініш жіберілді.", "Заявка отправлена."));
    }

    @Override
    public String toString() {
        return "Employee{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}
