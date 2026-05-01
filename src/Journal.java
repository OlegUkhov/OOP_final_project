import java.util.ArrayList;
import java.util.List;

public class Journal implements Observable {
    private String name;
    private List<ResearchPaper> papers;
    private List<Observer> subscribers;
    
    public Journal(String name) {
        this.name = name;
        this.papers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }
    
    public void publishPaper(ResearchPaper p) {
        papers.add(p);
        notifyObservers(p);
    }
    
    @Override
    public void subscribe(Observer o) {
        if (!subscribers.contains(o)) {
            subscribers.add(o);
        }
    }
    
    @Override
    public void unsubscribe(Observer o) {
        subscribers.remove(o);
    }
    
    @Override
    public void notifyObservers(ResearchPaper paper) {
        for (Observer observer : subscribers) {
            observer.update(paper);
        }
    }
    
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Journal{" + "name='" + name + '\'' + ", papers=" + papers.size() + '}';
    }
}
