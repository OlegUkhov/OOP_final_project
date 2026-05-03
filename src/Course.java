// Курс университета.
// Содержит информацию о кредитах, типе, преподавателях и занятиях (Lesson).
// Один курс может вести несколько преподавателей (лекции и практики отдельно).
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {

    // Уникальный код курса
    private String courseId;
    // Название курса
    private String name;
    // Количество кредитов курса
    private int credits;
    // Тип курса: MAJOR, MINOR или FREE_ELECTIVE
    private CourseType courseType;
    // Список преподавателей курса
    private List<Teacher> teachers;
    // Список занятий (лекции и практики)
    private List<Lesson> lessons;

    // Конструктор — создаёт курс с пустыми списками преподавателей и занятий
    public Course(String courseId, String name, int credits, CourseType courseType) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.teachers = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    // Добавить преподавателя в курс
    public void addTeacher(Teacher t) {
        if (t != null && !teachers.contains(t)) {
            teachers.add(t);
        }
    }

    // Добавить занятие в курс (лекцию или практику)
    public void addLesson(Lesson l) {
        if (l != null && !lessons.contains(l)) {
            lessons.add(l);
        }
    }

    // Получить список занятий курса
    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    // Получить количество кредитов (указано в диаграмме явно)
    public int getCredits() {
        return credits;
    }

    // Получить название курса — нужно для отображения в Mark.toString()
    public String getName() {
        return name;
    }

    // Строковое представление курса
    @Override
    public String toString() {
        return "Course{id='" + courseId + "', name='" + name + "', credits=" + credits
                + ", type=" + courseType + ", teachers=" + teachers.size()
                + ", lessons=" + lessons.size() + "}";
    }

    // Два курса равны, если совпадают их courseId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course c = (Course) o;
        return Objects.equals(courseId, c.courseId);
    }

    // Хэш-код по courseId
    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
