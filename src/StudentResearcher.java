// Concrete decorator: gives a Student the Researcher role
// Used for GraduateStudent instances and any bachelor assigned to research
// GraduateStudent.setSupervisor() accepts any Researcher including this type
public class StudentResearcher extends ResearcherDecorator {

    // Reference to the wrapped student; needed to display name in toString and supervisor checks
    private Student student;

    // Passes null to super because Student itself does not implement Researcher
    public StudentResearcher(Student student) {
        super(null);
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "StudentResearcher{name='" + (student != null ? student.getFirstName() + " " + student.getLastName() : "null")
                + "', papers=" + papers.size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
