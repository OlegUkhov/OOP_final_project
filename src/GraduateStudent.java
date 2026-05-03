// Master or PhD student; extends Student with a research supervisor and diploma papers
// Supervisor must be a Researcher with h-index >= 3 otherwise LowHIndexException is thrown
import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {

    // Points to a TeacherResearcher or StudentResearcher wrapped object
    private Researcher supervisor;
    private List<ResearchPaper> diplomaPapers;

    public GraduateStudent(String id, String firstName, String lastName, String email,
                           String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language, studentId);
        this.supervisor = null;
        this.diplomaPapers = new ArrayList<>();
    }

    // Calls researcher.calculateHIndex() which is implemented in ResearcherDecorator
    public void setSupervisor(Researcher researcher) throws LowHIndexException {
        if (researcher == null) return;
        int hIndex = researcher.calculateHIndex();
        if (hIndex < 3) {
            throw new LowHIndexException(
                "Supervisor h-index must be >= 3. Current: " + hIndex);
        }
        this.supervisor = researcher;
    }

    // Adds a paper to the diploma list; separate from ResearcherDecorator.papers
    public void publishDiplomaPaper(ResearchPaper paper) {
        if (paper != null && !diplomaPapers.contains(paper)) {
            diplomaPapers.add(paper);
        }
    }

    public List<ResearchPaper> getDiplomaPapers() {
        return new ArrayList<>(diplomaPapers);
    }

    @Override
    public String toString() {
        return "GraduateStudent{id='" + id + "', name='" + firstName + " " + lastName
                + "', gpa=" + String.format("%.2f", gpa)
                + ", supervisor=" + (supervisor != null ? "assigned" : "none")
                + ", diplomaPapers=" + diplomaPapers.size() + "}";
    }
}
