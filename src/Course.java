// A university course with credits type teachers and lessons
// One course can have multiple teachers: one for LECTURE and one for PRACTICE
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {

    private String courseId;
    private String name;
    // Checked by Student.registerForCourse() and Manager.approveRegistration() against the 21 limit
    private int credits;
    // MAJOR MINOR or FREE_ELECTIVE
    private CourseType courseType;
    private List<Teacher> teachers;
    // Each Lesson has a LessonType to distinguish lectures from practices
    private List<Lesson> lessons;

    public Course(String courseId, String name, int credits, CourseType courseType) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.teachers = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    // Called by Teacher.manageCourse() to register the teacher on the course side
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

    // Read by Student.registerForCourse() and Manager.approveRegistration() for credit checks
    public int getCredits() {
        return credits;
    }

    // Read by Mark.toString() to show which course the mark belongs to
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Course{id='" + courseId + "', name='" + name + "', credits=" + credits
                + ", type=" + courseType + ", teachers=" + teachers.size()
                + ", lessons=" + lessons.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course c = (Course) o;
        return Objects.equals(courseId, c.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
