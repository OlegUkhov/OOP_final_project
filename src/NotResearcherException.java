// Thrown by ResearchProject.addParticipant() when a null is passed instead of a Researcher
public class NotResearcherException extends RuntimeException {
    public NotResearcherException(String message) {
        super(message);
    }
}
