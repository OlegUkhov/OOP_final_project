import java.io.Serializable;
import java.util.Objects;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    private String lessonId;
    private String topic;
    private LessonType lessonType;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String teacherId; // Added to link lesson to a specific teacher
    private AtomicInteger enrolledStudentsCount; // New field for student counter
    private int capacity; // New field for lesson capacity

    public Lesson(String lessonId, String topic, LessonType lessonType) {
        this(lessonId, topic, lessonType, null, null, null, null, (lessonType == LessonType.LECTURE ? 75 : 25));
    }

    public Lesson(String lessonId, String topic, LessonType lessonType,
                  DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this(lessonId, topic, lessonType, dayOfWeek, startTime, endTime, null, (lessonType == LessonType.LECTURE ? 75 : 25));
    }

    public Lesson(String lessonId, String topic, LessonType lessonType,
                  DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String teacherId, int capacity) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.lessonType = lessonType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherId = teacherId;
        this.capacity = capacity;
        this.enrolledStudentsCount = new AtomicInteger(0);
    }

    public String getLessonId() { return lessonId; }
    public String getTopic() { return topic; }
    public LessonType getLessonType() { return lessonType; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getTeacherId() { return teacherId; }
    public int getEnrolledStudentsCount() { return enrolledStudentsCount.get(); }
    public int getCapacity() { return capacity; }

    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public void incrementEnrolledStudentsCount() { this.enrolledStudentsCount.incrementAndGet(); }
    public void decrementEnrolledStudentsCount() { this.enrolledStudentsCount.decrementAndGet(); }

    @Override
    public String toString() {
        String sched = "";
        if (dayOfWeek != null && startTime != null && endTime != null) {
            sched = ", schedule=" + dayOfWeek + " " + startTime + "-" + endTime;
        }
        return "Lesson{id=\'" + lessonId + "\', topic=\'" + topic + "\', type=" + lessonType + ", teacherId=\'" + teacherId + "\', enrolled=" + enrolledStudentsCount.get() + "/" + capacity + sched + "}";
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