// Single data store for the whole system (Singleton pattern)
// All collections live here; access through getInstance() only
// saveData() and loadData() are stubs for Part C serialization
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    private static DataStorage instance;

    private List<User> users;
    private List<Course> courses;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;
    private List<Journal> journals;
    private List<Log> logs;
    private List<News> news;
    // All Request objects submitted by students or employees; read by TechSupportSpecialist.viewNewRequests()
    private List<Request> requests;
    // All Message objects sent via Employee.sendMessage(); allows future inbox retrieval
    private List<Message> messages;

    private DataStorage() {
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.journals = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.news = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    // Double-check is not needed for a single-threaded demo but synchronized protects correctness
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public void addUser(User user) {
        if (user != null && !users.contains(user)) {
            users.add(user);
        }
    }

    public void removeUser(String userId) {
        if (userId != null) {
            users.removeIf(u -> u.getId().equals(userId));
        }
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public void addProject(ResearchProject project) {
        if (project != null && !projects.contains(project)) {
            projects.add(project);
        }
    }

    public void addJournal(Journal journal) {
        if (journal != null && !journals.contains(journal)) {
            journals.add(journal);
        }
    }

    public void addNews(News n) {
        if (n != null && !news.contains(n)) {
            news.add(n);
        }
    }

    public void addLog(Log log) {
        if (log != null && !logs.contains(log)) {
            logs.add(log);
        }
    }

    // Stores a Request in the central list so TechSupportSpecialist.viewNewRequests() can find it
    public void addRequest(Request request) {
        if (request != null && !requests.contains(request)) {
            requests.add(request);
        }
    }

    // Called by TechSupportSpecialist.viewNewRequests() to get the full request pool
    public List<Request> getRequests() {
        return new ArrayList<>(requests);
    }

    // Stores a Message so the conversation history is not lost after Employee.sendMessage() returns
    public void addMessage(Message message) {
        if (message != null && !messages.contains(message)) {
            messages.add(message);
        }
    }

    // Returns all messages; filter by receiver in the caller if an inbox view is needed
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    // Replaces an existing User entry matched by id; used by Admin.updateUser()
    public void updateUser(User user) {
        if (user == null) return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return;
            }
        }
    }

    public List<Log> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<News> getNews() {
        return new ArrayList<>(news);
    }

    // Scans users list: PROFESSOR teachers get TeacherResearcher wrapper
    // GraduateStudent instances get StudentResearcher wrapper
    // Returns fresh wrapper objects every call so h-index reflects current paper counts
    public List<Researcher> getAllResearchers() {
        List<Researcher> researchers = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Teacher) {
                Teacher t = (Teacher) u;
                if (t.getPosition() == TeacherPosition.PROFESSOR) {
                    researchers.add(new TeacherResearcher(t));
                }
            } else if (u instanceof GraduateStudent) {
                researchers.add(new StudentResearcher((Student) u));
            }
        }
        return researchers;
    }

    // Iterates all researchers and picks the one with the highest calculateHIndex() value
    // TeacherResearcher.calculateHIndex() delegates to ResearcherDecorator
    public Researcher getTopCitedResearcher() {
        List<Researcher> researchers = getAllResearchers();
        if (researchers.isEmpty()) return null;

        Researcher top = researchers.get(0);
        int maxH = top.calculateHIndex();

        for (Researcher r : researchers) {
            int h = r.calculateHIndex();
            if (h > maxH) {
                maxH = h;
                top = r;
            }
        }
        return top;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public List<Journal> getJournals() {
        return new ArrayList<>(journals);
    }

    // Part C replace with ObjectOutputStream to a file
    public void saveData() {
        System.out.println("[DataStorage] Saving... (" + users.size() + " users, "
                + courses.size() + " courses, " + papers.size() + " papers)");
    }

    // Part C replace with ObjectInputStream from a file
    public void loadData() {
        System.out.println("[DataStorage] Loading data...");
    }

    @Override
    public String toString() {
        return "DataStorage{users=" + users.size() + ", courses=" + courses.size()
                + ", papers=" + papers.size() + ", projects=" + projects.size()
                + ", journals=" + journals.size() + ", news=" + news.size()
                + ", requests=" + requests.size() + ", messages=" + messages.size() + "}";
    }
}
