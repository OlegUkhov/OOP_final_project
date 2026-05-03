// A single class meeting inside a Course; either LECTURE or PRACTICE
import java.util.Objects;

public class Lesson {

    private String lessonId;
    private String topic;
    // LECTURE or PRACTICE; used to distinguish which teacher handles which session
    private LessonType lessonType;

    public Lesson(String lessonId, String topic, LessonType lessonType) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    @Override
    public String toString() {
        return "Lesson{id='" + lessonId + "', topic='" + topic + "', type=" + lessonType + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson l = (Lesson) o;
        return Objects.equals(lessonId, l.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }
}
