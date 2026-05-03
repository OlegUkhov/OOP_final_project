// Concrete decorator gives a Teacher the Researcher role
// Used for professors and any teacher assigned to research
// DataStorage.getAllResearchers() creates these automatically for PROFESSOR position teachers
public class TeacherResearcher extends ResearcherDecorator {

    // Reference to the wrapped teacher; used by DataStorage.getTopCitedResearcher() to retrieve name
    private Teacher teacher;

    // Passes null to super because Teacher itself does not implement Researcher
    // All researcher logic is handled by ResearcherDecorator
    public TeacherResearcher(Teacher teacher) {
        super(null);
        this.teacher = teacher;
    }

    // Used in Main section to print the name of the top cited researcher
    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "TeacherResearcher{name='" + (teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "null")
                + "', papers=" + papers.size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
