// Класс преподавателя университета.
// Управляет курсами, выставляет оценки, отправляет жалобы на студентов.
// Имеет должность (TeacherPosition) и рейтинг, выставляемый студентами.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Teacher extends Employee {

    // Должность преподавателя (TUTOR, LECTOR, SENIOR_LECTOR, PROFESSOR)
    private TeacherPosition position;
    // Список курсов, которые ведёт преподаватель
    private List<Course> courses;
    // Текущий средний рейтинг преподавателя
    private double rating;
    // Количество оценок — нужно для корректного расчёта среднего
    private int ratingCount;

    // Конструктор — инициализирует преподавателя с нулевым рейтингом
    public Teacher(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    // Получить список курсов преподавателя
    public List<Course> viewCourses() {
        return new ArrayList<>(courses);
    }

    // Добавить курс в список преподавателя и зарегистрировать себя в курсе
    public void manageCourse(Course c) {
        if (c != null && !courses.contains(c)) {
            courses.add(c);
            // Регистрируем преподавателя в курсе
            c.addTeacher(this);
        }
    }

    // Выставить оценку студенту по курсу (только если курс ведёт этот преподаватель)
    public void putMark(Student s, Course c, Mark mark) {
        if (s != null && mark != null && courses.contains(c)) {
            // Добавляем оценку в список оценок студента
            s.addMark(mark);
        }
    }

    // Отправить жалобу на студента декану с указанием срочности
    public Complaint sendComplaint(Student s, String text, ComplaintUrgency urgency) {
        if (s == null || text == null || text.isEmpty()) return null;
        // Создаём объект жалобы и возвращаем его (хранение — на стороне получателя)
        return new Complaint(UUID.randomUUID().toString(), s, text, urgency, new Date());
    }

    // Просмотреть информацию о студентах своих курсов
    public void viewStudentsInfo() {
        for (Course c : courses) {
            System.out.println("Course: " + c);
        }
    }

    // Обновить рейтинг преподавателя — вызывается из Student.rateTeacher()
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            // Вычисляем новое среднее значение рейтинга
            this.rating = (this.rating * ratingCount + rating) / (ratingCount + 1);
            this.ratingCount++;
        }
    }

    // Получить текущий рейтинг преподавателя — нужен для отображения в demo
    public double getRating() {
        return rating;
    }

    // Получить должность преподавателя (указано в диаграмме явно)
    public TeacherPosition getPosition() {
        return position;
    }

    // Строковое представление преподавателя
    @Override
    public String toString() {
        return "Teacher{id='" + id + "', name='" + firstName + " " + lastName
                + "', position=" + position
                + ", rating=" + String.format("%.2f", rating)
                + ", courses=" + courses.size() + "}";
    }
}
