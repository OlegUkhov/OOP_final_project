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

    public void addParticipant(Researcher researcher) throws NotResearcherException {
        if (researcher == null) {
            throw new NotResearcherException("Participant must be a Researcher");
        }
        if (!participants.contains(researcher)) participants.add(researcher);
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) papers.add(paper);
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return "ResearchProject{topic='" + topic + "', participants=" + participants.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        return Objects.equals(projectId, ((ResearchProject) o).projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
