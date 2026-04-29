import java.util.ArrayList;
import java.util.List;

public class Student extends User{
    private String studentId;
    private double gpa;
    private int credits;
    private int failCount;
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

    public void registerForCourse(Course c) {
        if (courses.size() < 5) {
            courses.add(c);
        }
    }

    public List<Mark> viewMarks(){
        return new ArrayList<>(marks);
    }

    public String getTranscript(){
        return "Transctipt: " + marks.size() + " marks";
    }

    public void rateTeacher(Teacher t, int rating) {

    }

    public void joinOrganization(StudentOrganization org) {
        organizations.add(org);
    }

    public int getTotalCredits(){
        return credits;
    }

    @Override
    public String toString(){
        return "Id " + id + " student id " + studentId + " gpa " +
                gpa + " credits " + credits;
    }
}
