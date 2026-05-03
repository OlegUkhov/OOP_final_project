// Bachelor-level student
// Can register for courses up to 21 credits and fail at most 3 times
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private String studentId;
    protected double gpa;
    private int credits;       // total credits currently registered; enforced against 21 limit
    private int failCount;     // incremented externally when a course is failed; capped at 3
    private List<Course> courses;
    private List<Mark> marks;
    private List<StudentOrganization> organizations;

    public Student(String id, String firstName, String lastName, String email,
                   String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language);
        this.studentId = studentId;
        this.gpa = 0.0;
        this.credits = 0;
        this.failCount = 0;
        this.courses = new ArrayList<>();
        this.marks = new ArrayList<>();
        this.organizations = new ArrayList<>();
    }

    // Checks credit limit before adding the course; throws CourseOverloadException if exceeded
    // Manager.approveRegistration() calls this after its own pre-check
    public void registerForCourse(Course c) {
        if (c == null || courses.contains(c)) return;
        if (credits + c.getCredits() > 21) {
            throw new CourseOverloadException(
                "Cannot exceed 21 credits. Current: " + credits + ", course: " + c.getCredits());
        }
        courses.add(c);
        credits += c.getCredits();
    }

    public List<Mark> viewMarks() {
        return new ArrayList<>(marks);
    }

    // Builds a plain-text report; called directly in Main and Teacher panel scenarios
    public String getTranscript() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Student Transcript ===\n");
        sb.append("ID: ").append(studentId).append("\n");
        sb.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("GPA: ").append(String.format("%.2f", gpa)).append("\n");
        sb.append("Credits: ").append(credits).append("/21\n");
        for (Mark mark : marks) {
            sb.append("  ").append(mark).append("\n");
        }
        return sb.toString();
    }

    // Delegates to Teacher.addRating() which recalculates the running average
    public void rateTeacher(Teacher t, int rating) {
        if (t != null && rating >= 1 && rating <= 5) {
            t.addRating(rating);
        }
    }

    // Also calls org.addMember(this) so the organization list stays consistent
    public void joinOrganization(StudentOrganization org) {
        if (org != null && !organizations.contains(org)) {
            organizations.add(org);
            org.addMember(this);
        }
    }

    public int getTotalCredits() {
        return credits;
    }

    // Read by Manager.getStudentsSortedByGpa() comparator
    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        if (gpa >= 0.0 && gpa <= 4.0) {
            this.gpa = gpa;
        }
    }

    // Used in getTranscript()
    public String getStudentId() {
        return studentId;
    }

    // Called by Teacher.putMark() to push the Mark object into the student own list
    public void addMark(Mark mark) {
        if (mark != null && !marks.contains(mark)) {
            marks.add(mark);
        }
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + firstName + " " + lastName
                + "', gpa=" + String.format("%.2f", gpa)
                + ", credits=" + credits + ", fails=" + failCount + "}";
    }
}
