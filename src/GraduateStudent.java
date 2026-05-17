import java.util.List;

public class GraduateStudent extends Student {

    private static final long serialVersionUID = 1L;

    private transient Researcher supervisor;

    public GraduateStudent(String id, String firstName, String lastName, String email,
                           String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language, studentId);
        setResearcher(true);
        this.supervisor = null;
    }

    public void setSupervisor(Researcher researcher) throws LowHIndexException {
        if (researcher == null) return;
        int hIndex = researcher.calculateHIndex();
        if (hIndex < 3) {
            throw new LowHIndexException(
                    "Supervisor h-index must be >= 3. Current: " + hIndex);
        }
        this.supervisor = researcher;
    }

    public void publishDiplomaPaper(ResearchPaper paper) {
        if (paper != null) {
            DataStorage.getInstance().addDiplomaPaper(id, paper);
        }
    }

    public List<ResearchPaper> getDiplomaPapers() {
        return DataStorage.getInstance().getDiplomaPapers(id);
    }

    @Override
    public String toString() {
        return "GraduateStudent{id='" + id + "', name='" + firstName + " " + lastName
                + "', gpa=" + String.format("%.2f", gpa)
                + ", supervisor=" + (supervisor != null ? "assigned" : "none")
                + ", diplomaPapers=" + getDiplomaPapers().size() + "}";
    }
}
