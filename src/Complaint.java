// A complaint submitted by a Teacher about a Student to the dean
// Urgency level is LOW MEDIUM or HIGH; stored in ComplaintUrgency enum
import java.util.Date;
import java.util.Objects;

public class Complaint {

    private String complaintId;
    // The student this complaint is about
    private Student student;
    private String text;
    // LOW MEDIUM HIGH set when Teacher.sendComplaint() creates this object
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

    public ComplaintUrgency getUrgency() {
        return urgency;
    }

    @Override
    public String toString() {
        return "Complaint{id='" + complaintId + "', student="
                + (student != null ? student.getFirstName() + " " + student.getLastName() : "null")
                + ", urgency=" + urgency + ", date=" + date + ", text='" + text + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        Complaint c = (Complaint) o;
        return Objects.equals(complaintId, c.complaintId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }
}
