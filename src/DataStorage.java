// Единственное хранилище данных системы (паттерн Singleton).
// Содержит все сущности: пользователей, курсы, статьи, проекты, журналы, логи, новости.
// Доступ только через getInstance() — прямое создание запрещено.
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    // Единственный экземпляр класса (Singleton)
    private static DataStorage instance;

    // Список всех пользователей системы
    private List<User> users;
    // Список всех курсов
    private List<Course> courses;
    // Список всех научных статей
    private List<ResearchPaper> papers;
    // Список всех исследовательских проектов
    private List<ResearchProject> projects;
    // Список всех журналов
    private List<Journal> journals;
    // Список системных логов
    private List<Log> logs;
    // Список новостей
    private List<News> news;

    // Приватный конструктор — запрещает создание экземпляра извне
    private DataStorage() {
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.journals = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.news = new ArrayList<>();
    }

    // Получить единственный экземпляр DataStorage (потокобезопасно)
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            // Создаём экземпляр только при первом обращении
            instance = new DataStorage();
        }
        return instance;
    }

    // Добавить пользователя в систему
    public void addUser(User user) {
        if (user != null && !users.contains(user)) {
            users.add(user);
        }
    }

    // Удалить пользователя из системы по его id
    public void removeUser(String userId) {
        if (userId != null) {
            users.removeIf(u -> u.getId().equals(userId));
        }
    }

    // Добавить курс в систему
    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    // Добавить научную статью в систему
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    // Добавить исследовательский проект
    public void addProject(ResearchProject project) {
        if (project != null && !projects.contains(project)) {
            projects.add(project);
        }
    }

    // Добавить журнал в систему
    public void addJournal(Journal journal) {
        if (journal != null && !journals.contains(journal)) {
            journals.add(journal);
        }
    }

    // Добавить новость в систему
    public void addNews(News n) {
        if (n != null && !news.contains(n)) {
            news.add(n);
        }
    }

    // Добавить запись лога
    public void addLog(Log log) {
        if (log != null && !logs.contains(log)) {
            logs.add(log);
        }
    }

    // Получить всех исследователей системы (профессора + выпускники)
    public List<Researcher> getAllResearchers() {
        List<Researcher> researchers = new ArrayList<>();
        for (User u : users) {
            // Профессор автоматически считается исследователем
            if (u instanceof Teacher) {
                Teacher t = (Teacher) u;
                if (t.getPosition() == TeacherPosition.PROFESSOR) {
                    researchers.add(new TeacherResearcher(t));
                }
            }
            // Выпускник (магистр/PhD) автоматически считается исследователем
            else if (u instanceof GraduateStudent) {
                researchers.add(new StudentResearcher((Student) u));
            }
        }
        return researchers;
    }

    // Найти исследователя с наибольшим h-index (топ-цитируемый)
    public Researcher getTopCitedResearcher() {
        List<Researcher> researchers = getAllResearchers();
        if (researchers.isEmpty()) return null;

        Researcher top = researchers.get(0);
        int maxH = top.calculateHIndex();

        // Перебираем всех исследователей, ищем максимальный h-index
        for (Researcher r : researchers) {
            int h = r.calculateHIndex();
            if (h > maxH) {
                maxH = h;
                top = r;
            }
        }
        return top;
    }

    // Получить список всех пользователей
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // Получить список всех курсов
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    // Получить список всех журналов
    public List<Journal> getJournals() {
        return new ArrayList<>(journals);
    }

    // Сохранить данные (заглушка — в учебной версии вывод в консоль)
    public void saveData() {
        System.out.println("[DataStorage] Saving... (" + users.size() + " users, "
                + courses.size() + " courses, " + papers.size() + " papers)");
    }

    // Загрузить данные (заглушка)
    public void loadData() {
        System.out.println("[DataStorage] Loading data...");
    }

    // Строковое представление хранилища
    @Override
    public String toString() {
        return "DataStorage{users=" + users.size() + ", courses=" + courses.size()
                + ", papers=" + papers.size() + ", projects=" + projects.size()
                + ", journals=" + journals.size() + ", news=" + news.size() + "}";
    }
}
