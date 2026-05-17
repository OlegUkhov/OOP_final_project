import java.util.Objects;

public class Enrollment {

    private String studentId;
    private String courseId;

    public Enrollment(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;
        Enrollment e = (Enrollment) o;
        return Objects.equals(studentId, e.studentId) && Objects.equals(courseId, e.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }
}
