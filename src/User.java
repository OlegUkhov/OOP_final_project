// Абстрактный базовый класс для всех пользователей системы.
// Реализует Observer — каждый пользователь может получать уведомления о новых статьях в журналах.
import java.util.Objects;

public abstract class User implements Observer {

    // Уникальный идентификатор пользователя
    protected String id;
    // Имя пользователя
    protected String firstName;
    // Фамилия пользователя
    protected String lastName;
    // Электронная почта
    protected String email;
    // Пароль для входа
    protected String password;
    // Язык интерфейса пользователя
    protected Language language;

    // Конструктор — инициализирует все поля пользователя
    public User(String id, String firstName, String lastName,
                String email, String password, Language language) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.language = language;
    }

    // Вход в систему — возвращает true при успехе
    public boolean login() {
        return true;
    }

    // Выход из системы
    public void logout() {
    }

    // Уведомление подписчика о новой статье (реализация Observer)
    @Override
    public void update(ResearchPaper paper) {
        if (paper != null) {
            System.out.println("[NOTIFICATION] " + firstName + " " + lastName
                    + " notified: new paper — " + paper.getTitle());
        }
    }

    // Геттер id — нужен для поиска и удаления пользователя в DataStorage
    public String getId() {
        return id;
    }

    // Геттер firstName — нужен для отображения в toString() других классов
    public String getFirstName() {
        return firstName;
    }

    // Геттер lastName — нужен для отображения в toString() других классов
    public String getLastName() {
        return lastName;
    }

    // Строковое представление пользователя
    @Override
    public String toString() {
        return firstName + " " + lastName + " [id=" + id + ", email=" + email + "]";
    }

    // Два пользователя равны, если совпадают их id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    // Хэш-код по id
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
