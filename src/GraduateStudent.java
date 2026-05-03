// Класс выпускника (магистрант или PhD-студент).
// Расширяет Student. Имеет научного руководителя (Researcher)
// и список дипломных статей. Руководитель должен иметь h-index >= 3.
import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {

    // Научный руководитель (должен быть Researcher с h-index >= 3)
    private Researcher supervisor;
    // Список дипломных статей выпускника
    private List<ResearchPaper> diplomaPapers;

    // Конструктор — инициализирует выпускника без руководителя
    public GraduateStudent(String id, String firstName, String lastName, String email,
                           String password, Language language, String studentId) {
        super(id, firstName, lastName, email, password, language, studentId);
        this.supervisor = null;
        this.diplomaPapers = new ArrayList<>();
    }

    // Назначить научного руководителя.
    // Бросает LowHIndexException, если h-index руководителя меньше 3.
    public void setSupervisor(Researcher researcher) throws LowHIndexException {
        if (researcher == null) return;
        int hIndex = researcher.calculateHIndex();
        // Проверка: руководитель должен иметь h-index не меньше 3
        if (hIndex < 3) {
            throw new LowHIndexException(
                "Supervisor h-index must be >= 3. Current: " + hIndex);
        }
        this.supervisor = researcher;
    }

    // Опубликовать дипломную статью
    public void publishDiplomaPaper(ResearchPaper paper) {
        if (paper != null && !diplomaPapers.contains(paper)) {
            diplomaPapers.add(paper);
        }
    }

    // Получить список дипломных статей
    public List<ResearchPaper> getDiplomaPapers() {
        return new ArrayList<>(diplomaPapers);
    }

    // Строковое представление выпускника
    @Override
    public String toString() {
        return "GraduateStudent{id='" + id + "', name='" + firstName + " " + lastName
                + "', gpa=" + String.format("%.2f", gpa)
                + ", supervisor=" + (supervisor != null ? "assigned" : "none")
                + ", diplomaPapers=" + diplomaPapers.size() + "}";
    }
}
