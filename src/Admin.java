// System administrator; manages all users and reads log files
// Keeps its own managedUsers list and creates Log entries via logAction()
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee {

    private List<User> managedUsers;
    private List<Log> logs;

    public Admin(String id, String firstName, String lastName, String email,
                 String password, Language language, String employeeId,
                 double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managedUsers = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    // Also calls logAction() to record the add event; Log object is created with current timestamp
    public void addUser(User user) {
        if (user != null && !managedUsers.contains(user)) {
            managedUsers.add(user);
            logAction("Added user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    // Removes by id using removeIf; the id comparison mirrors User.equals() logic
    public void removeUser(String userId) {
        if (userId == null || userId.isEmpty()) return;
        managedUsers.removeIf(u -> u.getId().equals(userId));
        logAction("Removed user with id: " + userId);
    }

    public void updateUser(User user) {
        if (user != null && managedUsers.contains(user)) {
            logAction("Updated user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    public List<Log> viewLogFiles() {
        return new ArrayList<>(logs);
    }

    // Creates a Log entry with UUID timestamp and this admin id
    // Log.toString() is what viewLogFiles() output will show
    private void logAction(String action) {
        logs.add(new Log(UUID.randomUUID().toString(), this.id, action, new java.util.Date()));
    }

    @Override
    public String toString() {
        return "Admin{id='" + id + "', name='" + firstName + " " + lastName
                + "', users=" + managedUsers.size() + ", logs=" + logs.size() + "}";
    }
}
