import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {
    private String courseId;
    private String name;
    private int credits;
    private CourseType courseType;
    private List<Teacher> teachers;
    private List<Lesson> lessons;

    public Course(String courseId, String name, int credits, CourseType courseType) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.teachers = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public void addTeacher(Teacher t) {
        if (t != null && !teachers.contains(t)) {
            teachers.add(t);
        }
    }

    public void addLesson(Lesson l) {
        if (l != null && !lessons.contains(l)) {
            lessons.add(l);
        }
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    public List<Teacher> getTeachers() {
        return new ArrayList<>(teachers);
    }

    public int getCredits() {
        return credits;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName(){
        return name;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", courseType=" + courseType +
                ", teachers=" + teachers.size() +
                ", lessons=" + lessons.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}