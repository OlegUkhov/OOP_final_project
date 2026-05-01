public class Mark {
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    private Student student;
    private Course course;

    public Mark(double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
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

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + student.getFirstName() +
                ", course=" + course.getName() +
                ", total=" + String.format("%.2f", getTotalScore()) +
                ", grade=" + getLetterGrade() +
                '}';
    }
}
