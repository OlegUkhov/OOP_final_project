import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Teacher extends Employee {
    private TeacherPosition position;
    private List<Course> courses;
    private double rating;
    private int ratingCount;
    private List<Complaint> complaints;

    public Teacher(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.rating = 0.0;
        this.ratingCount = 0;
        this.complaints = new ArrayList<>();
    }

    public List<Course> viewCourses() {
        return new ArrayList<>(courses);
    }

    public void manageCourse(Course c) {
        if (c != null && !courses.contains(c)) {
            courses.add(c);
            c.addTeacher(this);
        }
    }

    public void putMark(Student s, Course c, Mark mark) {
        if (s != null && mark != null && courses.contains(c)) {
            s.addMark(mark);
        }
    }

    public Complaint sendComplaint(Student s, String text, ComplaintUrgency urgency) {
        if (s == null || text == null || text.isEmpty()) {
            return null;
        }
        Complaint complaint = new Complaint(
            UUID.randomUUID().toString(),
            s,
            text,
            urgency,
            new Date()
        );
        complaints.add(complaint);
        return complaint;
    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = (this.rating * ratingCount + rating) / (ratingCount + 1);
            this.ratingCount++;
        }
    }

    public TeacherPosition getPosition() {
        return position;
    }

    public double getRating() {
        return rating;
    }

    public List<Complaint> getComplaints() {
        return new ArrayList<>(complaints);
    }

    public int getComplaintsCount() {
        return complaints.size();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + id + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", position=" + position +
                ", rating=" + String.format("%.2f", rating) +
                ", courses=" + courses.size() +
                '}';
    }
}