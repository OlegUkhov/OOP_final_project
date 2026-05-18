import java.util.Objects;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class Lesson {

    private String lessonId;
    private String topic;
    private LessonType lessonType;
    // Optional schedule fields: day of week and start/end times
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public Lesson(String lessonId, String topic, LessonType lessonType) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
        this.dayOfWeek = null;
        this.startTime = null;
        this.endTime = null;
    }

    public Lesson(String lessonId, String topic, LessonType lessonType,
                  DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLessonId() { return lessonId; }
    public String getTopic() { return topic; }
    public LessonType getLessonType() { return lessonType; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    @Override
    public String toString() {
        String sched = "";
        if (dayOfWeek != null && startTime != null && endTime != null) {
            sched = ", schedule=" + dayOfWeek + " " + startTime + "-" + endTime;
        }
        return "Lesson{id='" + lessonId + "', topic='" + topic + "', type=" + lessonType + sched + "}";
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
