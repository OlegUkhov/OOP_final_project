// University research journal implementing the Observable interface (Observer pattern)
// Any User can subscribe; when publishPaper() is called all subscribers receive an update()
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Journal implements Observable {

    private String journalId;
    private String name;
    private List<ResearchPaper> papers;
    // All entries here implement Observer; User already implements Observer
    private List<Observer> subscribers;

    public Journal(String name) {
        this.journalId = UUID.randomUUID().toString();
        this.name = name;
        this.papers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    // Adding the paper triggers notifyObservers() which calls update() on every subscriber
    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            notifyObservers(p);
        }
    }

    @Override
    public void subscribe(Observer o) {
        if (o != null && !subscribers.contains(o)) {
            subscribers.add(o);
        }
    }

    @Override
    public void unsubscribe(Observer o) {
        if (o != null) {
            subscribers.remove(o);
        }
    }

    // Iterates subscribers and calls User.update(paper) on each one
    @Override
    public void notifyObservers(ResearchPaper paper) {
        for (Observer obs : subscribers) {
            obs.update(paper);
        }
    }

    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    // Used in Main to display journal name when subscribing
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Journal{name='" + name + "', papers=" + papers.size()
                + ", subscribers=" + subscribers.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        Journal j = (Journal) o;
        return Objects.equals(journalId, j.journalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalId);
    }
}
