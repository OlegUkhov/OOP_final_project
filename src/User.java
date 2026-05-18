import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Observer, Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
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

    public static User authenticate(String email, String password) {
        User user = DataStorage.getInstance().findUserByEmail(email);
        if (user != null && user.checkPassword(password)) return user;
        return null;
    }

    public boolean checkPassword(String pwd) {
        return password != null && password.equals(pwd);
    }

    public void logout() {
        System.out.println("[AUTH] " + firstName + " logged out.");
    }

    @Override
    public void update(ResearchPaper paper) {
        if (paper != null) {
            System.out.println("[JOURNAL] " + firstName + " — new paper: " + paper.getTitle());
        }
    }

    public void onNewsReceived(News item) {
        if (item != null) {
            System.out.println("[NEWS] " + firstName + " received: " + item.getTitle()
                    + (item.isPinned() ? " [PINNED]" : ""));
        }
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Language getLanguage() { return language; }

    /** Simple UI translation: EN / KZ / RU (Lecture: enum + control flow). */
    public String t(String en, String kz, String ru) {
        if (language == Language.KZ) return kz;
        if (language == Language.RU) return ru;
        return en;
    }

    public void setLanguage(Language lang) {
        if (lang != null) {
            this.language = lang;
            System.out.println(t("Language switched to: ", "Тіл өзгертілді: ", "Язык изменён: ") + lang);
        }
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " [" + email + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(id, ((User) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
