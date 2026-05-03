// Класс администратора системы.
// Управляет пользователями (добавление, удаление, обновление)
// и просматривает системные логи действий.
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Admin extends Employee {

    // Список пользователей под управлением администратора
    private List<User> managedUsers;
    // Список логов действий
    private List<Log> logs;

    // Конструктор — инициализирует пустые списки
    public Admin(String id, String firstName, String lastName, String email,
                 String password, Language language, String employeeId,
                 double salary, String department) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managedUsers = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    // Добавить нового пользователя в систему
    public void addUser(User user) {
        if (user != null && !managedUsers.contains(user)) {
            managedUsers.add(user);
            // Записываем действие в лог
            logAction("Added user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    // Удалить пользователя из системы по его id
    public void removeUser(String userId) {
        if (userId == null || userId.isEmpty()) return;
        // Удаляем пользователя с совпадающим id
        managedUsers.removeIf(u -> u.getId().equals(userId));
        logAction("Removed user with id: " + userId);
    }

    // Обновить данные существующего пользователя
    public void updateUser(User user) {
        if (user != null && managedUsers.contains(user)) {
            logAction("Updated user: " + user.getFirstName() + " " + user.getLastName());
        }
    }

    // Получить список всех системных логов
    public List<Log> viewLogFiles() {
        return new ArrayList<>(logs);
    }

    // Вспомогательный метод: создать и добавить запись лога
    private void logAction(String action) {
        logs.add(new Log(UUID.randomUUID().toString(), this.id, action, new java.util.Date()));
    }

    // Строковое представление администратора
    @Override
    public String toString() {
        return "Admin{id='" + id + "', name='" + firstName + " " + lastName
                + "', users=" + managedUsers.size() + ", logs=" + logs.size() + "}";
    }
}
