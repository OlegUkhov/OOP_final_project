import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Console application — all use cases from Use Case Diagram.
 * Uses every domain class: patterns Singleton, Decorator, Observer; Comparators; exceptions.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static DataStorage storage;

    private static final int PAGE_SIZE = 3;  // items per page for paginated lists

    public static void main(String[] args) {
        printHeader();
        storage = DataStorage.getInstance();
        storage.loadData();
        if (storage.getUsers().isEmpty()) {
            out("No saved data. Loading sample data...");
            initSampleData();
        }

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = loginScreen();
            } else {
                running = roleMenu();
            }
        }
        storage.saveData();
        out("\nData saved. Goodbye!");
        scanner.close();
    }

    // ===================== AUTH =====================

    private static boolean loginScreen() {
        User u = currentUser;
        out("\n--- " + txt(u, "Login", "Кіру", "Вход") + " ---");
        out("1. " + txt(u, "Login", "Кіру", "Войти"));
        out("2. " + txt(u, "Exit", "Шығу", "Выход"));
        pr(txt(u, "Choice: ", "Таңдау: ", "Выбор: "));
        String c = scanner.nextLine().trim();
        if ("2".equals(c)) return false;
        if ("1".equals(c)) {
            pr("Email: ");
            String email = scanner.nextLine().trim();
            pr(txt(u, "Password: ", "Құпия сөз: ", "Пароль: "));
            String pass = scanner.nextLine().trim();
            User user = User.authenticate(email, pass);
            if (user != null) {
                currentUser = user;
                out("\n" + user.t("Welcome, ", "Қош келдіңіз, ", "Добро пожаловать, ")
                        + user.getFirstName() + " " + user.getLastName()
                        + " [" + roleName(user) + "]");
            } else {
                out(txt(u, "Invalid credentials.", "Қате деректер.", "Неверные данные."));
            }
        }
        return true;
    }

    private static boolean roleMenu() {
        out("\n========================================");
        out("  " + currentUser.getFirstName() + " " + currentUser.getLastName()
                + "  |  " + roleName(currentUser) + "  |  " + currentUser.getLanguage());
        out("========================================");

        if (currentUser instanceof Admin)                return adminMenu((Admin) currentUser);
        if (currentUser instanceof Manager)              return managerMenu((Manager) currentUser);
        if (currentUser instanceof TechSupportSpecialist) return techMenu((TechSupportSpecialist) currentUser);
        if (currentUser instanceof Teacher)              return teacherMenu((Teacher) currentUser);
        if (currentUser instanceof GraduateStudent)      return graduateMenu((GraduateStudent) currentUser);
        if (currentUser instanceof Student)              return studentMenu((Student) currentUser);
        return handleCommon("0", currentUser);
    }

    // ===================== ADMIN =====================

    private static boolean adminMenu(Admin a) {
        printPanel(a, "Admin Panel", "Әкімші панелі", "Панель админа");
        out("1.  " + a.t("View all users", "Барлық пайдаланушылар", "Все пользователи"));
        out("2.  " + a.t("Add user", "Пайдаланушы қосу", "Добавить пользователя"));
        out("3.  " + a.t("Update user", "Пайдаланушыны өзгерту", "Обновить пользователя"));
        out("4.  " + a.t("Remove user", "Жою", "Удалить пользователя"));
        out("5.  " + a.t("View log files", "Логтар", "Логи"));
        printCommonMenu(a);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, a)) return !"0".equals(c);

        switch (c) {
            case "1":
                showPaged(storage.getUsers(), a.t("users", "пайдаланушы", "пользователей"));
                break;
            case "2":
                adminAddUser(a);
                break;
            case "3":
                adminUpdateUser(a);
                break;
            case "4":
                pr(a.t("User id: ", "ID: ", "ID: "));
                String uid = scanner.nextLine().trim();
                if (confirm(a.t("Remove user " + uid + "?", "Жою?", "Удалить?"))) {
                    a.removeUser(uid);
                    out(a.t("Removed.", "Жойылды.", "Удалено."));
                } else {
                    out(a.t("Cancelled.", "Болдырмады.", "Отменено."));
                }
                break;
            case "5":
                viewLogs(a);
                break;
            default:
                out(a.t("Unknown option.", "Белгісіз.", "Неизвестно."));
        }
        return true;
    }

    // ===================== MANAGER =====================

    private static boolean managerMenu(Manager m) {
        printPanel(m, "Manager Panel", "Менеджер панелі", "Панель менеджера");
        out("1.  " + m.t("Assign course to teacher", "Курс тағайындау", "Назначить курс"));
        out("2.  " + m.t("Approve student registration", "Тіркеуді бекіту", "Одобрить регистрацию"));
        out("3.  " + m.t("Add course for registration (major/year)", "Курс қосу", "Добавить курс"));
        out("4.  " + m.t("Statistical report", "Есеп", "Статистический отчёт"));
        out("5.  " + m.t("Publish news", "Жаңалық жариялау", "Опубликовать новость"));
        out("6.  " + m.t("Top cited researcher news", "Топ цитирований", "Новость о топ исследователе"));
        out("7.  " + m.t("Students sorted by GPA", "Студенттер GPA", "Студенты по GPA"));
        out("8.  " + m.t("Students sorted by name", "Студенттер аты", "Студенты по имени"));
        out("9.  " + m.t("Employee requests", "Қызметкер өтінімдері", "Заявки сотрудников"));
        out("10. " + m.t("Print all university papers", "Барлық мақалалар", "Все статьи вуза"));
        out("11. " + m.t("Submit employee request", "Өтініш жіберу", "Отправить заявку"));
        printCommonMenu(m);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, m)) return !"0".equals(c);

        try {
            switch (c) {
                case "1":  assignCourseMenu(m); break;
                case "2":  approveRegistrationMenu(m); break;
                case "3":  addCourseForRegMenu(m); break;
                case "4":  out(m.createStatisticalReport()); break;
                case "5":  publishNewsMenu(m); break;
                case "6":  m.generateTopCitedResearcherNews(); break;
                case "7":  studentsGpaMenu(m); break;
                case "8":  showPaged(m.getStudentsSortedByName(), m.t("students", "студент", "студентов")); break;
                case "9":
                    List<Request> er = m.viewEmployeeRequests();
                    showPaged(er, m.t("requests", "өтінімдер", "заявок"));
                    break;
                case "10": printPapersMenu(null); break;
                case "11": m.submitRequest(readLine(m.t("Request: ", "Өтініш: ", "Заявка: "))); break;
                default:   out(m.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseOverloadException | CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        return true;
    }

    /** Manager → Students by GPA with filters */
    private static void studentsGpaMenu(Manager m) {
        List<Student> all = m.getStudentsSortedByGpa();
        if (all.isEmpty()) { out(m.t("No students.", "Студент жоқ.", "Нет студентов.")); return; }

        out("\n" + m.t("Filter by status:", "Күйі:", "Фильтр по статусу:"));
        out("1. " + m.t("Graduated", "Бітіргендер", "Выпускники"));
        out("2. " + m.t("Currently studying", "Оқып жатқандар", "Сейчас учатся"));
        out("3. " + m.t("All", "Барлығы", "Все"));
        pr(m.t("Choice: ", "Таңдау: ", "Выбор: "));
        String statusChoice = scanner.nextLine().trim();

        List<Student> byStatus = new ArrayList<>();
        if ("1".equals(statusChoice)) {
            for (Student s : all) if (s.isGraduated()) byStatus.add(s);
        } else if ("2".equals(statusChoice)) {
            for (Student s : all) if (!s.isGraduated()) byStatus.add(s);
        } else {
            byStatus.addAll(all);
        }

        // Year filter (only for currently studying or all)
        List<Student> byYear = byStatus;
        if (!"1".equals(statusChoice)) {
            out("\n" + m.t("Filter by year:", "Курс:", "Фильтр по году:"));
            out("1. " + m.t("1st year", "1-курс", "1 курс"));
            out("2. " + m.t("2nd year", "2-курс", "2 курс"));
            out("3. " + m.t("3rd year", "3-курс", "3 курс"));
            out("4. " + m.t("4th year", "4-курс", "4 курс"));
            out("5. " + m.t("5th–7th year (Master/PhD)", "5-7 курс", "5–7 курс (магистр/PhD)"));
            out("6. " + m.t("All years", "Барлық курстар", "Все курсы"));
            pr(m.t("Choice: ", "Таңдау: ", "Выбор: "));
            String yearChoice = scanner.nextLine().trim();
            if (!"6".equals(yearChoice)) {
                byYear = new ArrayList<>();
                for (Student s : byStatus) {
                    int y = s.getStudyYear();
                    boolean match =
                            ("1".equals(yearChoice) && y == 1) ||
                                    ("2".equals(yearChoice) && y == 2) ||
                                    ("3".equals(yearChoice) && y == 3) ||
                                    ("4".equals(yearChoice) && y == 4) ||
                                    ("5".equals(yearChoice) && y >= 5);
                    if (match) byYear.add(s);
                }
            }
        }

        // Faculty filter
        List<String> faculties = getDistinctFaculties(byYear);
        List<Student> result = byYear;
        if (!faculties.isEmpty()) {
            out("\n" + m.t("Filter by faculty:", "Факультет:", "Фильтр по факультету:"));
            for (int i = 0; i < faculties.size(); i++) out((i + 1) + ". " + faculties.get(i));
            out((faculties.size() + 1) + ". " + m.t("All faculties", "Барлық факультеттер", "Все факультеты"));
            pr(m.t("Choice: ", "Таңдау: ", "Выбор: "));
            int fi = readInt("") - 1;
            if (fi >= 0 && fi < faculties.size()) {
                String fac = faculties.get(fi);
                result = new ArrayList<>();
                for (Student s : byYear) if (fac.equals(s.getFaculty())) result.add(s);
            }
        }

        if (result.isEmpty()) {
            out(m.t("No students match the filter.", "Студент табылмады.", "Студентов не найдено."));
        } else {
            out("\n" + m.t("Found: ", "Табылды: ", "Найдено: ") + result.size());
            showPaged(result, m.t("students", "студент", "студентов"));
        }
    }

    private static List<String> getDistinctFaculties(List<Student> students) {
        List<String> result = new ArrayList<>();
        for (Student s : students) {
            String f = s.getFaculty();
            if (f != null && !f.isEmpty() && !result.contains(f)) result.add(f);
        }
        return result;
    }

    // ===================== TECH SUPPORT =====================

    private static boolean techMenu(TechSupportSpecialist ts) {
        printPanel(ts, "Tech Support", "Тех. қолдау", "Техподдержка");
        out("1.  " + ts.t("View new requests (VIEWED)", "Жаңа өтінімдер", "Новые заявки"));
        out("2.  " + ts.t("Accept request", "Қабылдау", "Принять"));
        out("3.  " + ts.t("Reject request", "Қабылдамау", "Отклонить"));
        out("4.  " + ts.t("Mark as DONE", "Орындалды", "Выполнено"));
        printCommonMenu(ts);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, ts)) return !"0".equals(c);

        switch (c) {
            case "1":
                showPaged(ts.viewNewRequests(), ts.t("requests", "өтінімдер", "заявок"));
                break;
            case "2": processRequest(ts, true, false, false); break;
            case "3": processRequest(ts, false, true, false); break;
            case "4": processRequest(ts, false, false, true); break;
            default: out(ts.t("Unknown.", "Белгісіз.", "Неизвестно."));
        }
        return true;
    }

    // ===================== TEACHER =====================

    private static boolean teacherMenu(Teacher t) {
        printPanel(t, "Teacher Panel", "Оқытушы панелі", "Панель преподавателя");
        out("1.  " + t.t("View courses", "Курстар", "Курсы"));
        out("2.  " + t.t("Manage course (add lesson)", "Сабақ қосу", "Управление курсом"));
        out("3.  " + t.t("Put mark (1st+2nd+final)", "Баға қою", "Выставить оценку"));
        out("4.  " + t.t("View students info", "Студенттер", "Инфо о студентах"));
        out("5.  " + t.t("Send complaint to dean", "Шағым", "Жалоба декану"));
        out("6.  " + t.t("Send message to employee", "Хабарлама", "Сообщение"));
        if (t.isResearcher()) printResearcherMenu(t);
        else out("--- " + t.t("(Researcher: option 20)", "(Зертгер: 20)", "(Исследователь: 20)"));
        out("20. " + t.t("Enable researcher role", "Зертгер рөлі", "Роль исследователя"));
        printCommonMenu(t);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, t)) return !"0".equals(c);

        try {
            switch (c) {
                case "1":  showPaged(t.viewCourses(), t.t("courses", "курстар", "курсов")); break;
                case "2":  manageCourseMenu(t); break;
                case "3":  putMarkMenu(t); break;
                case "4":  t.viewStudentsInfo(); break;
                case "5":  sendComplaintMenu(t); break;
                case "6":  sendMessageMenu(t); break;
                case "7":  if (t.isResearcher()) publishResearchMenu(t); else needResearcher(t); break;
                case "8":  if (t.isResearcher()) out("h-index: " + researcherOf(t).calculateHIndex()); else needResearcher(t); break;
                case "9":  if (t.isResearcher()) printPapersMenu(researcherOf(t)); else needResearcher(t); break;
                case "10": if (t.isResearcher()) citationMenu(researcherOf(t)); else needResearcher(t); break;
                case "11": if (t.isResearcher()) leadProjectMenu(researcherOf(t)); else needResearcher(t); break;
                case "12": if (t.isResearcher()) addParticipantMenu(researcherOf(t)); else needResearcher(t); break;
                case "20": t.setResearcher(true); out(t.t("Researcher enabled.", "Қосылды.", "Включено.")); break;
                default:   out(t.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        return true;
    }

    // ===================== STUDENT =====================

    private static boolean studentMenu(Student s) {
        printPanel(s, "Student Panel", "Студент панелі", "Панель студента");
        out("1.  " + s.t("My courses", "Менің курстарым", "Мои курсы"));
        out("2.  " + s.t("Available courses", "Қолжетімді курстар", "Доступные курсы"));
        out("3.  " + s.t("Register for course (max 21 cr, 3 fails)", "Тіркелу", "Регистрация"));
        out("4.  " + s.t("Teachers of course", "Оқытушылар", "Преподаватели курса"));
        out("5.  " + s.t("View marks", "Бағалар", "Оценки"));
        out("6.  " + s.t("View transcript", "Транскрипт", "Транскрипт"));
        out("7.  " + s.t("Rate teacher", "Бағалау", "Оценить преподавателя"));
        out("8.  " + s.t("Join organization", "Ұйымға қосылу", "Вступить в организацию"));
        out("9.  " + s.t("Become head of organization", "Төраға", "Глава организации"));
        out("10. " + s.t("Submit tech request", "Тех. өтініш", "Тех. заявка"));
        printCommonMenu(s);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, s)) return !"0".equals(c);

        try {
            switch (c) {
                case "1":
                    showPaged(s.viewCourses(), s.t("courses", "курстар", "курсов"));
                    break;
                case "2":
                    showPaged(storage.getCourses(), s.t("courses", "курстар", "курсов"));
                    break;
                case "3":  registerCourseMenu(s); break;
                case "4":  viewTeachersMenu(s); break;
                case "5":
                    showPaged(s.viewMarks(), s.t("marks", "бағалар", "оценок"));
                    break;
                case "6":  out(s.getTranscript()); break;
                case "7":  rateTeacherMenu(s); break;
                case "8":  joinOrgMenu(s); break;
                case "9":  setOrgHeadMenu(s); break;
                case "10": s.submitRequest(readLine(s.t("Problem: ", "Мәселе: ", "Проблема: "))); break;
                default:   out(s.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseOverloadException | CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        return true;
    }

    // ===================== GRADUATE STUDENT =====================

    private static boolean graduateMenu(GraduateStudent gs) {
        printPanel(gs, "Graduate Student", "Магистрант", "Магистрант");
        out("--- " + gs.t("Student", "Студент", "Студент") + " ---");
        out("1-10 " + gs.t("(same as student menu)", "(студент сияқты)", "(как у студента)"));
        out("--- " + gs.t("Graduate", "Магистрант", "Магистрант") + " ---");
        out("11. " + gs.t("Assign supervisor (h>=3)", "Жетекші", "Научрук"));
        out("12. " + gs.t("Publish diploma paper", "Диплом", "Дипломная работа"));
        out("--- " + gs.t("Researcher", "Зертгер", "Исследователь") + " ---");
        out("13. " + gs.t("Publish research paper", "Мақала", "Публикация"));
        out("14. " + gs.t("Calculate h-index", "h-индекс", "h-индекс"));
        out("15. " + gs.t("Print papers sorted", "Мақалалар", "Статьи"));
        out("16. " + gs.t("Get citation", "Цитата", "Цитирование"));
        out("17. " + gs.t("Lead research project", "Жоба", "Проект"));
        out("18. " + gs.t("Add participant", "Қатысушы", "Участник"));
        printCommonMenu(gs);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, gs)) return !"0".equals(c);

        try {
            if (c.length() == 1 && c.charAt(0) >= '1' && c.charAt(0) <= '9') {
                studentAction(gs, c);
                return true;
            }
            if ("10".equals(c)) { studentAction(gs, "10"); return true; }
            switch (c) {
                case "11": assignSupervisorMenu(gs); break;
                case "12": publishDiplomaMenu(gs); break;
                case "13": publishResearchMenu(gs); break;
                case "14": out("h-index: " + researcherOf(gs).calculateHIndex()); break;
                case "15": printPapersMenu(researcherOf(gs)); break;
                case "16": citationMenu(researcherOf(gs)); break;
                case "17": leadProjectMenu(researcherOf(gs)); break;
                case "18": addParticipantMenu(researcherOf(gs)); break;
                default:   out(gs.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (LowHIndexException | CourseOverloadException | CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        return true;
    }

    private static void studentAction(Student s, String c) throws CourseOverloadException, CourseFailLimitException {
        switch (c) {
            case "1":  showPaged(s.viewCourses(), "courses"); break;
            case "2":  showPaged(storage.getCourses(), "courses"); break;
            case "3":  registerCourseMenu(s); break;
            case "4":  viewTeachersMenu(s); break;
            case "5":  showPaged(s.viewMarks(), "marks"); break;
            case "6":  out(s.getTranscript()); break;
            case "7":  rateTeacherMenu(s); break;
            case "8":  joinOrgMenu(s); break;
            case "9":  setOrgHeadMenu(s); break;
            case "10": s.submitRequest(readLine("Problem: ")); break;
        }
    }

    // ===================== COMMON =====================

    private static void printCommonMenu(User u) {
        out("--- " + u.t("All users", "Барлығы", "Все") + " ---");
        out("90. " + u.t("Switch language KZ/EN/RU", "Тіл", "Язык"));
        out("91. " + u.t("Subscribe to journal", "Жазылу", "Подписка на журнал"));
        out("92. " + u.t("Unsubscribe from journal", "Жазылудан бас тарту", "Отписка"));
        out("93. " + u.t("View news", "Жаңалықтар", "Новости"));
        out("94. " + u.t("Publish paper to journal (notify)", "Журналға мақала", "Статья в журнал"));
        out("95. " + u.t("Logout", "Шығу", "Выйти"));
        out("0.  " + u.t("Save & Exit", "Сақтау", "Сохранить и выйти"));
    }

    private static boolean handleCommon(String c, User u) {
        switch (c) {
            case "90": switchLanguage(); return true;
            case "91": subscribeJournal(); return true;
            case "92": unsubscribeJournal(); return true;
            case "93":
                if (u instanceof Student)   viewNewsPaged((Student) u);
                else if (u instanceof Employee) viewNewsPaged((Employee) u);
                return true;
            case "94": publishToJournalMenu(); return true;
            case "95": u.logout(); currentUser = null; return true;
            case "0":  return true;
            default:   return false;
        }
    }

    // ===================== RESEARCHER =====================

    private static void printResearcherMenu(User u) {
        out("7.  " + u.t("Publish research paper", "Мақала жариялау", "Публикация"));
        out("8.  " + u.t("Calculate h-index", "h-индекс", "h-индекс"));
        out("9.  " + u.t("Print papers sorted", "Мақалалар", "Статьи сортировка"));
        out("10. " + u.t("Get citation Plain/BibTeX", "Цитата", "Цитирование"));
        out("11. " + u.t("Lead research project", "Жоба", "Проект"));
        out("12. " + u.t("Add participant to project", "Қатысушы", "Участник"));
    }

    private static Researcher researcherOf(Teacher t) { return new TeacherResearcher(t); }
    private static Researcher researcherOf(Student s) { return new StudentResearcher(s); }
    private static void needResearcher(Teacher t) {
        out(t.t("Enable researcher (option 20).", "20 қосыңыз.", "Включите опцию 20."));
    }

    // ===================== ACTION MENUS =====================

    private static void registerCourseMenu(Student s) {
        Course c = pickCourse(storage.getCourses(), s.t("course", "курс", "курс"));
        if (c != null) {
            s.registerForCourse(c);
            out(s.t("Registered.", "Тіркелді.", "Зарегистрирован."));
        }
    }

    private static void viewTeachersMenu(Student s) {
        Course c = pickCourse(s.viewCourses(), s.t("your course", "курс", "курс"));
        if (c == null) return;
        List<Teacher> teachers = s.viewTeachersForCourse(c);
        if (teachers.isEmpty()) { out(s.t("No teachers assigned.", "Оқытушы жоқ.", "Преподавателей нет.")); return; }
        for (Teacher t : teachers) {
            out("  " + t + " rating=" + String.format("%.2f", t.getRating()));
        }
    }

    private static void rateTeacherMenu(Student s) {
        Course c = pickCourse(s.viewCourses(), "course");
        if (c == null || c.getTeachers().isEmpty()) return;
        Teacher t = pickTeacher(c.getTeachers());
        if (t == null) return;
        int r = readInt(s.t("Rating 1-5: ", "1-5: ", "1-5: "));
        if (r < 1 || r > 5) { out(s.t("Invalid rating.", "Қате баға.", "Неверная оценка.")); return; }
        s.rateTeacher(t, r);
        out(s.t("Rated.", "Бағаланды.", "Оценено."));
    }

    private static void joinOrgMenu(Student s) {
        pr(s.t("Organization name: ", "Атауы: ", "Название: "));
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { out(s.t("Name cannot be empty.", "Атауы бос.", "Название пусто.")); return; }
        StudentOrganization org = new StudentOrganization(name);
        s.joinOrganization(org);
        out(s.t("Joined.", "Қосылды.", "Вступили."));
    }

    private static void setOrgHeadMenu(Student s) {
        for (StudentOrganization o : storage.getOrganizations()) {
            if (o.getMembers().contains(s)) {
                o.setHead(s);
                out(s.t("You are head of ", "Төраға: ", "Глава: ") + o.getName());
                return;
            }
        }
        out(s.t("Join organization first.", "Алдымен қосылыңыз.", "Сначала вступите."));
    }

    private static void assignSupervisorMenu(GraduateStudent gs) throws LowHIndexException {
        List<Researcher> list = storage.getAllResearchers();
        if (list.isEmpty()) { out("(no researchers)"); return; }
        Researcher r = pickResearcher(list);
        if (r != null) {
            gs.setSupervisor(r);
            out(gs.t("Supervisor assigned.", "Тағайындалды.", "Назначен."));
        }
    }

    private static void publishDiplomaMenu(GraduateStudent gs) {
        pr("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { out("Title cannot be empty."); return; }
        ResearchPaper p = new ResearchPaper(title, "University Repository", 80, new Date());
        p.addAuthor(gs.getFirstName() + " " + gs.getLastName());
        gs.publishDiplomaPaper(p);
        out("Published.");
    }

    private static void publishResearchMenu(User owner) {
        pr("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { out("Title cannot be empty."); return; }
        int cit = readInt("Citations count: ");
        ResearchPaper p = new ResearchPaper(title, "University Journal", 20, new Date());
        p.addAuthor(owner.getFirstName() + " " + owner.getLastName());
        for (int i = 0; i < cit; i++) p.addCitation();
        if (owner instanceof Teacher) researcherOf((Teacher) owner).publishPaper(p);
        else if (owner instanceof Student) researcherOf((Student) owner).publishPaper(p);
        out("Published.");
    }

    private static void citationMenu(Researcher r) {
        List<ResearchPaper> papers = r.getPapers();
        if (papers.isEmpty()) { out("(no papers)"); return; }
        ResearchPaper p = papers.get(0);
        out("--- Plain Text ---\n" + p.getCitation(CitationFormat.PLAIN_TEXT));
        out("--- BibTeX ---\n" + p.getCitation(CitationFormat.BIBTEX));
    }

    private static void leadProjectMenu(Researcher r) {
        pr("Project topic: ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) { out("Topic cannot be empty."); return; }
        ResearchProject project = new ResearchProject(topic);
        r.leadProject(project);
        out("Project created.");
    }

    private static void addParticipantMenu(Researcher r) {
        List<ResearchProject> projects = storage.getProjects();
        if (projects.isEmpty()) { out("Create project first (option 11)."); return; }
        ResearchProject project = projects.get(0);
        out("1. Add valid researcher  2. Demo NotResearcherException (null)");
        if ("2".equals(scanner.nextLine().trim())) {
            try {
                project.addParticipant(null);
            } catch (NotResearcherException e) {
                out("Caught: " + e.getMessage());
            }
        } else {
            List<Researcher> researchers = storage.getAllResearchers();
            Researcher part = pickResearcher(researchers);
            if (part != null) {
                try {
                    project.addParticipant(part);
                    out("Participant added.");
                } catch (NotResearcherException e) {
                    out(e.getMessage());
                }
            }
        }
    }

    private static void manageCourseMenu(Teacher t) {
        Course c = pickCourse(t.viewCourses(), "course");
        if (c == null) return;
        pr("Lesson topic: ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) { out("Topic cannot be empty."); return; }
        out("1.LECTURE  2.PRACTICE");
        LessonType lt = "2".equals(scanner.nextLine().trim()) ? LessonType.PRACTICE : LessonType.LECTURE;
        c.addLesson(new Lesson("L" + System.currentTimeMillis(), topic, lt));
        storage.addCourse(c);
        out("Lesson added.");
    }

    private static void putMarkMenu(Teacher t) {
        Course c = pickCourse(t.viewCourses(), "course");
        if (c == null) return;
        Student s = pickStudent();
        if (s == null) return;
        double a1 = readDouble("1st attestation (0-30): ");
        double a2 = readDouble("2nd attestation (0-30): ");
        double fin = readDouble("Final (0-40): ");
        t.putMark(s, c, new Mark(a1, a2, fin, s, c));
        out("Mark saved.");
    }

    private static void sendComplaintMenu(Teacher t) {
        Student s = pickStudent();
        if (s == null) return;
        pr("Text: ");
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) { out("Text cannot be empty."); return; }
        out("1.LOW  2.MEDIUM  3.HIGH");
        ComplaintUrgency u = ComplaintUrgency.MEDIUM;
        String ch = scanner.nextLine().trim();
        if ("1".equals(ch)) u = ComplaintUrgency.LOW;
        else if ("3".equals(ch)) u = ComplaintUrgency.HIGH;
        Complaint c = t.sendComplaint(s, text, u);
        if (c != null) outObj(c);
    }

    private static void sendMessageMenu(Employee from) {
        List<Employee> emps = new ArrayList<>();
        for (User u : storage.getUsers()) {
            if (u instanceof Employee && !u.equals(from)) emps.add((Employee) u);
        }
        if (emps.isEmpty()) { out("No other employees."); return; }
        Employee to = pickEmployee(emps);
        if (to == null) return;
        pr("Message: ");
        String msg = scanner.nextLine().trim();
        if (msg.isEmpty()) { out("Message cannot be empty."); return; }
        from.sendMessage(to, msg);
        out("Sent.");
    }

    private static void assignCourseMenu(Manager m) {
        Teacher t = pickTeacher(allTeachers());
        Course c = pickCourse(storage.getCourses(), "course");
        if (t != null && c != null) {
            m.assignCourse(t, c);
            out("Assigned.");
        }
    }

    private static void approveRegistrationMenu(Manager m) {
        Student s = pickStudent();
        Course c = pickCourse(storage.getCourses(), "course");
        if (s != null && c != null) {
            m.approveRegistration(s, c);
            out("Approved.");
        }
    }

    private static void addCourseForRegMenu(Manager m) {
        pr("Course id: ");
        String id = scanner.nextLine().trim();
        pr("Name: ");
        String name = scanner.nextLine().trim();
        int cred = readInt("Credits: ");
        out("Type  1.MAJOR  2.MINOR  3.FREE_ELECTIVE:");
        CourseType ct = CourseType.MAJOR;
        String tc = scanner.nextLine().trim();
        if ("2".equals(tc)) ct = CourseType.MINOR;
        else if ("3".equals(tc)) ct = CourseType.FREE_ELECTIVE;
        pr("Major/Faculty: ");
        String major = scanner.nextLine().trim();
        int year = readInt("Study year: ");
        Course course = new Course(id, name, cred, ct, major, year);
        m.addCourseForRegistration(course, major, year);
        out("Course added.");
    }

    private static void publishNewsMenu(Manager m) {
        pr("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { out("Title cannot be empty."); return; }
        pr("Content: ");
        String content = scanner.nextLine().trim();
        pr("Topic (Research = pinned): ");
        String topic = scanner.nextLine().trim();
        m.publishNews(title, content, topic);
        out("Published.");
    }

    private static void publishToJournalMenu() {
        List<Journal> journals = storage.getJournals();
        if (journals.isEmpty()) { out("No journals in system."); return; }
        Journal j = journals.get(0);
        pr("Paper title for journal: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { out("Title cannot be empty."); return; }
        ResearchPaper p = new ResearchPaper(title, j.getName(), 15, new Date());
        j.publishPaper(p);
        out("Published — subscribers notified (Observer).");
    }

    private static void printPapersMenu(Researcher r) {
        out("Sort: 1.Date  2.Citations  3.Pages");
        Comparator<ResearchPaper> comp = new DateComparator();
        String ch = scanner.nextLine().trim();
        if ("2".equals(ch)) comp = new CitationComparator();
        else if ("3".equals(ch)) comp = new LengthComparator();
        if (r != null) r.printPapers(comp);
        else storage.printAllResearchersPapers(comp);
    }

    private static void processRequest(TechSupportSpecialist ts, boolean accept, boolean reject, boolean done) {
        List<Request> pool = done ? allAcceptedRequests() : ts.viewNewRequests();
        if (pool.isEmpty()) { out("No matching requests."); return; }
        Request r = pickRequest(pool);
        if (r == null) return;
        if (accept) ts.acceptRequest(r);
        else if (reject) ts.rejectRequest(r);
        else if (done) ts.markAsDone(r);
        out("Status: " + r.getStatus());
    }

    private static List<Request> allAcceptedRequests() {
        List<Request> list = new ArrayList<>();
        for (Request r : storage.getRequests()) {
            if (r.getStatus() == RequestStatus.ACCEPTED) list.add(r);
        }
        return list;
    }

    // ===================== NEWS (paged) =====================

    private static void viewNewsPaged(Student s) {
        // We collect news from storage indirectly via printNewsFeed,
        // but to paginate we need the list. Let's use storage directly.
        List<News> newsList = storage.getNews();
        if (newsList == null || newsList.isEmpty()) {
            out(s.t("No news.", "Жаңалық жоқ.", "Новостей нет."));
            return;
        }
        showNewsPaged(newsList, s);
    }

    private static void viewNewsPaged(Employee e) {
        List<News> newsList = storage.getNews();
        if (newsList == null || newsList.isEmpty()) {
            out(e.t("No news.", "Жаңалық жоқ.", "Новостей нет."));
            return;
        }
        showNewsPaged(newsList, e);
    }

    private static void showNewsPaged(List<News> newsList, User viewer) {
        int from = 0;
        while (from < newsList.size()) {
            int to = Math.min(from + PAGE_SIZE, newsList.size());
            out("\n--- " + viewer.t("News", "Жаңалықтар", "Новости")
                    + " [" + (from + 1) + "-" + to + " / " + newsList.size() + "] ---");
            for (int i = from; i < to; i++) {
                out("\n[" + (i + 1) + "]");
                newsList.get(i).print(viewer);
            }
            if (to >= newsList.size()) break;
            int remaining = Math.min(PAGE_SIZE, newsList.size() - to);
            out("\n" + viewer.t("1. Next " + remaining + "  0. Back to menu",
                    "1. Келесі " + remaining + "  0. Мәзір",
                    "1. Ещё " + remaining + "  0. В меню"));
            pr(viewer.t("Choice: ", "Таңдау: ", "Выбор: "));
            String ch = scanner.nextLine().trim();
            if (!"1".equals(ch)) break;
            from = to;
        }
    }

    // ===================== ADMIN HELPERS =====================

    private static void adminAddUser(Admin a) {
        out("Type: 1.Student  2.Teacher  3.Graduate  4.Manager  5.TechSupport");
        String type = scanner.nextLine().trim();
        pr("ID: "); String id = scanner.nextLine().trim();
        pr("First name: "); String fn = scanner.nextLine().trim();
        pr("Last name: "); String ln = scanner.nextLine().trim();
        pr("Email: "); String email = scanner.nextLine().trim();
        pr("Password: "); String pass = scanner.nextLine().trim();
        Language lang = a.getLanguage();

        switch (type) {
            case "2":
                a.addUser(new Teacher(id, fn, ln, email, pass, lang, "EMP" + id, 50000,
                        "General", TeacherPosition.LECTOR));
                break;
            case "3":
                a.addUser(new GraduateStudent(id, fn, ln, email, pass, lang, id));
                break;
            case "4":
                a.addUser(new Manager(id, fn, ln, email, pass, lang, "EMP" + id, 55000,
                        "Office", ManagerType.DEPARTMENT));
                break;
            case "5":
                a.addUser(new TechSupportSpecialist(id, fn, ln, email, pass, lang, "EMP" + id, 40000, "IT"));
                break;
            default:
                Student st = new Student(id, fn, ln, email, pass, lang, id);
                pr("Faculty: "); st.setFaculty(scanner.nextLine().trim());
                st.setStudyYear(readInt("Study year (1-7): "));
                a.addUser(st);
        }
        out("User added.");
    }

    private static void adminUpdateUser(Admin a) {
        pr("User id: ");
        User u = storage.findUserById(scanner.nextLine().trim());
        if (u == null) { out("Not found."); return; }
        out("Updating: " + u);
        pr("New email (empty=skip): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            out("Email update stored on next save for id " + u.getId());
        }
        a.updateUser(u);
        out("User updated (logged).");
    }

    private static void viewLogs(Admin a) {
        List<Log> logs = a.viewLogFiles();
        if (logs.isEmpty()) {
            out(a.t("No logs. Add/remove/update users to create logs.", "Лог жоқ.", "Логов нет."));
        } else {
            showPaged(logs, "logs");
        }
    }

    // ===================== PICKERS =====================

    private static Course pickCourse(List<Course> list, String label) {
        if (list == null || list.isEmpty()) { out("No courses."); return null; }
        out("\n--- Courses ---");
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            out((i + 1) + ". " + c);
        }
        int idx = readInt("Select " + label + " (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    private static Student pickStudent() {
        List<Student> list = new ArrayList<>();
        for (User u : storage.getUsers()) {
            if (u instanceof Student) list.add((Student) u);
        }
        if (list.isEmpty()) { out("No students."); return null; }
        out("\n--- Students ---");
        for (int i = 0; i < list.size(); i++) out((i + 1) + ". " + list.get(i));
        int idx = readInt("Select student (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    private static Teacher pickTeacher(List<Teacher> list) {
        if (list == null || list.isEmpty()) { out("No teachers."); return null; }
        out("\n--- Teachers ---");
        for (int i = 0; i < list.size(); i++) out((i + 1) + ". " + list.get(i));
        int idx = readInt("Select teacher (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    private static List<Teacher> allTeachers() {
        List<Teacher> list = new ArrayList<>();
        for (User u : storage.getUsers()) {
            if (u instanceof Teacher) list.add((Teacher) u);
        }
        return list;
    }

    private static Employee pickEmployee(List<Employee> list) {
        if (list.isEmpty()) { out("No employees."); return null; }
        out("\n--- Employees ---");
        for (int i = 0; i < list.size(); i++) out((i + 1) + ". " + list.get(i));
        int idx = readInt("Select (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    private static Researcher pickResearcher(List<Researcher> list) {
        if (list.isEmpty()) { out("No researchers."); return null; }
        out("\n--- Researchers ---");
        for (int i = 0; i < list.size(); i++) {
            out((i + 1) + ". h-index=" + list.get(i).calculateHIndex());
        }
        int idx = readInt("Select (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    private static Request pickRequest(List<Request> list) {
        out("\n--- Requests ---");
        for (int i = 0; i < list.size(); i++) out((i + 1) + ". " + list.get(i));
        int idx = readInt("Select request (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

    // ===================== PAGINATION =====================

    /** Show a list page by page (PAGE_SIZE items at a time) */
    private static <T> void showPaged(List<T> items, String label) {
        if (items == null || items.isEmpty()) { out("(" + label + ": none)"); return; }
        int from = 0;
        while (from < items.size()) {
            int to = Math.min(from + PAGE_SIZE, items.size());
            out("\n--- " + label + " [" + (from + 1) + "-" + to + " / " + items.size() + "] ---");
            for (int i = from; i < to; i++) outObj(items.get(i));
            if (to >= items.size()) break;
            int remaining = Math.min(PAGE_SIZE, items.size() - to);
            out("1. Next " + remaining + "   0. Back to menu");
            pr("Choice: ");
            String ch = scanner.nextLine().trim();
            if (!"1".equals(ch)) break;
            from = to;
        }
    }

    // ===================== JOURNAL / LANGUAGE =====================

    private static void switchLanguage() {
        out(currentUser.t("1.KZ  2.EN  3.RU", "1.KZ  2.EN  3.RU", "1.KZ  2.EN  3.RU"));
        String c = scanner.nextLine().trim();
        if ("1".equals(c)) currentUser.setLanguage(Language.KZ);
        else if ("2".equals(c)) currentUser.setLanguage(Language.EN);
        else if ("3".equals(c)) currentUser.setLanguage(Language.RU);
        out("Language: " + currentUser.getLanguage());
    }

    private static void subscribeJournal() {
        List<Journal> journals = storage.getJournals();
        if (journals.isEmpty()) { out("No journals."); return; }
        for (int i = 0; i < journals.size(); i++) out((i + 1) + ". " + journals.get(i).getName());
        int idx = readInt("Journal (0=cancel): ") - 1;
        if (idx >= 0 && idx < journals.size()) {
            journals.get(idx).subscribe(currentUser);
            out("Subscribed.");
        }
    }

    private static void unsubscribeJournal() {
        List<Journal> journals = storage.getJournals();
        if (journals.isEmpty()) { out("No journals."); return; }
        for (int i = 0; i < journals.size(); i++) out((i + 1) + ". " + journals.get(i).getName());
        int idx = readInt("Journal (0=cancel): ") - 1;
        if (idx >= 0 && idx < journals.size()) {
            journals.get(idx).unsubscribe(currentUser);
            out("Unsubscribed.");
        }
    }

    // ===================== SAMPLE DATA =====================

    private static void initSampleData() {
        Admin admin = new Admin("ADM001", "Alice", "Johnson", "admin@uni.edu", "admin",
                Language.EN, "EMP001", 60000, "Administration");
        storage.addUser(admin);

        Teacher professor = new Teacher("TCH001", "Bob", "Smith", "teacher@uni.edu", "teacher",
                Language.EN, "EMP002", 70000, "Computer Science", TeacherPosition.PROFESSOR);
        storage.addUser(professor);

        Manager manager = new Manager("MGR001", "Charlie", "Brown", "manager@uni.edu", "manager",
                Language.EN, "EMP003", 55000, "Academic Affairs", ManagerType.DEPARTMENT);
        storage.addUser(manager);

        Student student = new Student("STU001", "David", "Wilson", "student@uni.edu", "student",
                Language.EN, "STU001");
        student.setGpa(3.85);
        student.setFaculty("SITE");
        student.setStudyYear(2);
        storage.addUser(student);

        Student student2 = new Student("STU003", "Anna", "Lee", "anna@uni.edu", "anna",
                Language.EN, "STU003");
        student2.setGpa(3.50);
        student2.setFaculty("Business");
        student2.setStudyYear(3);
        storage.addUser(student2);

        Student student3 = new Student("STU004", "Mark", "Taylor", "mark@uni.edu", "mark",
                Language.EN, "STU004");
        student3.setGpa(2.90);
        student3.setFaculty("SITE");
        student3.setStudyYear(1);
        storage.addUser(student3);

        Student student4 = new Student("STU005", "Sara", "Khan", "sara@uni.edu", "sara",
                Language.EN, "STU005");
        student4.setGpa(3.70);
        student4.setFaculty("Law");
        student4.setStudyYear(4);
        student4.setGraduated(true);
        storage.addUser(student4);

        GraduateStudent graduate = new GraduateStudent("STU002", "Emma", "Davis", "graduate@uni.edu",
                "graduate", Language.EN, "STU002");
        graduate.setGpa(3.95);
        graduate.setFaculty("SITE");
        graduate.setStudyYear(5);
        storage.addUser(graduate);

        TechSupportSpecialist tech = new TechSupportSpecialist("TEC001", "Frank", "Miller",
                "tech@uni.edu", "tech", Language.EN, "EMP004", 40000, "IT");
        storage.addUser(tech);

        Course javaCourse = new Course("CMP101", "Advanced Java", 3, CourseType.MAJOR, "SITE", 2);
        javaCourse.addLesson(new Lesson("L01", "OOP", LessonType.LECTURE));
        javaCourse.addLesson(new Lesson("L02", "Patterns", LessonType.PRACTICE));
        professor.manageCourse(javaCourse);
        storage.addCourse(javaCourse);

        Course algoCourse = new Course("CMP201", "Algorithms", 4, CourseType.MAJOR, "SITE", 3);
        professor.manageCourse(algoCourse);
        storage.addCourse(algoCourse);

        manager.approveRegistration(student, javaCourse);
        professor.putMark(student, javaCourse, new Mark(95, 92, 98, student, javaCourse));

        TeacherResearcher resProf = new TeacherResearcher(professor);
        for (int i = 0; i < 15; i++) {
            ResearchPaper p = new ResearchPaper("ML Paper " + (i + 1), "IEEE", 20, new Date());
            p.addAuthor("Bob Smith");
            for (int j = 0; j <= i; j++) p.addCitation();
            resProf.publishPaper(p);
        }

        Journal journal = new Journal("Journal of Computer Science");
        storage.addJournal(journal);
        journal.subscribe(student);
        journal.subscribe(graduate);

        manager.publishNews("Welcome", "University Research System is online.", "General");
        manager.publishNews("Scholarship", "Applications open for Spring 2026.", "Academic");
        manager.publishNews("Conference", "International CS Conference coming in June.", "Research");
        manager.publishNews("Events", "Cultural week starts next Monday.", "General");

        student.submitRequest("Fix projector in room 101");

        ResearchProject proj = new ResearchProject("AI in Education");
        resProf.leadProject(proj);

        storage.addLog(new Log(UUID.randomUUID().toString(), "ADM001", "System initialized", new Date()));

        out("\n--- Sample accounts ---");
        out("  admin@uni.edu     / admin");
        out("  teacher@uni.edu   / teacher  (Professor, researcher)");
        out("  manager@uni.edu   / manager");
        out("  student@uni.edu   / student  (SITE, year 2)");
        out("  graduate@uni.edu  / graduate (SITE, year 5, researcher)");
        out("  tech@uni.edu      / tech");
        out("  anna@uni.edu      / anna     (Business, year 3)");
        out("  mark@uni.edu      / mark     (SITE, year 1)");
        out("  sara@uni.edu      / sara     (Law, year 4, graduated)");
    }

    // ===================== UTIL =====================

    private static void printHeader() {
        out("╔══════════════════════════════════════════════════╗");
        out("║     UNIVERSITY RESEARCH SYSTEM                   ║");
        out("║     OOP Final Project — full use case coverage   ║");
        out("╚══════════════════════════════════════════════════╝");
    }

    private static String roleName(User u) {
        if (u instanceof Admin)                 return u.t("Admin", "Әкімші", "Админ");
        if (u instanceof Manager)               return u.t("Manager", "Менеджер", "Менеджер");
        if (u instanceof TechSupportSpecialist) return u.t("Tech Support", "Тех. қолдау", "Техподдержка");
        if (u instanceof Teacher)               return u.t("Teacher", "Оқытушы", "Преподаватель");
        if (u instanceof GraduateStudent)       return u.t("Graduate", "Магистрант", "Магистрант");
        if (u instanceof Student)               return u.t("Student", "Студент", "Студент");
        return "User";
    }

    private static String txt(User u, String en, String kz, String ru) {
        if (u == null) return en;
        return u.t(en, kz, ru);
    }

    private static void printPanel(User u, String en, String kz, String ru) {
        out("\n--- " + u.t(en, kz, ru) + " ---");
    }

    /** Simple yes/no confirmation */
    private static boolean confirm(String prompt) {
        pr(prompt + " (y/n): ");
        String ch = scanner.nextLine().trim().toLowerCase();
        return "y".equals(ch) || "yes".equals(ch);
    }

    private static int readInt(String prompt) {
        if (!prompt.isEmpty()) pr(prompt);
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double readDouble(String prompt) {
        pr(prompt);
        try { return Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private static String readLine(String prompt) {
        pr(prompt);
        return scanner.nextLine().trim();
    }

    private static void out(String s) { System.out.println(s); }
    private static void pr(String s)  { System.out.print(s); }
    private static void outObj(Object o) { System.out.println(o); }
}