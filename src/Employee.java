import java.util.List;

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
    public void sendMessage(){}
    public List<Request> viewRequests(){}
    public String getEmployeeId(){
        return employeeId;
    }
    public double getSalary(){
        return salary;
    }
}
