// Класс студента-бакалавра.
// Может регистрироваться на курсы (максимум 21 кредит), просматривать оценки,
// получать транскрипт, оценивать преподавателей и вступать в организации.
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    // Студенческий номер
    private String studentId;
    // Средний балл (GPA)
    protected double gpa;
    // Суммарное количество кредитов, набранных студентом
    private int credits;
    // Количество провалов курсов (не более 3)
    private int failCount;
    // Список курсов, на которые записан студент
    private List<Course> courses;
    // Список оценок студента
    private List<Mark> marks;
    // Список студенческих организаций, в которых состоит студент
    private List<StudentOrganization> organizations;

    // Конструктор — инициализирует студента с начальными нулевыми значениями
    public Student(String id, String firstName, String lastName, String email,
                   String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language);
        this.studentId = studentId;
        this.gpa = 0.0;
        this.credits = 0;
        this.failCount = 0;
        this.courses = new ArrayList<>();
        this.marks = new ArrayList<>();
        this.organizations = new ArrayList<>();
    }

    // Записаться на курс — проверяет лимит в 21 кредит
    public void registerForCourse(Course c) {
        if (c == null || courses.contains(c)) return;
        // Проверка: суммарные кредиты не должны превышать 21
        if (credits + c.getCredits() > 21) {
            throw new CourseOverloadException(
                "Cannot exceed 21 credits. Current: " + credits + ", course: " + c.getCredits());
        }
        courses.add(c);
        credits += c.getCredits();
    }

    // Получить список всех оценок студента
    public List<Mark> viewMarks() {
        return new ArrayList<>(marks);
    }

    // Сформировать и вернуть транскрипт студента в виде строки
    public String getTranscript() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Student Transcript ===\n");
        // Выводим идентификационные данные
        sb.append("ID: ").append(studentId).append("\n");
        sb.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("GPA: ").append(String.format("%.2f", gpa)).append("\n");
        sb.append("Credits: ").append(credits).append("/21\n");
        // Перечисляем все оценки
        for (Mark mark : marks) {
            sb.append("  ").append(mark).append("\n");
        }
        return sb.toString();
    }

    // Поставить оценку преподавателю (от 1 до 5)
    public void rateTeacher(Teacher t, int rating) {
        if (t != null && rating >= 1 && rating <= 5) {
            // Делегируем обновление рейтинга преподавателю
            t.addRating(rating);
        }
    }

    // Вступить в студенческую организацию
    public void joinOrganization(StudentOrganization org) {
        if (org != null && !organizations.contains(org)) {
            organizations.add(org);
            // Добавляем себя в список участников организации
            org.addMember(this);
        }
    }

    // Получить суммарное количество кредитов студента
    public int getTotalCredits() {
        return credits;
    }

    // Геттер GPA — нужен для сортировки студентов по успеваемости в Manager
    public double getGpa() {
        return gpa;
    }

    // Сеттер GPA — нужен для установки значения снаружи класса
    public void setGpa(double gpa) {
        if (gpa >= 0.0 && gpa <= 4.0) {
            this.gpa = gpa;
        }
    }

    // Геттер studentId — нужен для отображения в транскрипте
    public String getStudentId() {
        return studentId;
    }

    // Добавить оценку в список — нужен для Teacher.putMark()
    public void addMark(Mark mark) {
        if (mark != null && !marks.contains(mark)) {
            marks.add(mark);
        }
    }

    // Строковое представление студента
    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + firstName + " " + lastName
                + "', gpa=" + String.format("%.2f", gpa)
                + ", credits=" + credits + ", fails=" + failCount + "}";
    }
}
