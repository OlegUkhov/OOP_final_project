public class Lesson {
    private String lessonId;
    private String topic;
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
        return "Lesson{" +
                "lessonId='" + lessonId + '\'' +
                ", topic='" + topic + '\'' +
                ", lessonType=" + lessonType +
                '}';
    }
}

