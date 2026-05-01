import java.util.ArrayList;
import java.util.List;

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
        if (!teachers.contains(t)) {
            teachers.add(t);
        }
    }

    public void addLesson(Lesson l) {
        if (!lessons.contains(l)) {
            lessons.add(l);
        }
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", courseType=" + courseType +
                '}';
    }

    public String getName(){
        return name;
    }
}