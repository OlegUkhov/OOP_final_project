import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {
    private Researcher supervisor;
    private List<ResearchPaper> diplomaPapers;

    public GraduateStudent(String id, String firstName, String lastName, String email,
                           String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language, studentId);
        this.supervisor = null;
        this.diplomaPapers = new ArrayList<>();
    }

    // Установить научного руководителя
    // Проверка: руководитель должен быть Researcher с h-index >= 3
    public void setSupervisor(Researcher researcher) throws LowHIndexException {
        if (researcher != null) {
            int hIndex = researcher.calculateHIndex();
            if (hIndex < 3) {
                throw new LowHIndexException("Supervisor must have h-index >= 3. Current h-index: " + hIndex);
            }
            this.supervisor = researcher;
        }
    }

    // Опубликовать дипломную статью
    public void publishDiplomaPaper(ResearchPaper paper) {
        if (paper != null && !diplomaPapers.contains(paper)) {
            diplomaPapers.add(paper);
        }
    }

    public Researcher getSupervisor() {
        return supervisor;
    }

    public List<ResearchPaper> getDiplomaPapers() {
        return new ArrayList<>(diplomaPapers);
    }

    @Override
    public String toString() {
        return "GraduateStudent{" +
                "id='" + id + '\'' +
                ", name='" + firstName + " " + lastName + '\'' +
                ", gpa=" + String.format("%.2f", gpa) +
                ", supervisor=" + (supervisor != null ? "assigned" : "none") +
                ", diplomaPapers=" + diplomaPapers.size() +
                '}';
    }
}
