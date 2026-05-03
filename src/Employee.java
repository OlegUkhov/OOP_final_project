// Абстрактный класс сотрудника университета.
// Расширяет User. Добавляет поля зарплаты, отдела и id сотрудника.
// Сотрудник может отправлять сообщения другим сотрудникам и просматривать запросы.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class Employee extends User {

    // Внутренний идентификатор сотрудника
    protected String employeeId;
    // Зарплата сотрудника
    protected double salary;
    // Название отдела
    protected String department;

    // Конструктор — инициализирует все поля сотрудника
    public Employee(String id, String firstName, String lastName, String email,
                    String password, Language language, String employeeId,
                    double salary, String department) {
        super(id, firstName, lastName, email, password, language);
        this.employeeId = employeeId;
        this.salary = salary;
        this.department = department;
    }

    // Отправить текстовое сообщение другому сотруднику
    public void sendMessage(Employee receiver, String text) {
        if (receiver != null && text != null && !text.isEmpty()) {
            // Создаём объект сообщения и сразу выводим (хранилище не нужно по диаграмме)
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

    // Просмотреть запросы (базовая реализация — возвращает пустой список)
    public List<Request> viewRequests() {
        return new ArrayList<>();
    }

    // Строковое представление сотрудника
    @Override
    public String toString() {
        return "Employee{id='" + id + "', name='" + firstName + " " + lastName
                + "', dept='" + department + "'}";
    }
}
