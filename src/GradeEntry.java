import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single grade entry in the teacher's journal (gradebook).
 * Distinct from Mark (which holds attestation scores); GradeEntry is a
 * free-form numeric grade (0–100) that a teacher can add, edit, or delete
 * for any student enrolled in their course.
 */
public class GradeEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private String entryId;
    private String studentId;
    private String courseId;
    private double value;          // 0–100
    private String description;    // e.g. "Lab 1", "Quiz 3"
    private Date date;

    public GradeEntry(String studentId, String courseId, double value,
                      String description, Date date) {
        this.entryId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.value = value;
        this.description = description;
        this.date = date;
    }

    // Used when loading from storage with a known id
    public GradeEntry(String entryId, String studentId, String courseId,
                      double value, String description, Date date) {
        this.entryId = entryId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.value = value;
        this.description = description;
        this.date = date;
    }

    // ---- getters ----

    public String getEntryId()    { return entryId; }
    public String getStudentId()  { return studentId; }
    public String getCourseId()   { return courseId; }
    public double getValue()      { return value; }
    public String getDescription(){ return description; }
    public Date   getDate()       { return date; }

    // ---- setters (for edit) ----

    public void setValue(double value)           { this.value = value; }
    public void setDescription(String description){ this.description = description; }
    public void setDate(Date date)               { this.date = date; }

    @Override
    public String toString() {
        return String.format("GradeEntry{desc='%s', value=%.1f, date=%s}",
                description, value, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GradeEntry)) return false;
        return Objects.equals(entryId, ((GradeEntry) o).entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }
}
