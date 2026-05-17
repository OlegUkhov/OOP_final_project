import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee {

    private static final long serialVersionUID = 1L;

    public Admin(String id, String firstName, String lastName, String email,
                 String password, Language language, String employeeId,
                 double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
    }

    public void addUser(User user) {
        if (user != null) {
            DataStorage.getInstance().addUser(user);
            logAction("Added user " + user.getFirstName() + " " + user.getLastName());
        }
    }

    public void removeUser(String userId) {
        if (userId != null && !userId.isEmpty()) {
            DataStorage.getInstance().removeUser(userId);
            logAction("Removed user id=" + userId);
        }
    }

    public void updateUser(User user) {
        if (user != null) {
            DataStorage.getInstance().updateUser(user);
            logAction("Updated user " + user.getFirstName());
        }
    }

    public List<Log> viewLogFiles() {
        return DataStorage.getInstance().getLogs();
    }

    private void logAction(String action) {
        Log log = new Log(UUID.randomUUID().toString(), id, action, new Date());
        DataStorage.getInstance().addLog(log);
    }

    @Override
    public String toString() {
        return "Admin{id='" + id + "', name='" + firstName + " " + lastName + "'}";
    }
}
