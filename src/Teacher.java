// University teacher; manages courses marks and complaints
// Can be wrapped by TeacherResearcher to gain Researcher capabilities
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Teacher extends Employee {

    // TUTOR LECTOR SENIOR_LECTOR PROFESSOR - used by DataStorage.getAllResearchers()
    // to decide if the teacher is automatically a researcher
    private TeacherPosition position;
    private List<Course> courses;
    private double rating;
    private int ratingCount; // needed to calculate running average in addRating()

    public Teacher(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    public List<Course> viewCourses() {
        return new ArrayList<>(courses);
    }

    // Registers this teacher inside the Course object as well to keep both sides in sync
    public void manageCourse(Course c) {
        if (c != null && !courses.contains(c)) {
            courses.add(c);
            c.addTeacher(this);
        }
    }

    // Pushes the Mark into student via Student.addMark()
    // Guard checks that this teacher actually owns the course
    public void putMark(Student s, Course c, Mark mark) {
        if (s != null && mark != null && courses.contains(c)) {
            s.addMark(mark);
        }
    }

    // Returns a Complaint object; caller (dean or manager) is responsible for storing it
    public Complaint sendComplaint(Student s, String text, ComplaintUrgency urgency) {
        if (s == null || text == null || text.isEmpty()) return null;
        return new Complaint(UUID.randomUUID().toString(), s, text, urgency, new Date());
    }

    public void viewStudentsInfo() {
        for (Course c : courses) {
            System.out.println("Course: " + c);
        }
    }

    // Called from Student.rateTeacher(); updates running average without storing all individual scores
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = (this.rating * ratingCount + rating) / (ratingCount + 1);
            this.ratingCount++;
        }
    }

    public double getRating() {
        return rating;
    }

    // Read by DataStorage.getAllResearchers() to auto-include professors in the researcher list
    public TeacherPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Teacher{id='" + id + "', name='" + firstName + " " + lastName
                + "', position=" + position
                + ", rating=" + String.format("%.2f", rating)
                + ", courses=" + courses.size() + "}";
    }
}
