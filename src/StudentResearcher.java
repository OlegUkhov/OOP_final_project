import java.util.Comparator;

public class StudentResearcher extends ResearcherDecorator {
    private Student student;

    public StudentResearcher(Student student) {
        super(null);  // Student сам по себе не Researcher, но мы оборачиваем его
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "StudentResearcher{" +
                "student=" + (student != null ? student.getFirstName() + " " + student.getLastName() : "null") +
                ", papers=" + papers.size() +
                ", h-index=" + calculateHIndex() +
                '}';
    }
}
