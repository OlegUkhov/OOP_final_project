// Конкретный декоратор: преподаватель в роли исследователя.
// Оборачивает Teacher, добавляя ему возможности Researcher.
// Используется для профессоров и других преподавателей-исследователей.
public class TeacherResearcher extends ResearcherDecorator {

    // Ссылка на оборачиваемого преподавателя
    private Teacher teacher;

    // Конструктор — принимает преподавателя, которому присваивается роль исследователя
    public TeacherResearcher(Teacher teacher) {
        // Передаём null: Teacher сам по себе не Researcher, декоратор работает автономно
        super(null);
        this.teacher = teacher;
    }

    // Получить оборачиваемого преподавателя — нужен для отображения имени в DataStorage
    public Teacher getTeacher() {
        return teacher;
    }

    // Строковое представление преподавателя-исследователя
    @Override
    public String toString() {
        return "TeacherResearcher{name='" + (teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "null")
                + "', papers=" + papers.size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
