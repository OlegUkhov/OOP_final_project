// Observable side of the Observer pattern
// Implemented by Journal which holds a list of Observer subscribers
public interface Observable {
    // Adds a subscriber to the journal notification list
    void subscribe(Observer o);
    // Removes a subscriber from the journal notification list
    void unsubscribe(Observer o);
    // Calls update() on every subscriber when a new paper is published
    void notifyObservers(ResearchPaper paper);
}
