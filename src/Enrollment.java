import java.io.Serializable;
import java.util.Objects;

public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String courseId;
    private String lessonId;

    public Enrollment(String studentId, String courseId, String lessonId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.lessonId = lessonId;
    }

    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getLessonId() { return lessonId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;
        Enrollment e = (Enrollment) o;
        return Objects.equals(studentId, e.studentId) && Objects.equals(courseId, e.courseId) && Objects.equals(lessonId, e.lessonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId, lessonId);
    }
}
