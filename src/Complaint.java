import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    private String complaintId;
    private Student student;
    private String text;
    private ComplaintUrgency urgency;
    private Date date;

    public Complaint(String complaintId, Student student, String text,
                     ComplaintUrgency urgency, Date date) {
        this.complaintId = complaintId;
        this.student = student;
        this.text = text;
        this.urgency = urgency;
        this.date = date;
    }

    public String getComplaintId() { return complaintId; }
    public Student getStudent() { return student; }
    public String getText() { return text; }
    public ComplaintUrgency getUrgency() { return urgency; }

    @Override
    public String toString() {
        return "Complaint{student=" + (student != null ? student.getFirstName() : "null")
                + ", urgency=" + urgency + ", text='" + text + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        return Objects.equals(complaintId, ((Complaint) o).complaintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }
}
