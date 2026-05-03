import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResearchProject {
    private String projectId;
    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> papers;

    public ResearchProject(String topic) {
        this.projectId = UUID.randomUUID().toString();
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    // Добавить участника проекта (должен быть Researcher)
    public void addParticipant(Researcher researcher) throws NotResearcherException {
        if (researcher == null) {
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

    // Добавить опубликованную статью в проект
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTopic() {
        return topic;
    }

    public List<Researcher> getParticipants() {
        return new ArrayList<>(participants);
    }

    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    @Override
    public String toString() {
        return "ResearchProject{" +
                "projectId='" + projectId + '\'' +
                ", topic='" + topic + '\'' +
                ", participants=" + participants.size() +
                ", papers=" + papers.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        ResearchProject project = (ResearchProject) o;
        return Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
