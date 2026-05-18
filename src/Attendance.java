import java.util.Date;

public class Attendance {

    private String id;
    private String courseId;
    private String lessonId;
    private String studentId;
    private Date date;
    private boolean present;

    public Attendance(String id, String courseId, String lessonId, String studentId, Date date, boolean present) {
        this.id = id;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }

    public String getId() { return id; }
    public String getCourseId() { return courseId; }
    public String getLessonId() { return lessonId; }
    public String getStudentId() { return studentId; }
    public Date getDate() { return date; }
    public boolean isPresent() { return present; }

    @Override
    public String toString() {
        return "Attendance{" + "course=" + courseId + ", student=" + studentId
                + ", date=" + date + ", present=" + present + '}';
    }
}
