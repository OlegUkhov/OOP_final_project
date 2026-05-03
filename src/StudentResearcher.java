// Конкретный декоратор: студент (выпускник) в роли исследователя.
// Оборачивает Student (обычно GraduateStudent), добавляя возможности Researcher.
// PhD- и магистранты всегда являются исследователями.
public class StudentResearcher extends ResearcherDecorator {

    // Ссылка на оборачиваемого студента
    private Student student;

    // Конструктор — принимает студента, которому присваивается роль исследователя
    public StudentResearcher(Student student) {
        // Передаём null: Student сам по себе не Researcher, декоратор работает автономно
        super(null);
        this.student = student;
    }

    // Получить оборачиваемого студента — нужен для отображения имени
    public Student getStudent() {
        return student;
    }

    // Строковое представление студента-исследователя
    @Override
    public String toString() {
        return "StudentResearcher{name='" + (student != null ? student.getFirstName() + " " + student.getLastName() : "null")
                + "', papers=" + papers.size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
