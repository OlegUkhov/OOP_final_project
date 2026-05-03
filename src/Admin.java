import java.util.ArrayList;
import java.util.List;

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

    // Добавить пользователя в систему
    public void addUser(User user) {
        if (user != null && !managedUsers.contains(user)) {
            managedUsers.add(user);
            logAction("Added user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    // Удалить пользователя из системы
    public void removeUser(String userId) {
        if (userId != null && !userId.isEmpty()) {
            managedUsers.removeIf(u -> u.getId().equals(userId));
            logAction("Removed user with ID: " + userId);
        }
    }

    // Обновить данные пользователя
    public void updateUser(User user) {
        if (user != null && managedUsers.contains(user)) {
            logAction("Updated user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    // Просмотреть логи
    public List<Log> viewLogFiles() {
        return new ArrayList<>(logs);
    }

    // Вспомогательный метод для логирования действий
    private void logAction(String action) {
        Log log = new Log(
            java.util.UUID.randomUUID().toString(),
            this.id,
            action,
            new java.util.Date()
        );
        logs.add(log);
    }

    public List<User> getManagedUsers() {
        return new ArrayList<>(managedUsers);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", managedUsers=" + managedUsers.size() +
                ", logs=" + logs.size() +
                '}';
    }
}
