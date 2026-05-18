import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String courseId;
    private String name;
    private int credits;
    private CourseType courseType;
    private String major;
    private int studyYear;
    private List<Teacher> teachers;
    private List<Lesson> lessons;

    public Course(String courseId, String name, int credits, CourseType courseType) {
        this(courseId, name, credits, courseType, "SITE", 2);
    }

    public Course(String courseId, String name, int credits, CourseType courseType,
                  String major, int studyYear) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.major = major;
        this.studyYear = studyYear;
        this.teachers = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public void addTeacher(Teacher t) {
        if (t != null && !teachers.contains(t)) teachers.add(t);
    }

    public void addLesson(Lesson l) {
        if (l != null && !lessons.contains(l)) lessons.add(l);
    }

    public List<Lesson> getLessons() { return new ArrayList<>(lessons); }
    public List<Teacher> getTeachers() { return new ArrayList<>(teachers); }
    public String getCourseId() { return courseId; }
    public int getCredits() { return credits; }
    public String getName() { return name; }
    public CourseType getCourseType() { return courseType; }
    public String getMajor() { return major; }
    public int getStudyYear() { return studyYear; }

    public void setMajor(String major) {
        if (major != null) this.major = major;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    @Override
    public String toString() {
        return "Course{id='" + courseId + "', name='" + name + "', credits=" + credits + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return Objects.equals(courseId, ((Course) o).courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
