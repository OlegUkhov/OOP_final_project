// Занятие в рамках курса.
// Может быть лекцией (LECTURE) или практикой (PRACTICE).
import java.util.Objects;

public class Lesson {

    // Уникальный идентификатор занятия
    private String lessonId;
    // Тема занятия
    private String topic;
    // Тип занятия: LECTURE или PRACTICE
    private LessonType lessonType;

    // Конструктор — инициализирует все поля занятия
    public Lesson(String lessonId, String topic, LessonType lessonType) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
    }

    // Получить тип занятия (указано в диаграмме явно)
    public LessonType getLessonType() {
        return lessonType;
    }

    // Строковое представление занятия
    @Override
    public String toString() {
        return "Lesson{id='" + lessonId + "', topic='" + topic + "', type=" + lessonType + "}";
    }

    // Два занятия равны, если совпадают их lessonId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson l = (Lesson) o;
        return Objects.equals(lessonId, l.lessonId);
    }

    // Хэш-код по lessonId
    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }
}
