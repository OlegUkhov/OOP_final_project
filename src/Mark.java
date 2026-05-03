import java.util.Objects;

public class Mark {
    private String markId;
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    private Student student;
    private Course course;

    public Mark(double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
        this.markId = java.util.UUID.randomUUID().toString();
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
    }

    public double getTotalScore() {
        return (firstAttestation + secondAttestation + finalExam) / 3.0;
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

    public String getMarkId() {
        return markId;
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + (student != null ? student.getFirstName() : "null") +
                ", course=" + (course != null ? course.getName() : "null") +
                ", total=" + String.format("%.2f", getTotalScore()) +
                ", grade=" + getLetterGrade() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;
        Mark mark = (Mark) o;
        return Objects.equals(markId, mark.markId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markId);
    }
}
