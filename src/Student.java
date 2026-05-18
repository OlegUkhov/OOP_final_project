import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class Student extends User {

    private static final long serialVersionUID = 1L;

    private String studentId;
    protected double gpa;
    protected int failCount;
    private boolean researcher;
    private int studyYear;      // 1-7
    private String faculty;     // e.g. "SITE", "Business"
    private boolean graduated;

    public Student(String id, String firstName, String lastName, String email,
                   String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language);
        this.studentId = studentId;
        this.gpa = 0.0;
        this.failCount = 0;
        this.researcher = false;
        this.studyYear = 1;
        this.faculty = "";
        this.graduated = false;
    }

    public void registerForCourse(Course c, Lesson lesson) {
        if (c == null || lesson == null || DataStorage.getInstance().isEnrolledForLesson(this, c, lesson)) return;
        if (failCount >= 3) {
            throw new CourseFailLimitException(
                    "Cannot register: exceeded 3 course failures (" + failCount + ")");
        }
        int total = getTotalCredits() + c.getCredits();
        if (total > 21) {
            throw new CourseOverloadException(
                    "Cannot exceed 21 credits. Current " + getTotalCredits()
                            + ", course " + c.getCredits());
        }
        DataStorage.getInstance().enrollStudent(this, c, lesson);
    }

    public void submitRequest(String description) {
        if (description == null || description.isEmpty()) return;
        Request req = new Request(UUID.randomUUID().toString(), this, description);
        DataStorage.getInstance().addMessage(req);
        System.out.println("[REQUEST] Submitted: " + description);
    }

    public List<Course> viewCourses() {
        return DataStorage.getInstance().getCoursesForStudent(this);
    }

    public List<Teacher> viewTeachersForCourse(Course course) {
        if (course == null) return new java.util.ArrayList<>();
        return course.getTeachers();
    }

    public List<Mark> viewMarks() {
        return DataStorage.getInstance().getMarksForStudent(this);
    }

    public String getTranscript() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Student Transcript ===\n");
        sb.append("ID: ").append(studentId).append("\n");
        sb.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("Faculty: ").append(faculty.isEmpty() ? "—" : faculty).append("\n");
        sb.append("Year: ").append(studyYear).append(graduated ? " (Graduated)" : "").append("\n");
        sb.append("GPA: ").append(String.format("%.2f", gpa)).append("\n");
        sb.append("Credits: ").append(getTotalCredits()).append("/21\n");
        sb.append("Failures: ").append(failCount).append("/3\n");
        for (Mark mark : viewMarks()) {
            sb.append("  ").append(mark).append("\n");
        }
        return sb.toString();
    }

    public void rateTeacher(Teacher t, int rating) {
        if (t != null && rating >= 1 && rating <= 5) {
            t.addRating(rating);
        }
    }

    public void joinOrganization(StudentOrganization org) {
        if (org != null) {
            org.addMember(this);
            DataStorage.getInstance().addOrganization(org);
        }
    }

    public void markAttendance(Course c, boolean present) {
        if (c == null) return;
        DataStorage ds = DataStorage.getInstance();
        Lesson matching = null;
        for (Lesson lesson : c.getLessons()) {
            if (ds.isAttendanceOpen(c.getCourseId(), lesson.getLessonId())) {
                matching = lesson;
                break;
            }
        }
        if (matching == null) {
            System.out.println("Attendance is not open for any lesson of this course.");
            return;
        }
        Attendance a = new Attendance(java.util.UUID.randomUUID().toString(), c.getCourseId(), matching.getLessonId(), this.getId(), new java.util.Date(), present);
        ds.addAttendance(a);
        System.out.println("Attendance recorded: " + (present ? "present" : "absent") + " for " + c.getCourseId());
    }

    public java.util.List<Attendance> viewAttendanceList() {
        return DataStorage.getInstance().getAttendancesForStudent(this.getId());
    }

    public int getTotalCredits() {
        int total = 0;
        for (Course c : viewCourses()) {
            total += c.getCredits();
        }
        return total;
    }

    public int getFailCount()          { return failCount; }
    public double getGpa()             { return gpa; }
    public void setGpa(double gpa)     { if (gpa >= 0.0 && gpa <= 4.0) this.gpa = gpa; }
    public String getStudentId()       { return studentId; }
    public boolean isResearcher()      { return researcher; }
    public void setResearcher(boolean r) { this.researcher = r; }

    public int getStudyYear()          { return studyYear; }
    public void setStudyYear(int y)    { if (y >= 1 && y <= 7) this.studyYear = y; }
    public String getFaculty()         { return faculty; }
    public void setFaculty(String f)   { this.faculty = (f != null ? f : ""); }
    public boolean isGraduated()       { return graduated; }
    public void setGraduated(boolean g){ this.graduated = g; }

    public void viewAllNews() {
        DataStorage.getInstance().printNewsFeed(this);
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + firstName + " " + lastName
                + "', faculty='" + faculty + "', year=" + studyYear
                + ", gpa=" + String.format("%.2f", gpa)
                + ", credits=" + getTotalCredits() + ", fails=" + failCount
                + (graduated ? ", GRADUATED" : "") + "}";
    }
}