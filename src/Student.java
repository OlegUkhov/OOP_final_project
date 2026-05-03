import java.util.ArrayList;
import java.util.List;

public class Student extends User{
    private String studentId;
    protected double gpa;
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
        if (c != null && !courses.contains(c)) {
            int newCredits = credits + c.getCredits();
            if (newCredits <= 21) {
                courses.add(c);
                credits = newCredits;
            } else {
                throw new CourseOverloadException("Cannot exceed 21 credits. Current: " + credits + ", Course: " + c.getCredits());
            }
        }
    }

    public List<Mark> viewMarks(){
        return new ArrayList<>(marks);
    }

    public String getTranscript(){
        StringBuilder transcript = new StringBuilder();
        transcript.append("=== Student Transcript ===\n");
        transcript.append("Student ID: ").append(studentId).append("\n");
        transcript.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
        transcript.append("GPA: ").append(String.format("%.2f", gpa)).append("\n");
        transcript.append("Credits: ").append(credits).append("/21\n");
        transcript.append("Marks: ").append(marks.size()).append("\n");
        for (Mark mark : marks) {
            transcript.append("  - ").append(mark.toString()).append("\n");
        }
        return transcript.toString();
    }

    public void rateTeacher(Teacher t, int rating) {
        if (t != null && rating >= 1 && rating <= 5) {
            // Логика рейтинга — в простой версии просто обновляем рейтинг учителя
            t.addRating(rating);
        }
    }

    public void joinOrganization(StudentOrganization org) {
        if (org != null && !organizations.contains(org)) {
            organizations.add(org);
            org.addMember(this);
        }
    }

    public int getTotalCredits(){
        return credits;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        if (gpa >= 0.0 && gpa <= 4.0) {
            this.gpa = gpa;
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void addMark(Mark mark) {
        if (mark != null && !marks.contains(mark)) {
            marks.add(mark);
        }
    }

    @Override
    public String toString(){
        return "Student{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", gpa=" + String.format("%.2f", gpa) +
                ", credits=" + credits +
                ", failCount=" + failCount +
                '}';
    }
}
