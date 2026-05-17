public class StudentResearcher extends ResearcherDecorator {

    private Student student;

    public StudentResearcher(Student student) {
        super(null, student != null ? student.getId() : null);
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "StudentResearcher{name='" + (student != null
                ? student.getFirstName() + " " + student.getLastName() : "null")
                + "', papers=" + getPapers().size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
