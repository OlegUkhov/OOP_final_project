import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Teacher extends Employee {

    private static final long serialVersionUID = 1L;

    private TeacherPosition position;
    private double rating;
    private int ratingCount;
    private boolean researcher;

    public Teacher(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, TeacherPosition position) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.position = position;
        this.rating = 0.0;
        this.ratingCount = 0;
        this.researcher = (position == TeacherPosition.PROFESSOR);
    }

    public List<Course> viewCourses() {
        return DataStorage.getInstance().getCoursesForTeacher(this);
    }

    public void manageCourse(Course c) {
        if (c != null) {
            c.addTeacher(this);
            DataStorage.getInstance().addCourse(c);
        }
    }

    public void putMark(Student s, Course c, Mark mark) {
        if (s == null || mark == null || !viewCourses().contains(c)) return;

        DataStorage.getInstance().addMark(mark);

        // Check retake status
        if ("RETAKE".equals(mark.getStatus())) {
            s.addRetakeCourse(c.getCourseId());
        }
    }

    /** Set the 1st attestation score for an existing mark. */
    public void setFirstAttestation(Student s, Course c, double value) {
        if (s == null || c == null || !viewCourses().contains(c)) return;
        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark != null && !mark.isAttestationClosed()) {
            mark.setFirstAttestation(value);
        }
    }

    /** Set the 2nd attestation score for an existing mark. */
    public void setSecondAttestation(Student s, Course c, double value) {
        if (s == null || c == null || !viewCourses().contains(c)) return;
        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark != null && !mark.isAttestationClosed()) {
            mark.setSecondAttestation(value);
        }
    }

    /** Set the final exam score and check for retake/FX. */
    public void setFinalExam(Student s, Course c, double value) {
        if (s == null || c == null || !viewCourses().contains(c)) return;
        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark != null) {
            mark.setFinalExam(value);
            // Check retake/FX after setting final
            if (mark.isFinalRetake()) {
                s.addRetakeCourse(c.getCourseId());
            }
        }
    }

    /** Close attestation period for a student's mark on a course.
     *  After closing, if attestation total < 29.5 → retake. */
    public void closeAttestation(Student s, Course c) {
        if (s == null || c == null || !viewCourses().contains(c)) return;
        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark != null && !mark.isAttestationClosed()) {
            mark.closeAttestation();
            if (mark.isAttestationRetake()) {
                s.addRetakeCourse(c.getCourseId());
            }
        }
    }

    public void addGradeEntry(Student s, Course c, double value, String description) {
        if (s == null || c == null || !viewCourses().contains(c)) return;
        GradeEntry ge = new GradeEntry(s.getId(), c.getCourseId(), value, description, new Date());
        DataStorage.getInstance().addGradeEntry(ge);
    }

    public void removeGradeEntry(String entryId) {
        DataStorage.getInstance().removeGradeEntry(entryId);
    }

    public List<GradeEntry> getJournal(Course c) {
        if (c == null || !viewCourses().contains(c)) return new java.util.ArrayList<>();
        return DataStorage.getInstance().getGradeEntriesForCourse(c.getCourseId());
    }

    public Complaint sendComplaint(Student s, String text, ComplaintUrgency urgency) {
        if (s == null || text == null || text.isEmpty()) return null;
        Complaint c = new Complaint(UUID.randomUUID().toString(), s, text, urgency, new Date());
        DataStorage.getInstance().addComplaint(c);
        return c;
    }

    public void viewStudentsInfo() {
        for (Course c : viewCourses()) {
            System.out.println("Course: " + c);
            for (Enrollment e : DataStorage.getInstance().getEnrollments()) {
                if (e.getCourseId().equals(c.getCourseId())) {
                    User u = DataStorage.getInstance().findUserById(e.getStudentId());
                    if (u != null) System.out.println("  Student: " + u);
                }
            }
        }
    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = (this.rating * ratingCount + rating) / (ratingCount + 1);
            this.ratingCount++;
        }
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public TeacherPosition getPosition() {
        return position;
    }

    public boolean isResearcher() {
        return researcher || position == TeacherPosition.PROFESSOR;
    }

    public void setResearcher(boolean researcher) {
        this.researcher = researcher;
    }

    public String generateMarksReport() {
        return DataStorage.getInstance().generateMarksReportForTeacher(this);
    }

    @Override
    public String toString() {
        String name = (firstName == null ? "" : firstName.trim()) + (lastName == null || lastName.trim().isEmpty() ? "" : " " + lastName.trim());
        String eid = employeeId != null && !employeeId.isEmpty() ? employeeId : id;
        return "Teacher{" + "empId='" + eid + "', name='" + name + "', position=" + position
                + ", rating=" + String.format("%.2f", rating)
                + ", courses=" + viewCourses().size() + "}";
    }
}
