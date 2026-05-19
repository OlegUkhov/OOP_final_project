import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

//Uses every domain class: patterns Singleton, Decorator, Observer; Comparators; exceptions.

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;
    private static DataStorage storage;

    private static final int PAGE_SIZE = 10;  // items per page for paginated lists

    // Generate short, human-friendly IDs like STU001 / TCH001 / ADM001
    private static String generateId(String prefix) {
        int max = 0;
        for (User u : DataStorage.getInstance().getUsers()) {
            String id = u.getId();
            if (id != null && id.startsWith(prefix)) {
                String num = id.replaceAll("\\D", "");
                try { int n = Integer.parseInt(num); if (n > max) max = n; } catch (Exception ex) {}
            }
        }
        return prefix + String.format("%03d", max + 1);
    }
    // Guest language (used before login). Default: English
    private static Language guestLanguage = Language.EN;

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
//
    private static boolean loginScreen() {
        User u = currentUser;
        out("\n--- " + txt(u, "Login", "Кіру", "Вход") + " ---");
        out("1. " + txt(u, "Login", "Кіру", "Войти"));
        out("2. " + txt(u, "News", "Жаңалықтар", "Новости"));
        out("3. " + txt(u, "Sign up (register)", "Тіркелу", "Регистрация"));
        out("4. " + txt(u, "Change language", "Тілді өзгерту", "Сменить язык"));
        out("0. " + txt(u, "Exit", "Шығу", "Выход"));
        pr(txt(u, "Choice: ", "Таңдау: ", "Выбор: "));
        String c = scanner.nextLine().trim();
        if ("0".equals(c)) return false;
        if ("3".equals(c)) { signUpScreen(); return true; }
        if ("2".equals(c)) { viewPublicNews(); return true; }
        if ("4".equals(c)) { switchGuestLanguage(); return true; }
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
        out("\n");
        out("  " + currentUser.getFirstName() + " " + currentUser.getLastName()
                + "  |  " + roleName(currentUser) + "  |  " + currentUser.getLanguage());
        out("");

        if (currentUser instanceof Admin)                return adminMenu((Admin) currentUser);
        if (currentUser instanceof Manager)              return managerMenu((Manager) currentUser);
        if (currentUser instanceof TechSupportSpecialist) return techMenu((TechSupportSpecialist) currentUser);
        if (currentUser instanceof Teacher)              return teacherMenu((Teacher) currentUser);
        if (currentUser instanceof GraduateStudent)      return graduateMenu((GraduateStudent) currentUser);
        if (currentUser instanceof Student)              return studentMenu((Student) currentUser);
        return handleCommon("0", currentUser);
    }


    private static boolean adminMenu(Admin a) {
        printPanel(a, "Admin Panel", "Әкімші панелі", "Панель админа");
        out("1.  " + a.t("View all users", "Барлық пайдаланушылар", "Все пользователи"));
        out("2.  " + a.t("Add user", "Пайдаланушы қосу", "Добавить пользователя"));
        out("3.  " + a.t("Update user", "Пайдаланушыны өзгерту", "Обновить пользователя"));
        out("4.  " + a.t("Remove user", "Жою", "Удалить пользователя"));
        out("5.  " + a.t("View log files", "Логтар", "Логи"));
        out("6.  " + a.t("User registration requests", "Тіркеу өтініштері", "Запросы регистрации"));
        out("7.  " + a.t("Toggle user registrations (open/closed)", "Қолданушыларды тіркеуді қосу/өшіру", "Открыть/закрыть регистрацию пользователей"));
        out("8.  " + a.t("Toggle course registrations (open/closed)", "Курстарға тіркеуді қосу/өшіру", "Открыть/закрыть регистрацию на курсы"));
        out("9.  " + a.t("View expelled students (>3 retakes)", "Шығарылған студенттер", "Отчисленные студенты (>3 пересдач)"));
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
            case "6":
                approveRegistrationMenu(a);
                break;
            case "7":
                boolean regOpen = DataStorage.getInstance().isRegistrationsOpen();
                DataStorage.getInstance().setRegistrationsOpen(!regOpen);
                out(a.t("User registrations now " + (regOpen ? "closed" : "open"), "Тіркеу " + (regOpen ? "жабылды" : "ашылды"), "Регистрация пользователей " + (regOpen ? "закрыта" : "открыта")));
                break;
            case "8":
                boolean cOpen = DataStorage.getInstance().isCourseRegistrationOpen();
                DataStorage.getInstance().setCourseRegistrationOpen(!cOpen);
                out(a.t("Course registrations now " + (cOpen ? "closed" : "open"), "Курстарға тіркеу " + (cOpen ? "жабылды" : "ашылды"), "Регистрация на курсы " + (cOpen ? "закрыта" : "открыта")));
                break;
            case "9":
                viewExpelledStudents(a);
                break;
            default:
                out(a.t("Unknown option.", "Белгісіз.", "Неизвестно."));
        }
        pause();
        return true;
    }


    private static boolean managerMenu(Manager m) {
        printPanel(m, "Manager Panel", "Менеджер панелі", "Панель менеджера");
        out("1.  " + m.t("Manage courses (add/delete/groups)", "Курс басқару", "Управление курсами"));
        out("2.  " + m.t("Assign course to teacher", "Курс тағайындау", "Назначить курс преподавателю"));
        out("3.  " + m.t("Approve student registration", "Тіркеуді бекіту", "Одобрить регистрацию"));
        out("4.  " + m.t("Statistical report", "Есеп", "Статистический отчёт"));
        out("5.  " + m.t("Publish news", "Жаңалық жариялау", "Опубликовать новость"));
        out("6.  " + m.t("Top cited researcher news", "Топ цитирований", "Новость о топ исследователе"));
        out("7.  " + m.t("Students sorted by GPA", "Студенттер GPA", "Студенты по GPA"));
        out("8.  " + m.t("Students sorted by name", "Студенттер аты", "Студенты по имени"));
        out("9.  " + m.t("Employee requests", "Қызметкер өтінімдері", "Заявки сотрудников"));
        out("10. " + m.t("Print all university papers", "Барлық мақалалар", "Все статьи вуза"));
        out("11. " + m.t("Submit employee request", "Өтініш жіберу", "Отправить заявку"));
        out("12. " + m.t("View expelled students (>3 retakes)", "Шығарылған студенттер", "Отчисленные студенты"));
        printCommonMenu(m);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, m)) return !"0".equals(c);

        try {
            switch (c) {
                case "1":  manageCoursesMenu(m); break;
                case "2":  assignCourseMenu(m); break;
                case "3":  approveRegistrationMenu(m); break;
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
                case "12": viewExpelledStudents(m); break;
                default:   out(m.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseOverloadException | CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        pause();
        return true;
    }

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


    private static boolean techMenu(TechSupportSpecialist ts) {
        printPanel(ts, "Tech Support", "Тех. қолдау", "Техподдержка");
        out("1.  " + ts.t("Active requests", "Белсенді өтінімдер", "Активные заявки"));
        out("2.  " + ts.t("Completed requests", "Орындалған өтінімдер", "Завершённые заявки"));
        printCommonMenu(ts);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, ts)) return !"0".equals(c);

        switch (c) {
            case "1": activeRequestsMenu(ts); break;
            case "2": completedRequestsMenu(ts); break;
            default: out(ts.t("Unknown.", "Белгісіз.", "Неизвестно."));
        }
        pause();
        return true;
    }

    private static void activeRequestsMenu(TechSupportSpecialist ts) {
        List<Request> active = getRequestsByStatus(RequestStatus.VIEWED, RequestStatus.ACCEPTED);
        if (active.isEmpty()) {
            out(ts.t("No active requests.", "Өтінім жоқ.", "Активных заявок нет."));
            return;
        }
        while (true) {
            out("\n--- " + ts.t("Active requests", "Белсенді өтінімдер", "Активные заявки")
                    + " [" + active.size() + "] ---");
            for (int i = 0; i < active.size(); i++) {
                Request r = active.get(i);
                out((i + 1) + ". [" + r.getStatus() + "] " + r);
            }
            out("0. " + ts.t("Back", "Артқа", "Назад"));
            int idx = readInt(ts.t("Select request: ", "Нөмір: ", "Номер: ")) - 1;
            if (idx == -1) return;
            if (idx < 0 || idx >= active.size()) { out(ts.t("Invalid.", "Қате.", "Неверно.")); continue; }

            Request selected = active.get(idx);
            out("\n" + selected);
            out(ts.t("Status: ", "Күй: ", "Статус: ") + selected.getStatus());
            out("1. " + ts.t("Accept", "Қабылдау", "Принять"));
            out("2. " + ts.t("Reject", "Қабылдамау", "Отклонить"));
            out("3. " + ts.t("Mark as DONE", "Орындалды", "Выполнено"));
            out("0. " + ts.t("Back", "Артқа", "Назад"));
            pr("Choice: ");
            String action = scanner.nextLine().trim();
            switch (action) {
                case "1":
                    ts.acceptRequest(selected);
                    out(ts.t("Accepted. Status: ", "Қабылданды. Күй: ", "Принято. Статус: ") + selected.getStatus());
                    break;
                case "2":
                    ts.rejectRequest(selected);
                    out(ts.t("Rejected. Status: ", "Қабылданбады. Күй: ", "Отклонено. Статус: ") + selected.getStatus());
                    active.remove(selected);
                    break;
                case "3":
                    ts.markAsDone(selected);
                    out(ts.t("Done. Status: ", "Орындалды. Күй: ", "Выполнено. Статус: ") + selected.getStatus());
                    active.remove(selected);
                    break;
                case "0": break;
                default: out(ts.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        }
    }

    private static void completedRequestsMenu(TechSupportSpecialist ts) {
        List<Request> done = getRequestsByStatus(RequestStatus.DONE, RequestStatus.REJECTED);
        showPaged(done, ts.t("completed requests", "орындалған өтінімдер", "завершённых заявок"));
    }

    private static List<Request> getRequestsByStatus(RequestStatus... statuses) {
        List<Request> list = new ArrayList<>();
        for (Request r : storage.getRequests()) {
            for (RequestStatus s : statuses) {
                if (r.getStatus() == s) { list.add(r); break; }
            }
        }
        return list;
    }


    private static boolean teacherMenu(Teacher t) {
        printPanel(t, "Teacher Panel", "Оқытушы панелі", "Панель преподавателя");
        out("1.  " + t.t("View courses", "Курстар", "Курсы"));
        out("2.  " + t.t("Manage course (add lesson)", "Сабақ қосу", "Управление курсом"));
        out("3.  " + t.t("Put mark (1st+2nd+final)", "Баға қою", "Выставить оценку"));
        out("4.  " + t.t("Set attestation/final score", "Аттестация/финал", "Выставить балл аттестации/финала"));
        out("5.  " + t.t("Close attestation", "Аттестация жабу", "Закрыть аттестацию"));
        out("6.  " + t.t("View students info", "Студенттер", "Инфо о студентах"));
        out("7.  " + t.t("Send complaint to dean", "Шағым", "Жалоба декану"));
        out("8.  " + t.t("Send message", "Хабарлама", "Сообщение"));
        if (t.isResearcher()) printResearcherMenu(t);
        else out("--- " + t.t("(Researcher: option 20)", "(Зертгер: 20)", "(Исследователь: 20)"));
        out("20. " + t.t("Enable researcher role", "Зертгер рөлі", "Роль исследователя"));
        out("21. " + t.t("Manage attendance (open/close)", "Посещаемость", "Управление посещаемостью"));
        out("22. " + t.t("Teacher Journal (gradebook)", "Журнал", "Журнал оценок"));
        printCommonMenu(t);
        pr("Choice: ");
        String c = scanner.nextLine().trim();
        if (handleCommon(c, t)) return !"0".equals(c);

        try {
            switch (c) {
                case "1":  showPaged(t.viewCourses(), t.t("courses", "курстар", "курсов")); break;
                case "2":  manageCourseMenu(t); break;
                case "3":  putMarkMenu(t); break;
                case "4":  setScoreMenu(t); break;
                case "5":  closeAttestationMenu(t); break;
                case "6":  t.viewStudentsInfo(); break;
                case "7":  sendComplaintMenu(t); break;
                case "8":  sendMessageMenu(t); break;
                case "9":  if (t.isResearcher()) publishResearchMenu(t); else needResearcher(t); break;
                case "10": if (t.isResearcher()) out("h-index: " + researcherOf(t).calculateHIndex()); else needResearcher(t); break;
                case "11": if (t.isResearcher()) printPapersMenu(researcherOf(t)); else needResearcher(t); break;
                case "12": if (t.isResearcher()) citationMenu(researcherOf(t)); else needResearcher(t); break;
                case "13": if (t.isResearcher()) leadProjectMenu(researcherOf(t)); else needResearcher(t); break;
                case "14": if (t.isResearcher()) addParticipantMenu(researcherOf(t)); else needResearcher(t); break;
                case "20": t.setResearcher(true); out(t.t("Researcher enabled.", "Қосылды.", "Включено.")); break;
                case "21": manageAttendanceMenu(t); break;
                case "22": teacherJournalMenu(t); break;
                default:   out(t.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        pause();
        return true;
    }


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
        out("11. " + s.t("Attendance (mark/view)", "Қатысу (отметить/просмотр)", "Посещаемость (отметить/просмотр)"));
        out("12. " + s.t("My retake courses", "Қайта курстар", "Мои курсы на пересдачу"));
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
                    List<Course> avail = new ArrayList<>();
                    for (Course course : storage.getCourses()) if (!DataStorage.getInstance().isEnrolled(s, course)) avail.add(course);
                    showPaged(avail, s.t("courses", "курстар", "курсов"));
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
                case "11": attendanceMenu(s); break;
                case "12": viewRetakeCourses(s); break;
                default:   out(s.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        } catch (CourseOverloadException | CourseFailLimitException ex) {
            out("Error: " + ex.getMessage());
        }
        pause();
        return true;
    }


    private static boolean graduateMenu(GraduateStudent gs) {
        printPanel(gs, "Graduate Student", "Магистрант", "Магистрант");
        out("--- " + gs.t("Student", "Студент", "Студент") + " ---");
        out("1.  " + gs.t("My courses", "Менің курстарым", "Мои курсы"));
        out("2.  " + gs.t("Available courses", "Қолжетімді курстар", "Доступные курсы"));
        out("3.  " + gs.t("Register for course (max 21 cr, 3 fails)", "Тіркелу", "Регистрация"));
        out("4.  " + gs.t("Teachers of course", "Оқытушылар", "Преподаватели курса"));
        out("5.  " + gs.t("View marks", "Бағалар", "Оценки"));
        out("6.  " + gs.t("View transcript", "Транскрипт", "Транскрипт"));
        out("7.  " + gs.t("Rate teacher", "Бағалау", "Оценить преподавателя"));
        out("8.  " + gs.t("Join organization", "Ұйымға қосылу", "Вступить в организацию"));
        out("9.  " + gs.t("Become head of organization", "Төраға", "Глава организации"));
        out("10. " + gs.t("Submit tech request", "Тех. өтініш", "Тех. заявка"));
        out("--- " + gs.t("Retake", "Қайта", "Пересдача") + " ---");
        out("10a. " + gs.t("My retake courses", "Қайта курстар", "Мои курсы на пересдачу"));
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
                pause();
                return true;
            }
            if ("10".equals(c)) { studentAction(gs, "10"); pause(); return true; }
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
        pause();
        return true;
    }

    private static void studentAction(Student s, String c) throws CourseOverloadException, CourseFailLimitException {
        switch (c) {
            case "1":  showPaged(s.viewCourses(), "courses"); break;
            case "2":  {
                List<Course> avail = new ArrayList<>();
                for (Course course : storage.getCourses()) if (!DataStorage.getInstance().isEnrolled(s, course)) avail.add(course);
                showPaged(avail, "courses");
            } break;
            case "3":  registerCourseMenu(s); break;
            case "4":  viewTeachersMenu(s); break;
            case "5":  showPaged(s.viewMarks(), "marks"); break;
            case "6":  out(s.getTranscript()); break;
            case "7":  rateTeacherMenu(s); break;
            case "8":  joinOrgMenu(s); break;
            case "9":  setOrgHeadMenu(s); break;
            case "10": s.submitRequest(readLine("Problem: ")); break;
            case "12": viewRetakeCourses(s); break;
        }
    }


    private static void printCommonMenu(User u) {
        out("--- " + u.t("All users", "Барлығы", "Все") + " ---");
        out("90. " + u.t("Switch language KZ/EN/RU", "Тіл", "Язык"));
        out("91. " + u.t("Subscribe to journal", "Жазылу", "Подписка на журнал"));
        out("92. " + u.t("Unsubscribe from journal", "Жазылудан бас тарту", "Отписка"));
        out("93. " + u.t("View news", "Жаңалықтар", "Новости"));
        out("94. " + u.t("Publish paper to journal (notify)", "Журналға мақала", "Статья в журнал"));
        out("95. " + u.t("Logout", "Шығу", "Выйти"));
        out("96. " + u.t("View my messages", "Менің хабарламалар", "Мои сообщения"));
        out("97. " + u.t("Change password", "Пароль өзгерту", "Сменить пароль"));
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
            case "96": viewMyMessages(u); return true;
            case "97": changePasswordMenu(u); return true;
            case "94": publishToJournalMenu(); return true;
            case "95": u.logout(); currentUser = null; return true;
            case "0":  return true;
            default:   return false;
        }
    }

    private static void printResearcherMenu(User u) {
        out("9.  " + u.t("Publish research paper", "Мақала жариялау", "Публикация"));
        out("10. " + u.t("Calculate h-index", "h-индекс", "h-индекс"));
        out("11. " + u.t("Print papers sorted", "Мақалалар", "Статьи сортировка"));
        out("12. " + u.t("Get citation Plain/BibTeX", "Цитата", "Цитирование"));
        out("13. " + u.t("Lead research project", "Жоба", "Проект"));
        out("14. " + u.t("Add participant to project", "Қатысушы", "Участник"));
    }

    private static Researcher researcherOf(Teacher t) { return new TeacherResearcher(t); }
    private static Researcher researcherOf(Student s) { return new StudentResearcher(s); }
    private static void needResearcher(Teacher t) {
        out(t.t("Enable researcher (option 20).", "20 қосыңыз.", "Включите опцию 20."));
    }

    private static void registerCourseMenu(Student s) {
        if (!DataStorage.getInstance().isCourseRegistrationOpen()) {
            out(s.t("Course registration is currently closed.", "Курстарға тіркелу жабық.", "Регистрация на курсы закрыта."));
            return;
        }
        List<Course> avail = new ArrayList<>();
        for (Course c : storage.getCourses()) {
            if (!DataStorage.getInstance().isEnrolled(s, c)) avail.add(c);
        }
        if (avail.isEmpty()) { out(s.t("No available courses.", "Қолжетімді курс жоқ.", "Нет доступных курсов.")); return; }
        out(s.t("Your current credits: " + s.getTotalCredits() + "/21", "Ағымдағы кредиттер: " + s.getTotalCredits() + "/21", "Текущие кредиты: " + s.getTotalCredits() + "/21"));
        Course c = pickCourse(avail, s.t("course", "курс", "курс"));
        if (c != null) {
            List<Lesson> lessons = c.getLessons();
            if (lessons.isEmpty()) { out("This course has no lessons/groups."); return; }
            out(c.getName() + " — " + c.getCredits() + " credits, type=" + c.getCourseType());
            out("Select group:");
            for (int i = 0; i < lessons.size(); i++) {
                Lesson l = lessons.get(i);
                String timeStr = (l.getDayOfWeek() != null && l.getStartTime() != null && l.getEndTime() != null)
                        ? " (" + l.getDayOfWeek() + " " + l.getStartTime() + "-" + l.getEndTime() + ")"
                        : "";
                out((i+1) + ". " + l.getTopic() + timeStr);
            }
            String li = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(li) - 1;
                if (idx >= 0 && idx < lessons.size()) {
                    s.registerForCourse(c, lessons.get(idx));
                    out(s.t("Registered.", "Тіркелді.", "Зарегистрирован."));
                } else {
                    out("Invalid selection.");
                }
            } catch (Exception ex) {
                out("Invalid input.");
            }
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
        out("1. " + s.t("Join existing organization", "Қосылу", "Вступить в существующую организацию"));
        out("2. " + s.t("Request new organization", "Жаңа ұйым сұрау", "Запросить создание организации"));
        pr(s.t("Choice: ", "Таңдау: ", "Выбор: "));
        String ch = scanner.nextLine().trim();
        if ("1".equals(ch)) {
            List<StudentOrganization> orgs = storage.getOrganizations();
            if (orgs.isEmpty()) { out(s.t("No organizations.", "Ұйым жоқ.", "Нет организаций.")); return; }
            out("--- Organizations ---");
            for (int i = 0; i < orgs.size(); i++) out((i+1) + ". " + orgs.get(i).getName());
            int idx = readInt(s.t("Select (0=cancel): ", "Таңдау: ", "Выбор: ")) - 1;
            if (idx == -1) return;
            if (idx >= 0 && idx < orgs.size()) { s.joinOrganization(orgs.get(idx)); out(s.t("Joined.", "Қосылды.", "Вступили.")); }
            else out(s.t("Invalid selection.", "Қате.", "Неверно."));
        } else if ("2".equals(ch)) {
            pr(s.t("Organization name: ", "Атауы: ", "Название: "));
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) { out(s.t("Name cannot be empty.", "Атауы бос.", "Название пусто.")); return; }
            s.submitRequest("Create organization: " + name);
            out(s.t("Creation request submitted.", "Өтініш жіберілді.", "Запрос создан."));
        } else {
            out(s.t("Unknown.", "Белгісіз.", "Неизвестно."));
        }
    }

    private static void signUpScreen() {
        if (!DataStorage.getInstance().isRegistrationsOpen()) {
            out(txt(null, "Registration is currently closed.", "Тіркеу қазіргі уақытта жабық.", "Регистрация в настоящее время закрыта."));
            return;
        }
        out(txt(null, "--- Sign Up ---", "--- Тіркелу ---", "--- Регистрация ---"));
        pr(txt(null, "First name: ", "Аты: ", "Имя: "));
        String first = scanner.nextLine().trim();
        pr(txt(null, "Last name: ", "Тегі: ", "Фамилия: "));
        String last = scanner.nextLine().trim();
        out(txt(null, "Role options: 1=Student, 2=Graduate, 3=Teacher, 4=Manager, 5=Admin, 6=Tech",
                "Рөл опциялар: 1=Студент,2=Магистрант,3=Оқытушы,4=Менеджер,5=Админ,6=Тех",
                "Опции роли: 1=Студент,2=Аспирант,3=Преподаватель,4=Менеджер,5=Админ,6=Тех"));
        pr(txt(null, "Role (number): ", "Рөл (нөмір): ", "Роль (номер): "));
        String r = scanner.nextLine().trim();
        String role = "Student";
        switch (r) {
            case "2": role = "Graduate"; break;
            case "3": role = "Teacher"; break;
            case "4": role = "Manager"; break;
            case "5": role = "Admin"; break;
            case "6": role = "Tech"; break;
            default: role = "Student"; break;
        }
        pr(txt(null, "Comment for admin: ", "Әкімшіге жазба: ", "Комментарий для админа: "));
        String comment = scanner.nextLine().trim();
        String faculty = "";
        String studyYear = "";
        if ("Student".equals(role) || "Graduate".equals(role)) {
            out(txt(null, "Select faculty (0=cancel):", "Факультетті таңдаңыз (0=болдырмау):", "Выберите факультет (0=отмена):"));
            Faculty[] facs = Faculty.values();
            for (int i = 0; i < facs.length; i++) out((i+1) + ". " + facs[i]);
            int fi = readInt(txt(null, "Choice: ", "Таңдау: ", "Выбор: ")) - 1;
            if (fi == -1) { out(txt(null, "Cancelled.", "Болдырмады.", "Отменено.")); return; }
            if (fi < 0 || fi >= facs.length) { out(txt(null, "Invalid faculty. Cancelling.", "Факультет дұрыс емес. Болдырылды.", "Неверный факультет. Отменено.")); return; }
            faculty = facs[fi].name();
            int sy = readInt(txt(null, "Study year (1-7, 0=cancel): ", "Курс (1-7, 0=болдырмау): ", "Курс (1-7, 0=отмена): "));
            if (sy == 0) { out(txt(null, "Cancelled.", "Болдырмады.", "Отменено.")); return; }
            if (sy < 1 || sy > 7) { out(txt(null, "Invalid study year. Cancelling.", "Қате курс. Болдырылды.", "Неверный курс. Отменено.")); return; }
            studyYear = String.valueOf(sy);
        }
        pr(txt(null, "Desired password (will be set by admin if blank): ", "Қалаған пароль (бос қалса әкім белгілейді): ", "Желаемый пароль (если пусто, установит админ): "));
        String desiredPwd = scanner.nextLine().trim();

        // build compact content
        String content = "SIGNUP|" + role + "|" + first + "|" + last + "|" + comment + "|" + faculty + "|" + studyYear + "|" + desiredPwd;
        Request req = new Request(java.util.UUID.randomUUID().toString(), null, content);
        DataStorage.getInstance().addMessage(req);
        out(txt(null, "Registration request submitted. Admin will approve.", "Тіркеу өтініші жіберілді. Әкімші мақұлдайды.", "Запрос на регистрацию отправлен. Админ утвердит."));
    }

    private static void approveRegistrationMenu(Admin a) {
        List<Request> list = DataStorage.getInstance().getRequests();
        List<Request> signups = new ArrayList<>();
        for (Request r : list) {
            if (r.getContent() != null && r.getContent().startsWith("SIGNUP|")) signups.add(r);
        }
        if (signups.isEmpty()) { out(a.t("No registration requests.", "Өтініштер жоқ.", "Нет запросов.")); return; }
        out("--- Registration Requests ---");
        for (int i = 0; i < signups.size(); i++) out((i+1) + ". " + signups.get(i).getContent());
        int idx = readInt(a.t("Select (0=cancel): ", "Таңдау: ", "Выбор: ")) - 1;
        if (idx == -1) return;
        if (idx < 0 || idx >= signups.size()) { out(a.t("Invalid selection.", "Қате таңдау.", "Неверно.")); return; }
        Request sel = signups.get(idx);
        String[] parts = sel.getContent().split("\\|");
        // SIGNUP|role|first|last|comment|faculty|studyYear|desiredPwd
        String role = parts.length > 1 ? parts[1] : "Student";
        String first = parts.length > 2 ? parts[2] : "";
        String last = parts.length > 3 ? parts[3] : "";
        String comment = parts.length > 4 ? parts[4] : "";
        String faculty = parts.length > 5 ? parts[5] : "";
        String studyYear = parts.length > 6 ? parts[6] : "";
        String desiredPwd = parts.length > 7 ? parts[7] : "";

        out("Request details:");
        out("Name: " + first + " " + last);
        out("Role: " + role);
        out("Comment: " + comment);
        if (!faculty.isEmpty()) out("Faculty: " + faculty);
        if (!studyYear.isEmpty()) out("Study year: " + studyYear);

        out("1. Approve and create user\n2. Reject request\n0. Cancel");
        int action = readInt("Choice: ");
        if (action == 0) { out("Cancelled."); return; }
        if (action == 2) {
            sel.setStatus(RequestStatus.REJECTED);
            out("Request rejected.");
            return;
        }

        String base;
        if (last == null || last.trim().isEmpty()) base = first.toLowerCase().replaceAll("\\s+", "");
        else base = (first + "_" + last).toLowerCase().replaceAll("\\s+", "");
        // strip leading/trailing underscores
        base = base.replaceAll("^_+|_+$", "");
        if (base.isEmpty()) base = "user" + (int)(Math.random() * 900 + 100);
        String suggestedEmail = base + "@kbtu.kz";
        pr("Email for user [" + suggestedEmail + "]: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = suggestedEmail;

        pr("Set initial password (leave blank to use requested or autogenerated): ");
        String pwd = scanner.nextLine().trim();
        if (pwd.isEmpty()) pwd = !desiredPwd.isEmpty() ? desiredPwd : "changeme";

        // create user according to role with friendly generated id
        User newUser = null;
        String idPrefix;
        if ("Student".equals(role) || "Graduate".equals(role)) idPrefix = "STU";
        else if ("Teacher".equals(role)) idPrefix = "TCH";
        else if ("Manager".equals(role)) idPrefix = "MGR";
        else if ("Admin".equals(role)) idPrefix = "ADM";
        else if ("Tech".equals(role)) idPrefix = "TEC";
        else idPrefix = "USR";
        String newId = generateId(idPrefix);
        if ("Student".equals(role) || "Graduate".equals(role)) {
            String studentId = newId;
            Student s = new Student(newId, first, last, email, pwd, Language.EN, studentId);
            if (!faculty.isEmpty()) s.setFaculty(faculty);
            if (!studyYear.isEmpty()) {
                try { s.setStudyYear(Integer.parseInt(studyYear)); } catch (Exception ex) {}
            }
            newUser = s;
        } else if ("Teacher".equals(role)) {
            String empId = newId;
            Teacher t = new Teacher(newId, first, last, email, pwd, Language.EN, empId, 0.0, "", TeacherPosition.TUTOR);
            newUser = t;
        } else if ("Manager".equals(role)) {
            String empId = newId;
            Manager m = new Manager(newId, first, last, email, pwd, Language.EN, empId, 0.0, "", ManagerType.OR);
            newUser = m;
        } else if ("Admin".equals(role)) {
            String empId = newId;
            Admin adm = new Admin(newId, first, last, email, pwd, Language.EN, empId, 0.0, "");
            newUser = adm;
        } else { // Tech
            String empId = newId;
            TechSupportSpecialist ts = new TechSupportSpecialist(newId, first, last, email, pwd, Language.EN, empId, 0.0, "");
            newUser = ts;
        }

        a.addUser(newUser);
        sel.setStatus(RequestStatus.ACCEPTED);
        out("User created: " + newUser + ". Email=" + email + " Password=" + pwd);
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
        out("Add schedule for this lesson? 1.Yes  2.No");
        String sched = scanner.nextLine().trim();
        if ("1".equals(sched)) {
            try {
                out("Select day: 1.Mon 2.Tue 3.Wed 4.Thu 5.Fri 6.Sat 7.Sun");
                int d = Integer.parseInt(scanner.nextLine().trim());
                DayOfWeek day = DayOfWeek.of(Math.max(1, Math.min(7, d)));
                out("Start time (HH:mm): ");
                LocalTime st = LocalTime.parse(scanner.nextLine().trim());
                out("End time (HH:mm): ");
                LocalTime et = LocalTime.parse(scanner.nextLine().trim());
                c.addLesson(new Lesson("L" + System.currentTimeMillis(), topic, lt, day, st, et));
            } catch (Exception ex) {
                out("Invalid schedule input — lesson created without schedule.");
                c.addLesson(new Lesson("L" + System.currentTimeMillis(), topic, lt));
            }
        } else {
            c.addLesson(new Lesson("L" + System.currentTimeMillis(), topic, lt));
        }
        storage.addCourse(c);
        out("Lesson added.");
    }

    private static void manageAttendanceMenu(Teacher t) {
        List<Course> courses = t.viewCourses();
        if (courses.isEmpty()) { out("No courses."); return; }
        Course c = pickCourse(courses, "course");
        if (c == null) return;
        List<Lesson> lessons = c.getLessons();
        if (lessons.isEmpty()) { out("No lessons for this course."); return; }
        out("Select lesson to manage attendance:");
        for (int i = 0; i < lessons.size(); i++) {
            out((i+1) + ". " + lessons.get(i));
        }
        int li = Integer.parseInt(scanner.nextLine().trim());
        if (li < 1 || li > lessons.size()) { out("Invalid selection."); return; }
        Lesson lesson = lessons.get(li-1);
        DataStorage ds = DataStorage.getInstance();
        out("1. Open attendance  2. Close attendance  3. View attendance for this lesson");
        String ch = scanner.nextLine().trim();
        if ("1".equals(ch)) { ds.openAttendance(c.getCourseId(), lesson.getLessonId()); out("Attendance opened for " + c.getCourseId() + " / " + lesson.getLessonId()); }
        else if ("2".equals(ch)) { ds.closeAttendance(c.getCourseId(), lesson.getLessonId()); out("Attendance closed for " + c.getCourseId() + " / " + lesson.getLessonId()); }
        else if ("3".equals(ch)) {
            List<Attendance> list = ds.getAttendancesForSession(c.getCourseId(), lesson.getLessonId());
            if (list.isEmpty()) { out("No attendance records for this lesson."); return; }
            for (Attendance a : list) outObj(a);
        } else out("Unknown.");
    }

    private static void attendanceMenu(Student s) {
        out("1. Mark attendance  2. View my attendance");
        String ch = scanner.nextLine().trim();
        if ("1".equals(ch)) {
            List<Course> courses = s.viewCourses();
            if (courses.isEmpty()) { out(s.t("No courses.", "Курс жоқ.", "Нет курсов.")); return; }
            Course c = pickCourse(courses, s.t("course", "курс", "курс"));
            if (c == null) return;
            // Student.markAttendance will validate schedule and whether attendance is opened for the specific lesson
            out("1. Present  0. Cancel");
            String p = scanner.nextLine().trim();
            if ("1".equals(p)) {
                s.markAttendance(c, true);
            } else {
                out("Cancelled.");
            }
        } else if ("2".equals(ch)) {
            List<Attendance> list = s.viewAttendanceList();
            if (list.isEmpty()) { out(s.t("No attendance records.", "Жазба жоқ.", "Нет записей.")); return; }
            out(s.t("Unsorted list:", "Тізім:", "Список:"));
            for (Attendance a : list) outObj(a);
            out(s.t("Sort after viewing? 1.Status 2.Date 0.No", "Сұрыптау? 1.Күй 2.Күні 0.Жоқ", "Сортировать? 1.Статус 2.Дата 0.Нет"));
            String so = scanner.nextLine().trim();
            if ("1".equals(so)) {
                list.sort((a,b) -> Boolean.compare(b.isPresent(), a.isPresent()));
            } else if ("2".equals(so)) {
                list.sort((a,b) -> a.getDate().compareTo(b.getDate()));
            } else return;
            out(s.t("Sorted list:", "Сұрыпталған:", "Отсортировано:"));
            for (Attendance a : list) outObj(a);
        } else out(s.t("Unknown.", "Белгісіз.", "Неизвестно."));
    }

    private static void putMarkMenu(Teacher t) {
        Course c = pickCourse(t.viewCourses(), "course");
        if (c == null) return;
        Student s = pickStudent();
        if (s == null) return;
        // Check if a mark already exists for this student-course
        Mark existing = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (existing != null) {
            out(t.t("Mark already exists for this student and course:", "Баға бар:", "Оценка уже существует:") + " " + existing);
            out("1. " + t.t("Overwrite", "Алмастыру", "Перезаписать") + "  0. " + t.t("Cancel", "Болдырмау", "Отмена"));
            String ch = scanner.nextLine().trim();
            if (!"1".equals(ch)) return;
            // Remove old mark
            DataStorage.getInstance().removeMark(existing.getMarkId());
        }
        double a1 = readDouble("1st attestation (0-30): ");
        double a2 = readDouble("2nd attestation (0-30): ");
        double fin = readDouble("Final (0-40): ");
        Mark mark = new Mark(a1, a2, fin, s, c);
        t.putMark(s, c, mark);
        out(t.t("Mark saved. Status: ", "Баға сақталды. Күй: ", "Оценка сохранена. Статус: ") + mark.getStatus()
                + " | " + t.t("Grade: ", "Баға: ", "Оценка: ") + mark.getLetterGrade());
        if (mark.isAttestationRetake()) {
            out(t.t("RETAKE: Attestation total < 29.5", "ҚАЙТА: Аттестация < 29.5", "ПЕРЕСДАЧА: Сумма аттестаций < 29.5"));
        }
        if (mark.isFinalRetake()) {
            out(t.t("RETAKE: Final exam < 9.5", "ҚАЙТА: Финал < 9.5", "ПЕРЕСДАЧА: Финал < 9.5"));
        }
        if (mark.isFX()) {
            out(t.t("FX: Final exam 9.5-19.5 (conditional fail)", "FX: Финал 9.5-19.5", "FX: Финал 9.5-19.5 (условный неуд)"));
        }
        if (s.isExpelled()) {
            out(t.t("STUDENT EXPELLED: more than 3 retakes!", "СТУДЕНТ ШЫҒАРЫЛДЫ: 3+ қайта!", "СТУДЕНТ ОТЧИСЛЕН: больше 3 пересдач!"));
        }
    }

    /** Menu to set individual attestation or final scores for an existing mark. */
    private static void setScoreMenu(Teacher t) {
        Course c = pickCourse(t.viewCourses(), "course");
        if (c == null) return;
        Student s = pickStudent();
        if (s == null) return;

        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark == null) {
            // Create a new empty mark
            mark = new Mark(0, 0, 0, s, c);
            DataStorage.getInstance().addMark(mark);
            out(t.t("New mark record created.", "Жаңа баға құрылды.", "Новая запись оценки создана."));
        }

        out(t.t("Current mark: ", "Ағымдағы баға: ", "Текущая оценка: ") + mark);
        out("1. " + t.t("Set 1st attestation (0-30)", "1-аттестация", "1-я аттестация (0-30)"));
        out("2. " + t.t("Set 2nd attestation (0-30)", "2-аттестация", "2-я аттестация (0-30)"));
        out("3. " + t.t("Set final exam (0-40)", "Финал", "Финальный экзамен (0-40)"));
        out("0. " + t.t("Cancel", "Болдырмау", "Отмена"));
        pr(t.t("Choice: ", "Таңдау: ", "Выбор: "));
        String ch = scanner.nextLine().trim();

        if (mark.isAttestationClosed() && ("1".equals(ch) || "2".equals(ch))) {
            out(t.t("Attestation is CLOSED. Cannot modify attestation scores.", "Аттестация ЖАБЫҚ. Өзгерту мүмкін емес.", "Аттестация ЗАКРЫТА. Нельзя изменить баллы аттестации."));
            return;
        }

        switch (ch) {
            case "1": {
                double val = readDouble(t.t("1st attestation (0-30): ", "1-аттестация (0-30): ", "1-я аттестация (0-30): "));
                t.setFirstAttestation(s, c, val);
                out(t.t("1st attestation set to ", "1-аттестация: ", "1-я аттестация: ") + String.format("%.1f", val));
                break;
            }
            case "2": {
                double val = readDouble(t.t("2nd attestation (0-30): ", "2-аттестация (0-30): ", "2-я аттестация (0-30): "));
                t.setSecondAttestation(s, c, val);
                out(t.t("2nd attestation set to ", "2-аттестация: ", "2-я аттестация: ") + String.format("%.1f", val));
                break;
            }
            case "3": {
                double val = readDouble(t.t("Final exam (0-40): ", "Финал (0-40): ", "Финальный экзамен (0-40): "));
                t.setFinalExam(s, c, val);
                out(t.t("Final exam set to ", "Финал: ", "Финал: ") + String.format("%.1f", val));
                // Check and display status
                if (mark.isFinalRetake()) {
                    out(t.t("RETAKE: Final < 9.5", "ҚАЙТА: Финал < 9.5", "ПЕРЕСДАЧА: Финал < 9.5"));
                } else if (mark.isFX()) {
                    out(t.t("FX: Final 9.5-19.5", "FX: Финал 9.5-19.5", "FX: Финал 9.5-19.5"));
                } else if (val >= 19.5) {
                    out(t.t("PASS", "ӨТТІ", "СДАЛ"));
                }
                break;
            }
            default:
                out(t.t("Cancelled.", "Болдырмады.", "Отменено."));
        }

        out(t.t("Mark: ", "Баға: ", "Оценка: ") + mark);
        if (s.isExpelled()) {
            out(t.t("STUDENT EXPELLED: more than 3 retakes!", "СТУДЕНТ ШЫҒАРЫЛДЫ!", "СТУДЕНТ ОТЧИСЛЕН: больше 3 пересдач!"));
        }
    }

    /** Menu to close attestation for a student on a course. */
    private static void closeAttestationMenu(Teacher t) {
        Course c = pickCourse(t.viewCourses(), "course");
        if (c == null) return;

        // Show all enrolled students with their attestation status
        List<Student> enrolled = new ArrayList<>();
        for (Enrollment e : storage.getEnrollments()) {
            if (e.getCourseId().equals(c.getCourseId())) {
                User u = storage.findUserById(e.getStudentId());
                if (u instanceof Student) enrolled.add((Student) u);
            }
        }
        if (enrolled.isEmpty()) { out(t.t("No students enrolled.", "Студент жоқ.", "Нет студентов.")); return; }

        out("\n--- " + t.t("Students & Attestation Status for ", "Студенттер: ", "Студенты и статус аттестации для ") + c.getName() + " ---");
        for (int i = 0; i < enrolled.size(); i++) {
            Student s = enrolled.get(i);
            Mark m = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
            String status;
            if (m == null) {
                status = t.t("No mark", "Баға жоқ", "Нет оценки");
            } else if (m.isAttestationClosed()) {
                status = t.t("CLOSED (total: ", "ЖАБЫҚ (жиын: ", "ЗАКРЫТА (сумма: ") + String.format("%.1f", m.getAttestationTotal()) + ")";
            } else {
                status = t.t("OPEN (total: ", "АШЫҚ (жиын: ", "ОТКРЫТА (сумма: ") + String.format("%.1f", m.getAttestationTotal()) + ")";
            }
            out((i + 1) + ". " + s.getFirstName() + " " + s.getLastName() + " — " + status);
        }

        out("\n1. " + t.t("Close attestation for one student", "Бір студент", "Закрыть для одного студента"));
        out("2. " + t.t("Close attestation for ALL students", "Барлық студенттер", "Закрыть для ВСЕХ студентов"));
        out("0. " + t.t("Back", "Артқа", "Назад"));
        pr(t.t("Choice: ", "Таңдау: ", "Выбор: "));
        String ch = scanner.nextLine().trim();

        if ("1".equals(ch)) {
            int si = readInt(t.t("Student number: ", "Нөмір: ", "Номер студента: ")) - 1;
            if (si < 0 || si >= enrolled.size()) { out(t.t("Invalid.", "Қате.", "Неверно.")); return; }
            Student target = enrolled.get(si);
            t.closeAttestation(target, c);
            Mark m = DataStorage.getInstance().findMarkForStudentCourse(target.getId(), c.getCourseId());
            out(t.t("Attestation closed for ", "Аттестация жабылды: ", "Аттестация закрыта для ") + target.getFirstName());
            if (m != null && m.isAttestationRetake()) {
                out(t.t("RETAKE: Attestation total " + String.format("%.1f", m.getAttestationTotal()) + " < 29.5",
                        "ҚАЙТА: " + String.format("%.1f", m.getAttestationTotal()) + " < 29.5",
                        "ПЕРЕСДАЧА: Сумма аттестаций " + String.format("%.1f", m.getAttestationTotal()) + " < 29.5"));
            }
            if (target.isExpelled()) {
                out(t.t("STUDENT EXPELLED: more than 3 retakes!", "СТУДЕНТ ШЫҒАРЫЛДЫ!", "СТУДЕНТ ОТЧИСЛЕН!"));
            }
        } else if ("2".equals(ch)) {
            int retakeCount = 0;
            for (Student s : enrolled) {
                t.closeAttestation(s, c);
                Mark m = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
                if (m != null && m.isAttestationRetake()) {
                    retakeCount++;
                    out(s.getFirstName() + " " + s.getLastName()
                            + " — " + t.t("RETAKE", "ҚАЙТА", "ПЕРЕСДАЧА")
                            + " (" + String.format("%.1f", m.getAttestationTotal()) + ")");
                }
                if (s.isExpelled()) {
                    out(s.getFirstName() + " " + s.getLastName()
                            + " — " + t.t("EXPELLED!", "ШЫҒАРЫЛДЫ!", "ОТЧИСЛЕН!"));
                }
            }
            out(t.t("Attestation closed for all " + enrolled.size() + " students. Retakes: " + retakeCount,
                    "Аттестация жабылды. Қайта: " + retakeCount,
                    "Аттестация закрыта для всех " + enrolled.size() + " студентов. Пересдач: " + retakeCount));
        }
    }

    private static void teacherJournalMenu(Teacher t) {
        List<Course> courses = t.viewCourses();
        if (courses.isEmpty()) { out(t.t("No courses assigned to you.", "Курс жоқ.", "Нет прикрепленных курсов.")); return; }
        Course c = pickCourse(courses, "course");
        if (c == null) return;

        // Get students enrolled in this course
        List<Student> enrolled = new ArrayList<>();
        for (Enrollment e : storage.getEnrollments()) {
            if (e.getCourseId().equals(c.getCourseId())) {
                User u = storage.findUserById(e.getStudentId());
                if (u instanceof Student) enrolled.add((Student) u);
            }
        }
        if (enrolled.isEmpty()) { out(t.t("No students enrolled in this course.", "Студент жоқ.", "Нет студентов на курсе.")); return; }

        while (true) {
            out("\n--- " + t.t("Select Student for ", "Студентті таңдаңыз: ", "Выбор студента для ") + c.getName() + " ---");
            for (int i = 0; i < enrolled.size(); i++) out((i + 1) + ". " + enrolled.get(i));
            out("0. " + t.t("Back", "Артқа", "Назад"));
            int si = readInt(t.t("Choice: ", "Таңдау: ", "Выбор: ")) - 1;
            if (si == -1) break;
            if (si < 0 || si >= enrolled.size()) { out(t.t("Invalid.", "Қате.", "Неверно.")); continue; }

            Student target = enrolled.get(si);
            studentJournalAction(t, target, c);
        }
    }

    private static void studentJournalAction(Teacher t, Student target, Course c) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
        while (true) {
            // Show attestation/final info
            Mark mark = DataStorage.getInstance().findMarkForStudentCourse(target.getId(), c.getCourseId());
            out("\n" + t.t("=== Attestation & Final for ", "=== Аттестация: ", "=== Аттестация и финал для ") + target.getFirstName() + " " + target.getLastName() + " ===");
            if (mark == null) {
                out(t.t("  (no attestation marks yet)", "  (аттестация жоқ)", "  (оценок аттестации пока нет)"));
            } else {
                out(t.t("  1st att: ", "  1-атт: ", "  1-я атт: ") + String.format("%.1f", mark.getFirstAttestation()) + "/30"
                        + "  |  " + t.t("2nd att: ", "2-атт: ", "2-я атт: ") + String.format("%.1f", mark.getSecondAttestation()) + "/30"
                        + "  |  " + t.t("final: ", "финал: ", "финал: ") + String.format("%.1f", mark.getFinalExam()) + "/40");
                out(t.t("  Total: ", "  Жиын: ", "  Итого: ") + String.format("%.1f", mark.getTotalScore()) + "/100"
                        + "  |  " + t.t("Grade: ", "Баға: ", "Оценка: ") + mark.getLetterGrade()
                        + "  |  " + t.t("Status: ", "Күй: ", "Статус: ") + mark.getStatus()
                        + (mark.isAttestationClosed() ? "  [" + t.t("ATTESTATION CLOSED", "АТТЕСТАЦИЯ ЖАБЫҚ", "АТТЕСТАЦИЯ ЗАКРЫТА") + "]" : ""));
            }

            // Show journal grade entries
            List<GradeEntry> grades = DataStorage.getInstance().getGradeEntries(target.getId(), c.getCourseId());
            out("\n" + t.t("=== Journal Grades for ", "=== Журнал: ", "=== Оценки журнала для ") + target.getFirstName() + " " + target.getLastName() + " ===");
            if (grades.isEmpty()) {
                out(t.t("  (no journal grades yet)", "  (баға жоқ)", "  (оценок в журнале пока нет)"));
            } else {
                for (int i = 0; i < grades.size(); i++) {
                    GradeEntry ge = grades.get(i);
                    out(String.format("%d. [%s]: %.1f (%s)", i + 1, sdf.format(ge.getDate()), ge.getValue(), ge.getDescription()));
                }
            }

            out("\n" + t.t("Options:", "Опциялар:", "Опции:"));
            out("1. " + t.t("Sort grades -> {Descending, Ascending}", "Сұрыптау", "Сортировка -> {По убыванию, Возрастанию}"));
            out("2. " + t.t("Select grade -> {Edit, Delete, Cancel}", "Бағаны таңдау", "Выбрать оценку -> {Изменить, Удалить, Отмена}"));
            out("3. " + t.t("Add new grade", "Баға қою", "Поставить оценку"));
            out("4. " + t.t("Set attestation/final score", "Аттестация/финал", "Выставить балл аттестации/финала"));
            out("5. " + t.t("Close attestation", "Аттестация жабу", "Закрыть аттестацию"));
            out("0. " + t.t("Back", "Артқа", "Назад"));
            pr(t.t("Choice: ", "Таңдау: ", "Выбор: "));
            String ch = scanner.nextLine().trim();

            if ("0".equals(ch)) break;
            switch (ch) {
                case "1":
                    if (grades.isEmpty()) { out(t.t("No grades to sort.", "Сұрыптау үшін баға жоқ.", "Нет оценок для сортировки.")); break; }
                    out("1. " + t.t("Descending (high to low)", "Төмендеу", "По убыванию"));
                    out("2. " + t.t("Ascending (low to high)", "Өсу", "По возрастанию"));
                    String sortCh = scanner.nextLine().trim();
                    if ("1".equals(sortCh)) grades.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
                    else if ("2".equals(sortCh)) grades.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));
                    if (!grades.isEmpty()) {
                        out("\n--- " + t.t("Sorted list:", "Сұрыпталған:", "Отсортировано:") + " ---");
                        for (GradeEntry ge : grades) {
                            out(String.format("[%s]: %.1f (%s)", sdf.format(ge.getDate()), ge.getValue(), ge.getDescription()));
                        }
                    }
                    break;
                case "2":
                    if (grades.isEmpty()) { out(t.t("No grades to select.", "Баға жоқ.", "Нет оценок для выбора.")); break; }
                    int idx = readInt(t.t("Select grade number: ", "Нөмір: ", "Номер оценки: ")) - 1;
                    if (idx >= 0 && idx < grades.size()) {
                        GradeEntry selected = grades.get(idx);
                        out("1. " + t.t("Edit", "Өзгерту", "Изменить"));
                        out("2. " + t.t("Delete", "Жою", "Удалить"));
                        out("0. " + t.t("Cancel", "Болдырмау", "Отмена"));
                        String action = scanner.nextLine().trim();
                        if ("1".equals(action)) {
                            double val = readDouble(t.t("New value: ", "Жаңа баға: ", "Новое значение: "));
                            String desc = readLine(t.t("New description: ", "Сипаттама: ", "Новое описание: "));
                            selected.setValue(val);
                            selected.setDescription(desc);
                            selected.setDate(new Date());
                            out(t.t("Updated.", "Өзгертілді.", "Обновлено."));
                        } else if ("2".equals(action)) {
                            t.removeGradeEntry(selected.getEntryId());
                            out(t.t("Deleted.", "Жойылды.", "Удалено."));
                        }
                    }
                    break;
                case "3":
                    double val = readDouble(t.t("Grade (0-100): ", "Баға: ", "Оценка: "));
                    String desc = readLine(t.t("Description: ", "Сипаттама: ", "Описание: "));
                    t.addGradeEntry(target, c, val, desc);
                    out(t.t("Added.", "Қосылды.", "Добавлено."));
                    break;
                case "4":
                    setScoreForStudent(t, target, c);
                    break;
                case "5":
                    if (mark != null && mark.isAttestationClosed()) {
                        out(t.t("Attestation already closed.", "Аттестация жабық.", "Аттестация уже закрыта."));
                    } else {
                        t.closeAttestation(target, c);
                        Mark updated = DataStorage.getInstance().findMarkForStudentCourse(target.getId(), c.getCourseId());
                        out(t.t("Attestation closed.", "Аттестация жабылды.", "Аттестация закрыта."));
                        if (updated != null && updated.isAttestationRetake()) {
                            out(t.t("RETAKE: Attestation total " + String.format("%.1f", updated.getAttestationTotal()) + " < 29.5",
                                    "ҚАЙТА: " + String.format("%.1f", updated.getAttestationTotal()) + " < 29.5",
                                    "ПЕРЕСДАЧА: Сумма аттестаций " + String.format("%.1f", updated.getAttestationTotal()) + " < 29.5"));
                        }
                        if (target.isExpelled()) {
                            out(t.t("STUDENT EXPELLED!", "СТУДЕНТ ШЫҒАРЫЛДЫ!", "СТУДЕНТ ОТЧИСЛЕН!"));
                        }
                    }
                    break;
            }
        }
    }

    private static void setScoreForStudent(Teacher t, Student s, Course c) {
        Mark mark = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), c.getCourseId());
        if (mark == null) {
            mark = new Mark(0, 0, 0, s, c);
            DataStorage.getInstance().addMark(mark);
        }
        out("1. " + t.t("Set 1st attestation (0-30)", "1-аттестация", "1-я аттестация (0-30)"));
        out("2. " + t.t("Set 2nd attestation (0-30)", "2-аттестация", "2-я аттестация (0-30)"));
        out("3. " + t.t("Set final exam (0-40)", "Финал", "Финальный экзамен (0-40)"));
        out("0. " + t.t("Cancel", "Болдырмау", "Отмена"));
        pr(t.t("Choice: ", "Таңдау: ", "Выбор: "));
        String ch = scanner.nextLine().trim();

        if (mark.isAttestationClosed() && ("1".equals(ch) || "2".equals(ch))) {
            out(t.t("Attestation CLOSED. Cannot modify.", "Аттестация ЖАБЫҚ.", "Аттестация ЗАКРЫТА. Нельзя изменить."));
            return;
        }

        switch (ch) {
            case "1": {
                double v = readDouble(t.t("1st attestation (0-30): ", "1-аттестация (0-30): ", "1-я аттестация (0-30): "));
                t.setFirstAttestation(s, c, v);
                out(t.t("Set to ", "Орнатылды: ", "Установлено: ") + String.format("%.1f", v));
                break;
            }
            case "2": {
                double v = readDouble(t.t("2nd attestation (0-30): ", "2-аттестация (0-30): ", "2-я аттестация (0-30): "));
                t.setSecondAttestation(s, c, v);
                out(t.t("Set to ", "Орнатылды: ", "Установлено: ") + String.format("%.1f", v));
                break;
            }
            case "3": {
                double v = readDouble(t.t("Final exam (0-40): ", "Финал (0-40): ", "Финальный экзамен (0-40): "));
                t.setFinalExam(s, c, v);
                if (mark.isFinalRetake()) out(t.t("RETAKE: Final < 9.5", "ҚАЙТА", "ПЕРЕСДАЧА: Финал < 9.5"));
                else if (mark.isFX()) out(t.t("FX: Final 9.5-19.5", "FX", "FX: Финал 9.5-19.5"));
                else out(t.t("Set to ", "Орнатылды: ", "Установлено: ") + String.format("%.1f", v));
                break;
            }
        }
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
        List<User> recipients = new ArrayList<>();
        for (User u : storage.getUsers()) {
            if (!u.equals(from)) recipients.add(u);
        }
        if (recipients.isEmpty()) { out("No other users."); return; }
        out("\n--- Send message to ---");
        for (int i = 0; i < recipients.size(); i++) {
            User u = recipients.get(i);
            String role = roleName(u);
            out((i + 1) + ". " + u.getFirstName() + " " + u.getLastName() + " (" + role + ")");
        }
        out("0. Cancel");
        int idx = readInt("Select recipient: ") - 1;
        if (idx < 0 || idx >= recipients.size()) { out("Cancelled."); return; }
        User to = recipients.get(idx);
        pr("Message: ");
        String msg = scanner.nextLine().trim();
        if (msg.isEmpty()) { out("Message cannot be empty."); return; }
        from.sendMessage(to, msg);
        out("Sent to " + to.getFirstName() + " " + to.getLastName() + ".");
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


    private static void manageCoursesMenu(Manager m) {
        while (true) {
            out("\n--- " + m.t("Course Management", "Курс басқару", "Управление курсами") + " ---");
            out("1. " + m.t("Create course with groups", "Курс қосу", "Создать курс с группами"));
            out("2. " + m.t("Delete course", "Курс жою", "Удалить курс"));
            out("3. " + m.t("Add group (lesson) to course", "Топ қосу", "Добавить группу в курс"));
            out("4. " + m.t("Remove group from course", "Топ жою", "Удалить группу из курса"));
            out("5. " + m.t("List all courses", "Курстар тізімі", "Список курсов"));
            out("0. " + m.t("Back", "Артқа", "Назад"));
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1": createCourseWithGroupsMenu(m); break;
                case "2": deleteCourseMenu(m); break;
                case "3": addGroupToCourseMenu(m); break;
                case "4": removeGroupFromCourseMenu(m); break;
                case "5": listCoursesWithGroupsMenu(m); break;
                case "0": return;
                default: out(m.t("Unknown.", "Белгісіз.", "Неизвестно."));
            }
        }
    }

    private static void createCourseWithGroupsMenu(Manager m) {
        pr("Course id: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) { out("Course id cannot be empty."); return; }
        if (DataStorage.getInstance().findCourseById(id) != null) {
            out(m.t("Course with this ID already exists.", "Бұндай курс бар.", "Курс с таким ID уже существует."));
            return;
        }
        pr("Course name: ");
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

        out(m.t("\nNow add groups (lessons) for this course. Leave topic blank to finish.",
                "\nОсы курсқа топтар қосыңыз. Бос қалдырсаңыз аяқталады.",
                "\nТеперь добавьте группы (занятия) для этого курса. Оставьте пустым для завершения."));
        while (true) {
            pr("\n  Group topic (e.g. 'Group A', 'Practice 1'): ");
            String topic = scanner.nextLine().trim();
            if (topic.isEmpty()) break;

            out("  Lesson type  1.LECTURE  2.PRACTICE:");
            String ltChoice = scanner.nextLine().trim();
            LessonType lt = "2".equals(ltChoice) ? LessonType.PRACTICE : LessonType.LECTURE;

            pr("  Day of week (MONDAY-SUNDAY): ");
            String dayStr = scanner.nextLine().trim().toUpperCase();
            java.time.DayOfWeek dow = null;
            try { dow = java.time.DayOfWeek.valueOf(dayStr); } catch (Exception e) {
                out("  Invalid day. Leaving unscheduled.");
            }

            java.time.LocalTime start = null, end = null;
            if (dow != null) {
                pr("  Start time (HH:mm, e.g. 09:00): ");
                try { start = java.time.LocalTime.parse(scanner.nextLine().trim()); } catch (Exception e) { out("  Invalid time."); }
                pr("  End time (HH:mm, e.g. 10:30): ");
                try { end = java.time.LocalTime.parse(scanner.nextLine().trim()); } catch (Exception e) { out("  Invalid time."); }
            }

            int capacity = readInt("  Capacity (default " + (lt == LessonType.LECTURE ? 75 : 25) + "): ");
            if (capacity <= 0) capacity = (lt == LessonType.LECTURE ? 75 : 25);

            String lessonId = "L" + System.currentTimeMillis() + "_" + course.getLessons().size();
            Lesson lesson = new Lesson(lessonId, topic, lt, dow, start, end, null, capacity);
            course.addLesson(lesson);
            out("  Group '" + topic + "' added (" + lt + ", cap=" + capacity + ").");
        }

        m.addCourseForRegistration(course, major, year);
        out(m.t("Course created with " + course.getLessons().size() + " group(s).",
                course.getLessons().size() + " топ қосылды.",
                "Курс создан с " + course.getLessons().size() + " группой(ами)."));
    }

    private static void deleteCourseMenu(Manager m) {
        List<Course> courses = DataStorage.getInstance().getCourses();
        if (courses.isEmpty()) { out(m.t("No courses.", "Курс жоқ.", "Нет курсов.")); return; }
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            out((i + 1) + ". " + c.getName() + " (" + c.getCourseId() + ", " + c.getCredits() + " cr, " + c.getMajor() + ")");
        }
        out("0. " + m.t("Cancel", "Болдырмау", "Отмена"));
        int idx = readInt("Select course to delete: ") - 1;
        if (idx < 0 || idx >= courses.size()) { out(m.t("Cancelled.", "Болдырмады.", "Отменено.")); return; }
        Course toDelete = courses.get(idx);
        if (confirm(m.t("Delete course " + toDelete.getName() + "? This removes all related enrollments, marks, grades.",
                toDelete.getName() + " курсын жою керек пе?",
                "Удалить курс " + toDelete.getName() + "? Это удалит все связанные записи, оценки."))) {
            DataStorage.getInstance().removeCourse(toDelete.getCourseId());
            out(m.t("Course deleted.", "Курс жойылды.", "Курс удалён."));
        } else {
            out(m.t("Cancelled.", "Болдырмады.", "Отменено."));
        }
    }

    private static void addGroupToCourseMenu(Manager m) {
        Course course = pickCourse();
        if (course == null) return;

        pr("Group topic (e.g. 'Group B', 'Lab 2'): ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) { out(m.t("Topic cannot be empty.", "Тақырып бос болмауы тиіс.", "Тема не может быть пустой.")); return; }

        out("Lesson type  1.LECTURE  2.PRACTICE:");
        String ltChoice = scanner.nextLine().trim();
        LessonType lt = "2".equals(ltChoice) ? LessonType.PRACTICE : LessonType.LECTURE;

        pr("Day of week (MONDAY-SUNDAY, or blank for unscheduled): ");
        String dayStr = scanner.nextLine().trim().toUpperCase();
        java.time.DayOfWeek dow = null;
        try { dow = java.time.DayOfWeek.valueOf(dayStr); } catch (Exception e) { /* unscheduled */ }

        java.time.LocalTime start = null, end = null;
        if (dow != null) {
            pr("Start time (HH:mm): ");
            try { start = java.time.LocalTime.parse(scanner.nextLine().trim()); } catch (Exception e) { out("Invalid time."); }
            pr("End time (HH:mm): ");
            try { end = java.time.LocalTime.parse(scanner.nextLine().trim()); } catch (Exception e) { out("Invalid time."); }
        }

        int capacity = readInt("Capacity (default " + (lt == LessonType.LECTURE ? 75 : 25) + "): ");
        if (capacity <= 0) capacity = (lt == LessonType.LECTURE ? 75 : 25);

        String lessonId = "L" + System.currentTimeMillis() + "_" + course.getLessons().size();
        Lesson lesson = new Lesson(lessonId, topic, lt, dow, start, end, null, capacity);
        course.addLesson(lesson);
        out(m.t("Group '" + topic + "' added to " + course.getName() + ".",
                "Топ қосылды.",
                "Группа '" + topic + "' добавлена в " + course.getName() + "."));
    }

    private static void removeGroupFromCourseMenu(Manager m) {
        Course course = pickCourse();
        if (course == null) return;

        List<Lesson> lessons = course.getLessons();
        if (lessons.isEmpty()) { out(m.t("No groups in this course.", "Топ жоқ.", "В этом курсе нет групп.")); return; }
        for (int i = 0; i < lessons.size(); i++) {
            Lesson l = lessons.get(i);
            out((i + 1) + ". " + l.getTopic() + " (" + l.getLessonType() + ", " + l.getEnrolledStudentsCount() + "/" + l.getCapacity() + ")");
        }
        out("0. Cancel");
        int idx = readInt("Select group to remove: ") - 1;
        if (idx < 0 || idx >= lessons.size()) { out(m.t("Cancelled.", "Болдырмады.", "Отменено.")); return; }
        Lesson toRemove = lessons.get(idx);
        course.removeLessonById(toRemove.getLessonId());
        out(m.t("Group removed.", "Топ жойылды.", "Группа удалена."));
    }

    private static void listCoursesWithGroupsMenu(Manager m) {
        List<Course> courses = DataStorage.getInstance().getCourses();
        if (courses.isEmpty()) { out(m.t("No courses.", "Курс жоқ.", "Нет курсов.")); return; }
        for (Course c : courses) {
            out("\n" + c.getName() + " (" + c.getCourseId() + ") — " + c.getCredits() + " cr, " + c.getCourseType() + ", " + c.getMajor() + ", year " + c.getStudyYear());
            List<Lesson> lessons = c.getLessons();
            if (lessons.isEmpty()) {
                out("  (no groups)");
            } else {
                for (Lesson l : lessons) {
                    String sched = (l.getDayOfWeek() != null) ? " " + l.getDayOfWeek() + " " + l.getStartTime() + "-" + l.getEndTime() : "";
                    out("  - " + l.getTopic() + " [" + l.getLessonType() + "] enrolled=" + l.getEnrolledStudentsCount() + "/" + l.getCapacity() + sched);
                }
            }
        }
    }

    private static Course pickCourse() {
        List<Course> courses = DataStorage.getInstance().getCourses();
        if (courses.isEmpty()) { out("No courses."); return null; }
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            out((i + 1) + ". " + c.getName() + " (" + c.getCourseId() + ")");
        }
        out("0. Cancel");
        int idx = readInt("Select course: ") - 1;
        if (idx < 0 || idx >= courses.size()) return null;
        return courses.get(idx);
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
        // Topic must be one of allowed enum values
        Topic[] topics = Topic.values();
        out("Select topic (Research will be pinned):");
        for (int i = 0; i < topics.length; i++) out((i+1) + ". " + topics[i]);
        int ti = readInt("Choice: ");
        if (ti <= 0 || ti > topics.length) { out("Invalid topic choice. Cancelling."); return; }
        String topic = topics[ti-1].toString();
        m.publishNews(title, content, topic);
        out("Published.");
    }

    private static void publishToJournalMenu() {
        List<Journal> journals = storage.getJournals();
        if (journals == null) journals = new ArrayList<>();
        out("--- Journals ---");
        for (int i = 0; i < journals.size(); i++) out((i + 1) + ". " + journals.get(i).getName());
        out((journals.size() + 1) + ". Create new journal");
        pr("Select journal (0=cancel): ");
        int choice = readInt("");
        if (choice == 0) return;
        Journal j = null;
        if (choice == journals.size() + 1) {
            pr("New journal name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) { out("Name cannot be empty."); return; }
            j = new Journal(name);
            storage.addJournal(j);
        } else if (choice >= 1 && choice <= journals.size()) {
            j = journals.get(choice - 1);
        } else {
            out("Invalid selection.");
            return;
        }
        pr("Paper title for journal: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) { out("Title cannot be empty."); return; }
        pr("Pages (enter for default 15): ");
        String ps = scanner.nextLine().trim();
        int pages = 15;
        try { if (!ps.isEmpty()) pages = Integer.parseInt(ps); } catch (NumberFormatException ignored) {}
        ResearchPaper p = new ResearchPaper(title, j.getName(), pages, new Date());
        if (currentUser != null) {
            p.addAuthor(currentUser.getFirstName() + " " + currentUser.getLastName());
            p.setOwnerId(currentUser.getId());
        }
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

    // Show only general/public news for guests (pre-login view).
    private static void viewPublicNews() {
        List<News> all = storage.getNews();
        if (all == null || all.isEmpty()) { out("No public news."); return; }

        // collect distinct topics
        List<String> topics = new ArrayList<>();
        for (News n : all) {
            String t = n.getTopic() == null ? "General" : n.getTopic().trim();
            if (t.isEmpty()) t = "General";
            if (!topics.contains(t)) topics.add(t);
        }

        out(txt(null, "Select topic to view (0=cancel):", "Тақырыпты таңдаңыз (0=болдырмау):", "Выберите тему (0=отмена):"));
        for (int i = 0; i < topics.size(); i++) out((i + 1) + ". " + topics.get(i));
        out((topics.size() + 1) + ". " + txt(null, "All topics", "Барлық тақырыптар", "Все темы"));
        int choice = readInt(txt(null, "Choice: ", "Таңдау: ", "Выбор: "));
        if (choice == 0) return;

        List<News> filtered = new ArrayList<>();
        if (choice == topics.size() + 1) {
            filtered.addAll(all);
        } else if (choice >= 1 && choice <= topics.size()) {
            String sel = topics.get(choice - 1);
            for (News n : all) {
                String t = n.getTopic() == null ? "General" : n.getTopic().trim();
                if (t.isEmpty()) t = "General";
                if (t.equalsIgnoreCase(sel)) filtered.add(n);
            }
        } else {
            out("Invalid selection.");
            return;
        }

        if (filtered.isEmpty()) { out("No news for selected topic."); return; }
        Student guest = new Student("GUEST", "Guest", "", "guest@kbtu.kz", "", guestLanguage, "GUEST");
        showNewsPaged(filtered, guest);
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

    private static void viewMyMessages(User u) {
        List<Message> allMsgs = DataStorage.getInstance().getMessages();
        // Gather all messages involving this user (sent or received)
        List<Message> mine = new ArrayList<>();
        for (Message m : allMsgs) {
            User sender = m.getSender();
            User receiver = m.getReceiver();
            if ((sender != null && sender.equals(u)) || (receiver != null && receiver.equals(u))) {
                mine.add(m);
            }
        }
        if (mine.isEmpty()) { out(u.t("No messages.", "Хабарлама жоқ.", "Сообщений нет.")); return; }

        // Build a list of conversation partners
        List<User> partners = new ArrayList<>();
        for (Message m : mine) {
            User partner = m.getSender().equals(u) ? m.getReceiver() : m.getSender();
            if (partner != null && !partners.contains(partner)) partners.add(partner);
        }

        while (true) {
            out("\n--- " + u.t("Messages", "Хабарламалар", "Сообщения") + " ---");
            for (int i = 0; i < partners.size(); i++) {
                User p = partners.get(i);
                long unread = mine.stream()
                    .filter(m -> m.getReceiver() != null && m.getReceiver().equals(u) && m.getSender().equals(p))
                    .count();
                out((i + 1) + ". " + p.getFirstName() + " " + p.getLastName() + " (" + roleName(p) + ") [" + unread + " msg]");
            }
            out("0. " + u.t("Back", "Артқа", "Назад"));
            int idx = readInt(u.t("Select person: ", "Таңдаңыз: ", "Выберите: ")) - 1;
            if (idx < 0 || idx >= partners.size()) return;

            User partner = partners.get(idx);
            List<Message> conv = new ArrayList<>();
            for (Message m : mine) {
                if ((m.getSender().equals(u) && m.getReceiver().equals(partner))
                 || (m.getSender().equals(partner) && m.getReceiver().equals(u))) {
                    conv.add(m);
                }
            }
            out("\n--- Chat with " + partner.getFirstName() + " " + partner.getLastName() + " ---");
            for (Message m : conv) {
                String who = m.getSender().equals(u) ? "You" : m.getSender().getFirstName();
                out("  [" + who + "] " + m.getContent());
            }
            out("");
        }
    }

    private static void changePasswordMenu(User u) {
        pr(u.t("Current password: ", "Ағымдағы пароль: ", "Текущий пароль: "));
        String cur = scanner.nextLine().trim();
        if (!u.checkPassword(cur)) { out(u.t("Incorrect current password.", "Қате пароль.", "Неверный пароль.")); return; }
        pr(u.t("New password: ", "Жаңа пароль: ", "Новый пароль: "));
        String np1 = scanner.nextLine().trim();
        if (np1.isEmpty()) { out(u.t("Password cannot be empty.", "Пароль бос болмауы тиіс.", "Пароль не может быть пустым.")); return; }
        pr(u.t("Confirm new password: ", "Қайта енгізу: ", "Подтвердите пароль: "));
        String np2 = scanner.nextLine().trim();
        if (!np1.equals(np2)) { out(u.t("Passwords do not match.", "Пароль сәйкес емес.", "Пароли не совпадают.")); return; }
        u.password = np1;
        DataStorage.getInstance().addLog(new Log(java.util.UUID.randomUUID().toString(), u.getId(), "Password changed by user", new java.util.Date()));
        out(u.t("Password changed.", "Пароль өзгертілді.", "Пароль изменён."));
    }

    private static void adminAddUser(Admin a) {
        out("Type: 1.Student  2.Teacher  3.Graduate  4.Manager  5.TechSupport (0=cancel)");
        String type = scanner.nextLine().trim();
        if (type == null || type.isEmpty() || "0".equals(type) || "q".equalsIgnoreCase(type)) { out("Cancelled."); return; }

        pr("First name (0=cancel): "); String fn = scanner.nextLine().trim();
        if (fn.isEmpty() || "0".equals(fn) || "q".equalsIgnoreCase(fn)) { out("Cancelled."); return; }
        pr("Last name (0=cancel): "); String ln = scanner.nextLine().trim();
        if (ln.isEmpty() || "0".equals(ln) || "q".equalsIgnoreCase(ln)) { out("Cancelled."); return; }
        pr("Email (0=cancel): "); String email = scanner.nextLine().trim();
        if (email.isEmpty() || "0".equals(email) || "q".equalsIgnoreCase(email)) { out("Cancelled."); return; }
        // basic uniqueness check
        if (DataStorage.getInstance().findUserByEmail(email) != null) { out("Email already in use. Aborting."); return; }
        pr("Password (0=cancel): "); String pass = scanner.nextLine().trim();
        if (pass.isEmpty() || "0".equals(pass) || "q".equalsIgnoreCase(pass)) { out("Cancelled."); return; }
        Language lang = a.getLanguage();

        String uid = java.util.UUID.randomUUID().toString();
        switch (type) {
            case "2":
                a.addUser(new Teacher(uid, fn, ln, email, pass, lang, "EMP" + uid.substring(0,5), 50000,
                        "General", TeacherPosition.LECTOR));
                break;
            case "3":
                a.addUser(new GraduateStudent(uid, fn, ln, email, pass, lang, uid));
                break;
            case "4":
                a.addUser(new Manager(uid, fn, ln, email, pass, lang, "EMP" + uid.substring(0,5), 55000,
                        "Office", ManagerType.DEPARTMENT));
                break;
            case "5":
                a.addUser(new TechSupportSpecialist(uid, fn, ln, email, pass, lang, "EMP" + uid.substring(0,5), 40000, "IT"));
                break;
            default:
                // Student
                out("Select faculty (0=cancel):");
                Faculty[] facs = Faculty.values();
                for (int i = 0; i < facs.length; i++) out((i+1) + ". " + facs[i]);
                int fi = readInt("Choice: ") - 1;
                if (fi == -1) { out("Cancelled."); return; }
                if (fi < 0 || fi >= facs.length) { out("Invalid faculty. Aborting."); return; }
                int sy = readInt("Study year (1-7, 0=cancel): ");
                if (sy == 0) { out("Cancelled."); return; }
                if (sy < 1 || sy > 7) { out("Invalid study year. Aborting."); return; }
                Student st = new Student(uid, fn, ln, email, pass, lang, uid);
                st.setFaculty(facs[fi].name());
                st.setStudyYear(sy);
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
        pr("New password (empty=skip): ");
        String np = scanner.nextLine().trim();
        if (!np.isEmpty()) {
            u.password = np;
            out("Password updated for id " + u.getId());
            DataStorage.getInstance().addLog(new Log(java.util.UUID.randomUUID().toString(), a.getId(), "Admin set new password for user " + u.getId(), new java.util.Date()));
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

    @SuppressWarnings("unused")
    private static Request pickRequest(List<Request> list) {
        out("\n--- Requests ---");
        for (int i = 0; i < list.size(); i++) out((i + 1) + ". " + list.get(i));
        int idx = readInt("Select request (0=cancel): ") - 1;
        if (idx == -1) return null;
        if (idx >= 0 && idx < list.size()) return list.get(idx);
        out("Invalid selection.");
        return null;
    }

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

    private static void switchLanguage() {
        out(currentUser.t("1.KZ  2.EN  3.RU", "1.KZ  2.EN  3.RU", "1.KZ  2.EN  3.RU"));
        String c = scanner.nextLine().trim();
        if ("1".equals(c)) currentUser.setLanguage(Language.KZ);
        else if ("2".equals(c)) currentUser.setLanguage(Language.EN);
        else if ("3".equals(c)) currentUser.setLanguage(Language.RU);
        out("Language: " + currentUser.getLanguage());
    }

    private static void switchGuestLanguage() {
        out("1.KZ  2.EN  3.RU");
        String c = scanner.nextLine().trim();
        if ("1".equals(c)) guestLanguage = Language.KZ;
        else if ("2".equals(c)) guestLanguage = Language.EN;
        else if ("3".equals(c)) guestLanguage = Language.RU;
        out("Language: " + guestLanguage);
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

    private static void viewExpelledStudents(User u) {
        List<Student> expelled = DataStorage.getInstance().getExpelledStudents();
        if (expelled.isEmpty()) {
            out(u.t("No expelled students.", "Шығарылған студент жоқ.", "Нет отчисленных студентов."));
            return;
        }
        out("\n--- " + u.t("Expelled Students (>3 retakes)", "Шығарылған студенттер", "Отчисленные студенты (>3 пересдач)") + " ---");
        for (Student s : expelled) {
            out(s.getFirstName() + " " + s.getLastName()
                    + " (" + s.getStudentId() + ") — "
                    + u.t("retakes: ", "қайта: ", "пересдач: ") + s.getRetakeCount()
                    + " | " + u.t("courses: ", "курстар: ", "курсы: ") + String.join(", ", s.getRetakeCourseIds()));
        }
    }

    private static void viewRetakeCourses(Student s) {
        List<String> retakeIds = s.getRetakeCourseIds();
        if (retakeIds.isEmpty()) {
            out(s.t("You have no retake courses.", "Қайта курстар жоқ.", "У вас нет курсов на пересдачу."));
            return;
        }
        out("\n--- " + s.t("Your Retake Courses", "Қайта курстар", "Курсы на пересдачу") + " ---");
        for (String cid : retakeIds) {
            Course c = DataStorage.getInstance().findCourseById(cid);
            String name = c != null ? c.getName() : cid;
            Mark m = DataStorage.getInstance().findMarkForStudentCourse(s.getId(), cid);
            String reason = "";
            if (m != null) {
                if (m.isAttestationRetake()) {
                    reason = s.t("Attestation total ", "Аттестация ", "Сумма аттестаций ") + String.format("%.1f", m.getAttestationTotal()) + " < 29.5";
                } else if (m.isFinalRetake()) {
                    reason = s.t("Final ", "Финал ", "Финал ") + String.format("%.1f", m.getFinalExam()) + " < 9.5";
                }
            }
            out("  " + name + " (" + cid + ") — " + reason);
        }
        out(s.t("Total retakes: ", "Қайта: ", "Всего пересдач: ") + retakeIds.size() + "/3"
                + (s.isExpelled() ? " — " + s.t("EXPELLED", "ШЫҒАРЫЛДЫ", "ОТЧИСЛЕН") : ""));
    }

    private static void initSampleData() {
        Admin admin = new Admin("ADM001", "Alice", "Johnson", "admin@kbtu.kz", "admin",
                Language.EN, "EMP001", 60000, "Administration");
        storage.addUser(admin);

        Teacher professor = new Teacher("TCH001", "Bob", "Smith", "teacher@kbtu.kz", "teacher",
                Language.EN, "EMP002", 70000, "Computer Science", TeacherPosition.PROFESSOR);
        storage.addUser(professor);

        Manager manager = new Manager("MGR001", "Charlie", "Brown", "manager@kbtu.kz", "manager",
                Language.EN, "EMP003", 55000, "Academic Affairs", ManagerType.DEPARTMENT);
        storage.addUser(manager);

        Student student = new Student("STU001", "David", "Wilson", "student@kbtu.kz", "student",
                Language.EN, "STU001");
        student.setGpa(3.85);
        student.setFaculty("SITE");
        student.setStudyYear(2);
        storage.addUser(student);

        Student student2 = new Student("STU003", "Anna", "Lee", "anna@kbtu.kz", "anna",
                Language.EN, "STU003");
        student2.setGpa(3.50);
        student2.setFaculty("Business");
        student2.setStudyYear(3);
        storage.addUser(student2);

        Student student3 = new Student("STU004", "Mark", "Taylor", "mark@kbtu.kz", "mark",
                Language.EN, "STU004");
        student3.setGpa(2.90);
        student3.setFaculty("SITE");
        student3.setStudyYear(1);
        storage.addUser(student3);

        Student student4 = new Student("STU005", "Sara", "Khan", "sara@kbtu.kz", "sara",
                Language.EN, "STU005");
        student4.setGpa(3.70);
        student4.setFaculty("Law");
        student4.setStudyYear(4);
        student4.setGraduated(true);
        storage.addUser(student4);

        GraduateStudent graduate = new GraduateStudent("STU002", "Emma", "Davis", "graduate@kbtu.kz",
                "graduate", Language.EN, "STU002");
        graduate.setGpa(3.95);
        graduate.setFaculty("SITE");
        graduate.setStudyYear(5);
        storage.addUser(graduate);

        TechSupportSpecialist tech = new TechSupportSpecialist("TEC001", "Frank", "Miller",
            "tech@kbtu.kz", "tech", Language.EN, "EMP004", 40000, "IT");
        storage.addUser(tech);

        Course javaCourse = new Course("CMP101", "Advanced Java", 3, CourseType.MAJOR, "SITE", 2);
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        javaCourse.addLesson(new Lesson("L01", "OOP", LessonType.LECTURE, today, LocalTime.of(10, 0), LocalTime.of(11, 30)));
        javaCourse.addLesson(new Lesson("L02", "Patterns", LessonType.PRACTICE, today, LocalTime.of(14, 0), LocalTime.of(15, 30)));
        professor.manageCourse(javaCourse);
        storage.addCourse(javaCourse);

        Course algoCourse = new Course("CMP201", "Algorithms", 4, CourseType.MAJOR, "SITE", 3);
        professor.manageCourse(algoCourse);
        storage.addCourse(algoCourse);

        manager.approveRegistration(student, javaCourse);
        professor.putMark(student, javaCourse, new Mark(95, 92, 98, student, javaCourse));

        TeacherResearcher resProf = new TeacherResearcher(professor);
        String[] journals = new String[] {"IEEE", "ACM", "Springer", "Elsevier"};
        for (int i = 0; i < 15; i++) {
            int pages = 5 + (i % 12); // vary pages
            String journalName = journals[i % journals.length];
            Date pubDate = new Date(System.currentTimeMillis() - (long) i * 30L * 24L * 60L * 60L * 1000L);
            ResearchPaper p = new ResearchPaper("ML Paper " + (i + 1), journalName, pages, pubDate);
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
        out("  admin@kbtu.kz     / admin");
        out("  teacher@kbtu.kz   / teacher  (Professor, researcher)");
        out("  manager@kbtu.kz   / manager");
        out("  student@kbtu.kz   / student  (SITE, year 2)");
        out("  graduate@kbtu.kz  / graduate (SITE, year 5, researcher)");
        out("  tech@kbtu.kz      / tech");
        out("  anna@kbtu.kz      / anna     (Business, year 3)");
        out("  mark@kbtu.kz      / mark     (SITE, year 1)");
        out("  sara@kbtu.kz      / sara     (Law, year 4, graduated)");
    }

    private static void printHeader() {
        out("");
        out("KBTU WSP ");
        out("");
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
        if (u == null) {
            if (guestLanguage == Language.KZ) return kz;
            if (guestLanguage == Language.RU) return ru;
            return en;
        }
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

    private static void pause() {
        out("");
        pr("↵ Press Enter to continue... ");
        scanner.nextLine();
    }
}