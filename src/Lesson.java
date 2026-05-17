import java.util.Objects;

public class Lesson {

    private String lessonId;
    private String topic;
    private LessonType lessonType;

    public Lesson(String lessonId, String topic, LessonType lessonType) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
    }

    public String getLessonId() { return lessonId; }
    public String getTopic() { return topic; }
    public LessonType getLessonType() { return lessonType; }

    @Override
    public String toString() {
        return "Lesson{id='" + lessonId + "', topic='" + topic + "', type=" + lessonType + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        return Objects.equals(lessonId, ((Lesson) o).lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }
}
