import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee {

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
            logAction("Removed user with id " + userId);
        }
    }

    public void updateUser(User user) {
        if (user != null) {
            DataStorage.getInstance().updateUser(user);
            logAction("Updated user " + user.getFirstName() + " " + user.getLastName());
        }
    }

    public List<Log> viewLogFiles() {
        return DataStorage.getInstance().getLogs();
    }

    // Creates Log entry with UUID timestamp and admin id then stores in DataStorage
    private void logAction(String action) {
        Log log = new Log(UUID.randomUUID().toString(), this.id, action, new Date());
        DataStorage.getInstance().addLog(log);
    }

    @Override
    public String toString() {
        return "Admin{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}