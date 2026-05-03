// Исследовательский проект.
// Имеет тему, список участников (только Researcher) и список опубликованных статей.
// При попытке добавить не-исследователя бросается NotResearcherException.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResearchProject {

    // Уникальный идентификатор проекта
    private String projectId;
    // Тема исследовательского проекта
    private String topic;
    // Список участников-исследователей
    private List<Researcher> participants;
    // Список статей, опубликованных в рамках проекта
    private List<ResearchPaper> papers;

    // Конструктор — создаёт проект по теме, генерирует id
    public ResearchProject(String topic) {
        this.projectId = UUID.randomUUID().toString();
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    // Добавить участника-исследователя в проект
    public void addParticipant(Researcher researcher) throws NotResearcherException {
        if (researcher == null) {
            // Нельзя добавить null — это нарушение контракта исследователя
            throw new NotResearcherException("Participant cannot be null");
        }
        if (!participants.contains(researcher)) {
            participants.add(researcher);
        }
    }

    // Удалить участника из проекта
    public void removeParticipant(Researcher researcher) {
        if (researcher != null) {
            participants.remove(researcher);
        }
    }

    // Добавить статью в список публикаций проекта
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    // Получить список участников проекта
    public List<Researcher> getParticipants() {
        return new ArrayList<>(participants);
    }

    // Строковое представление проекта
    @Override
    public String toString() {
        return "ResearchProject{id='" + projectId + "', topic='" + topic
                + "', participants=" + participants.size()
                + ", papers=" + papers.size() + "}";
    }

    // Два проекта равны, если совпадают их projectId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        ResearchProject rp = (ResearchProject) o;
        return Objects.equals(projectId, rp.projectId);
    }

    // Хэш-код по projectId
    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
