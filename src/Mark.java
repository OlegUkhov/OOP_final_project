// Grade record linking a Student to a Course
// Three components: first attestation second attestation final exam
// getTotalScore() averages them; getLetterGrade() maps the average to A-F scale
import java.util.Objects;
import java.util.UUID;

public class Mark {

    private String markId;
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    // Link to Student so the mark can display who it belongs to in toString()
    private Student student;
    // Link to Course so the mark can display which course it covers in toString()
    private Course course;

    public Mark(double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
        this.markId = UUID.randomUUID().toString();
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
    }

    // Simple arithmetic mean of all three scores
    public double getTotalScore() {
        return (firstAttestation + secondAttestation + finalExam) / 3.0;
    }

    // Threshold boundaries follow a university scale
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
        return "Mark{student=" + (student != null ? student.getFirstName() : "null")
                + ", course=" + (course != null ? course.getName() : "null")
                + ", total=" + String.format("%.2f", getTotalScore())
                + ", grade=" + getLetterGrade() + "}";
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
