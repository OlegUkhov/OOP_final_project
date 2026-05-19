import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Mark implements Serializable {

    private static final long serialVersionUID = 2L;

    private String markId;
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    private Student student;
    private Course course;
    private boolean attestationClosed; // true after teacher closes attestation period

    public Mark(double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
        this.markId = UUID.randomUUID().toString();
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
        this.attestationClosed = false;
    }

    public Mark(String markId, double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
        this.markId = markId;
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
        this.attestationClosed = false;
    }

    public String getMarkId() { return markId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public double getFirstAttestation() { return firstAttestation; }
    public double getSecondAttestation() { return secondAttestation; }
    public double getFinalExam() { return finalExam; }
    public boolean isAttestationClosed() { return attestationClosed; }

    public void setFirstAttestation(double val) { this.firstAttestation = val; }
    public void setSecondAttestation(double val) { this.secondAttestation = val; }
    public void setFinalExam(double val) { this.finalExam = val; }

    /** Close the attestation period. After closing, attestation scores are locked.
     *  If total attestation score < 29.5 the student gets RETAKE status. */
    public void closeAttestation() {
        this.attestationClosed = true;
    }

    public double getAttestationTotal() {
        return firstAttestation + secondAttestation;
    }

    public double getTotalScore() {
        return firstAttestation + secondAttestation + finalExam;
    }

    /** Check if student needs retake due to low attestation total (< 29.5). */
    public boolean isAttestationRetake() {
        return attestationClosed && getAttestationTotal() < 29.5;
    }

    /** Check if student needs retake due to low final exam (< 9.5). */
    public boolean isFinalRetake() {
        return finalExam > 0 && finalExam < 9.5;
    }

    /** Check if student has FX (conditional fail) on final: 9.5 <= final <= 19.5. */
    public boolean isFX() {
        return finalExam >= 9.5 && finalExam <= 19.5;
    }

    /** Overall pass/retake/fx status for this mark. */
    public String getStatus() {
        if (isAttestationRetake() || isFinalRetake()) return "RETAKE";
        if (isFX()) return "FX";
        return "PASS";
    }

    public String getLetterGrade() {
        double score = getTotalScore();
        if (score >= 94.5) return "A";
        if (score >= 89.5) return "A-";
        if (score >= 84.5) return "B+";
        if (score >= 79.5) return "B";
        if (score >= 74.5) return "B-";
        if (score >= 69.5) return "C+";
        if (score >= 64.5) return "C";
        if (score >= 59.5) return "C-";
        if (score >= 54.5) return "D+";
        if (score >= 49.5) return "D";
        return "F";
    }

    @Override
    public String toString() {
        String status = getStatus();
        return "Mark{student=" + (student != null ? student.getFirstName() : "null")
                + ", course=" + (course != null ? course.getName() : "null")
                + ", att1=" + String.format("%.1f", firstAttestation)
                + ", att2=" + String.format("%.1f", secondAttestation)
                + ", final=" + String.format("%.1f", finalExam)
                + ", total=" + String.format("%.1f", getTotalScore())
                + ", grade=" + getLetterGrade()
                + ", status=" + status
                + (attestationClosed ? " [ATTESTATION CLOSED]" : "")
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;
        Mark m = (Mark) o;
        return Objects.equals(markId, m.markId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markId);
    }
}
