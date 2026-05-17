// A research project with a topic list of Researcher participants and published papers
// Only Researcher instances can be added; null input throws NotResearcherException
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResearchProject {

    private String projectId;
    private String topic;
    // All entries here must implement Researcher; enforced by the addParticipant guard
    private List<Researcher> participants;
    private List<ResearchPaper> papers;

    public ResearchProject(String topic) {
        this.projectId = UUID.randomUUID().toString();
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    // Throws NotResearcherException for null to satisfy the spec requirement
    // ResearcherDecorator.leadProject() catches this exception internally
    public void addParticipant(Researcher researcher) throws NotResearcherException {
        if (researcher == null) {
            throw new NotResearcherException("Participant cannot be null");
        }
        if (!participants.contains(researcher)) {
            participants.add(researcher);
        }
    }

    public void removeParticipant(Researcher researcher) {
        if (researcher != null) {
            participants.remove(researcher);
        }
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public List<Researcher> getParticipants() {
        return new ArrayList<>(participants);
    }

    @Override
    public String toString() {
        return "ResearchProject{id='" + projectId + "', topic='" + topic
                + "', participants=" + participants.size()
                + ", papers=" + papers.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        ResearchProject rp = (ResearchProject) o;
        return Objects.equals(projectId, rp.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
