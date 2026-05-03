import java.util.ArrayList;
import java.util.Comparator;
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
    
    // Приватный конструктор для Singleton
    private DataStorage() {
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.journals = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.news = new ArrayList<>();
    }
    
    // Получить единственный экземпляр DataStorage
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }
    
    // Добавить пользователя
    public void addUser(User user) {
        if (user != null && !users.contains(user)) {
            users.add(user);
        }
    }
    
    // Удалить пользователя
    public void removeUser(String userId) {
        if (userId != null) {
            users.removeIf(u -> u.getId().equals(userId));
        }
    }
    
    // Найти пользователя по ID
    public User findUserById(String userId) {
        if (userId != null) {
            for (User u : users) {
                if (u.getId().equals(userId)) {
                    return u;
                }
            }
        }
        return null;
    }
    
    // Добавить курс
    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }
    
    // Добавить статью
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }
    
    // Добавить проект
    public void addProject(ResearchProject project) {
        if (project != null && !projects.contains(project)) {
            projects.add(project);
        }
    }
    
    // Добавить журнал
    public void addJournal(Journal journal) {
        if (journal != null && !journals.contains(journal)) {
            journals.add(journal);
        }
    }
    
    // Добавить новость
    public void addNews(News n) {
        if (n != null && !news.contains(n)) {
            news.add(n);
        }
    }
    
    // Добавить лог
    public void addLog(Log log) {
        if (log != null && !logs.contains(log)) {
            logs.add(log);
        }
    }
    
    // Получить всех исследователей
    public List<Researcher> getAllResearchers() {
        List<Researcher> researchers = new ArrayList<>();
        
        // Исследователи среди преподавателей (профессора)
        for (User u : users) {
            if (u instanceof Teacher) {
                Teacher t = (Teacher) u;
                if (t.getPosition() == TeacherPosition.PROFESSOR) {
                    researchers.add(new TeacherResearcher(t));
                }
            }
            // Исследователи среди выпускников
            else if (u instanceof GraduateStudent) {
                researchers.add(new StudentResearcher((Student) u));
            }
        }
        
        return researchers;
    }
    
    // Получить исследователя с наибольшим h-index
    public Researcher getTopCitedResearcher() {
        List<Researcher> researchers = getAllResearchers();
        if (researchers.isEmpty()) {
            return null;
        }
        
        Researcher top = researchers.get(0);
        int maxHIndex = top.calculateHIndex();
        
        for (Researcher r : researchers) {
            int hIndex = r.calculateHIndex();
            if (hIndex > maxHIndex) {
                maxHIndex = hIndex;
                top = r;
            }
        }
        
        return top;
    }
    
    // Geters
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
    
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }
    
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }
    
    public List<ResearchProject> getProjects() {
        return new ArrayList<>(projects);
    }
    
    public List<Journal> getJournals() {
        return new ArrayList<>(journals);
    }
    
    public List<Log> getLogs() {
        return new ArrayList<>(logs);
    }
    
    public List<News> getNews() {
        return new ArrayList<>(news);
    }
    
    // Сохранить данные (упрощённо)
    public void saveData() {
        System.out.println("[DataStorage] Saving data... (" + users.size() + " users, " +
                courses.size() + " courses, " + papers.size() + " papers)");
    }
    
    // Загрузить данные (упрощённо)
    public void loadData() {
        System.out.println("[DataStorage] Loading data...");
    }
    
    @Override
    public String toString() {
        return "DataStorage{" +
                "users=" + users.size() +
                ", courses=" + courses.size() +
                ", papers=" + papers.size() +
                ", projects=" + projects.size() +
                ", journals=" + journals.size() +
                ", news=" + news.size() +
                '}';
    }
}
