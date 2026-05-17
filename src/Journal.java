import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Journal implements Observable {

    private String journalId;
    private String name;
    private List<ResearchPaper> papers;
    private List<Observer> subscribers;

    public Journal(String name) {
        this.journalId = UUID.randomUUID().toString();
        this.name = name;
        this.papers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            DataStorage.getInstance().addPaper(p);
            notifyObservers(p);
        }
    }

    @Override
    public void subscribe(Observer o) {
        if (o != null && !subscribers.contains(o)) subscribers.add(o);
    }

    @Override
    public void unsubscribe(Observer o) {
        if (o != null) subscribers.remove(o);
    }

    @Override
    public void notifyObservers(ResearchPaper paper) {
        for (Observer obs : subscribers) obs.update(paper);
    }

    public List<Observer> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return "Journal{name='" + name + "', subscribers=" + subscribers.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        return Objects.equals(journalId, ((Journal) o).journalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalId);
    }
}
