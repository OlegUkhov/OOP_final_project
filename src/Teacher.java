import java.util.ArrayList;
import java.util.List;

public class Teacher extends Employee {
    private TeacherPosition position;
    private List<Course> courses;
    private double rating;

    public Teacher(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
    }

    public List<Course> viewCourses() {
        return new ArrayList<>(courses);
    }

    public void manageCourse(Course c) {
        if (!courses.contains(c)) {
            courses.add(c);
        }
    }

    public void putMark(Student s, Course c, Mark mark) {
    }

    public Complaint sendComplaint(Student s, String text, ComplaintUrgency urgency) {
    }

    public TeacherPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Id " + id + " position " + position + " rating " + rating;
    }
}