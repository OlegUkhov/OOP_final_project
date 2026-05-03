// Observer side of the Observer pattern used by the Journal subscription system
// Implemented by User so any user can be subscribed to a Journal
public interface Observer {
    // Called by Journal.notifyObservers() when a new paper is published
    void update(ResearchPaper paper);
}
