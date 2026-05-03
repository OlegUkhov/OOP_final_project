// Abstract base for every person in the system
// Implements Observer so any user can subscribe to a Journal and receive paper notifications
import java.util.Objects;

public abstract class User implements Observer {

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    // Controls which language the UI shows for this user (KZ / EN / RU)
    protected Language language;

    public User(String id, String firstName, String lastName,
                String email, String password, Language language) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.language = language;
    }

    // Entry point for the system; a real version would check credentials against DataStorage
    public boolean login() {
        return true;
    }

    public void logout() {
    }

    // Called by Journal.notifyObservers() when a new paper is published in a subscribed journal
    @Override
    public void update(ResearchPaper paper) {
        if (paper != null) {
            System.out.println("[NOTIFICATION] " + firstName + " " + lastName
                    + " notified: new paper - " + paper.getTitle());
        }
    }

    // Used by DataStorage.removeUser() and Admin.removeUser() to match by id
    public String getId() {
        return id;
    }

    // Used by toString() in Message and Complaint to show the person name
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " [id=" + id + ", email=" + email + "]";
    }

    // Equality is id-based so the same person is never stored twice in a collection
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
