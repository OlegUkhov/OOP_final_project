import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DataStorage implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + "/storage.ser";
    private static DataStorage instance;

    private List<User> users;
    private List<Course> courses;
    private List<Enrollment> enrollments;
    private List<Mark> marks;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;
    private List<Journal> journals;
    private List<Log> logs;
    private List<News> news;
    private List<Message> messages;
    private List<Complaint> complaints;
    private List<Attendance> attendances;
    // track open attendance sessions as "courseId|lessonId"
    private List<String> openAttendanceSessions;
    private List<StudentOrganization> organizations;
    private List<String> diplomaPaperIds;

    private DataStorage() {
        users = new ArrayList<>();
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        marks = new ArrayList<>();
        papers = new ArrayList<>();
        projects = new ArrayList<>();
        journals = new ArrayList<>();
        logs = new ArrayList<>();
        news = new ArrayList<>();
        messages = new ArrayList<>();
        complaints = new ArrayList<>();
        attendances = new ArrayList<>();
        openAttendanceSessions = new ArrayList<>();
        organizations = new ArrayList<>();
        diplomaPaperIds = new ArrayList<>();
    }

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    // ---------- business methods (unchanged) ----------

    public void addUser(User user) {
        if (user != null && !users.contains(user)) users.add(user);
    }

    public void removeUser(String userId) {
        if (userId == null) return;
        users.removeIf(u -> u.getId().equals(userId));
        enrollments.removeIf(e -> e.getStudentId().equals(userId));
        marks.removeIf(m -> m.getStudent() != null && m.getStudent().getId().equals(userId));
    }

    public void updateUser(User user) {
        if (user == null) return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return;
            }
        }
    }

    public User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : users) {
            if (email.equalsIgnoreCase(u.getEmail())) return u;
        }
        return null;
    }

    public User findUserById(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) courses.add(course);
    }

    public Course findCourseById(String courseId) {
        for (Course c : courses) {
            if (c.getCourseId().equals(courseId)) return c;
        }
        return null;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public List<Course> getCoursesForTeacher(Teacher teacher) {
        List<Course> result = new ArrayList<>();
        if (teacher == null) return result;
        for (Course c : courses) {
            if (c.getTeachers().contains(teacher)) result.add(c);
        }
        return result;
    }

    public List<Course> getCoursesForStudent(Student student) {
        List<Course> result = new ArrayList<>();
        if (student == null) return result;
        for (Enrollment e : enrollments) {
            if (e.getStudentId().equals(student.getId())) {
                Course c = findCourseById(e.getCourseId());
                if (c != null) result.add(c);
            }
        }
        return result;
    }

    public void enrollStudent(Student student, Course course, Lesson lesson) {
        if (student == null || course == null || lesson == null) return;
        // Check for schedule conflicts with already enrolled courses
        for (Enrollment en : enrollments) {
            if (!en.getStudentId().equals(student.getId())) continue;
            Course existing = findCourseById(en.getCourseId());
            if (existing == null) continue;
            // Find the lesson for this enrollment
            Lesson existingLesson = null;
            for (Lesson l : existing.getLessons()) {
                if (l.getLessonId().equals(en.getLessonId())) {
                    existingLesson = l;
                    break;
                }
            }
            if (existingLesson == null) continue;
            // Check time overlap
            if (lesson.getDayOfWeek() != null && lesson.getStartTime() != null && lesson.getEndTime() != null
                    && existingLesson.getDayOfWeek() != null && existingLesson.getStartTime() != null && existingLesson.getEndTime() != null) {
                if (lesson.getDayOfWeek() == existingLesson.getDayOfWeek()) {
                    if (!lesson.getEndTime().isBefore(existingLesson.getStartTime()) && !existingLesson.getEndTime().isBefore(lesson.getStartTime())) {
                        System.out.println("Schedule conflict: cannot enroll student " + student.getId()
                                + " into course " + course.getCourseId() + " on this time because it overlaps with " + existing.getCourseId());
                        return;
                    }
                }
            }
        }
        Enrollment e = new Enrollment(student.getId(), course.getCourseId(), lesson.getLessonId());
        if (!enrollments.contains(e)) enrollments.add(e);
    }

    public boolean isEnrolled(Student student, Course course) {
        if (student == null || course == null) return false;
        // Check if student is enrolled in ANY lesson of this course
        for (Enrollment e : enrollments) {
            if (e.getStudentId().equals(student.getId()) && e.getCourseId().equals(course.getCourseId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnrolledForLesson(Student student, Course course, Lesson lesson) {
        if (student == null || course == null || lesson == null) return false;
        return enrollments.contains(new Enrollment(student.getId(), course.getCourseId(), lesson.getLessonId()));
    }

    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public void addMark(Mark mark) {
        if (mark != null && !marks.contains(mark)) marks.add(mark);
    }

    public List<Mark> getMarksForStudent(Student student) {
        List<Mark> result = new ArrayList<>();
        if (student == null) return result;
        for (Mark m : marks) {
            if (m.getStudent() != null && m.getStudent().getId().equals(student.getId())) {
                result.add(m);
            }
        }
        return result;
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) papers.add(paper);
    }

    public List<ResearchPaper> getPapersByOwner(String ownerId) {
        List<ResearchPaper> result = new ArrayList<>();
        if (ownerId == null) return result;
        for (ResearchPaper p : papers) {
            if (ownerId.equals(p.getOwnerId())) result.add(p);
        }
        return result;
    }

    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    public void addProject(ResearchProject project) {
        if (project != null && !projects.contains(project)) {
            projects.add(project);
        }
    }

    public List<ResearchProject> getProjects() {
        return new ArrayList<>(projects);
    }

    public void addDiplomaPaper(String studentId, ResearchPaper paper) {
        if (studentId != null && paper != null) {
            addPaper(paper);
            String key = studentId + ":" + paper.getPaperId();
            if (!diplomaPaperIds.contains(key)) diplomaPaperIds.add(key);
        }
    }

    public List<ResearchPaper> getDiplomaPapers(String studentId) {
        List<ResearchPaper> result = new ArrayList<>();
        for (String key : diplomaPaperIds) {
            if (key.startsWith(studentId + ":")) {
                String paperId = key.substring(studentId.length() + 1);
                for (ResearchPaper p : papers) {
                    if (p.getPaperId().equals(paperId)) {
                        result.add(p);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void addJournal(Journal journal) {
        if (journal != null && !journals.contains(journal)) journals.add(journal);
    }

    public List<Journal> getJournals() {
        return new ArrayList<>(journals);
    }

    public void addNews(News n) {
        if (n != null && !news.contains(n)) news.add(n);
    }

    public void publishNews(News item) {
        if (item == null) return;
        addNews(item);
        if ("Research".equals(item.getTopic())) item.pin();
        for (User u : users) u.onNewsReceived(item);
    }

    public List<News> getNews() {
        List<News> sorted = new ArrayList<>(news);
        sorted.sort((a, b) -> Boolean.compare(b.isPinned(), a.isPinned()));
        return sorted;
    }

    public void printNewsFeed(User viewer) {
        System.out.println(viewer.t("=== News Feed ===", "=== Жаңалықтар ===", "=== Новости ==="));
        List<News> list = getNews();
        if (list.isEmpty()) {
            System.out.println(viewer.t("  (no news yet)", "  (жаңалық жоқ)", "  (новостей пока нет)"));
            return;
        }
        int num = 1;
        for (News n : list) {
            System.out.println("--- " + viewer.t("News #", "Жаңалық #", "Новость #") + num++ + " ---");
            n.print(viewer);
        }
    }

    public void addMessage(Message message) {
        if (message != null && !messages.contains(message)) messages.add(message);
    }

    public void addAttendance(Attendance a) {
        if (a != null) attendances.add(a);
    }

    public java.util.List<Attendance> getAttendancesForCourse(String courseId) {
        java.util.List<Attendance> res = new java.util.ArrayList<>();
        if (courseId == null) return res;
        for (Attendance a : attendances) if (courseId.equals(a.getCourseId())) res.add(a);
        return res;
    }

    public java.util.List<Attendance> getAttendancesForStudent(String studentId) {
        java.util.List<Attendance> res = new java.util.ArrayList<>();
        if (studentId == null) return res;
        for (Attendance a : attendances) if (studentId.equals(a.getStudentId())) res.add(a);
        return res;
    }

    public java.util.List<Attendance> getAttendancesForSession(String courseId, String lessonId) {
        java.util.List<Attendance> res = new java.util.ArrayList<>();
        if (courseId == null || lessonId == null) return res;
        for (Attendance a : attendances) if (courseId.equals(a.getCourseId()) && lessonId.equals(a.getLessonId())) res.add(a);
        return res;
    }

    public void openAttendance(String courseId, String lessonId) {
        if (courseId == null || lessonId == null) return;
        String key = courseId + "|" + lessonId;
        if (!openAttendanceSessions.contains(key)) openAttendanceSessions.add(key);
    }

    public void closeAttendance(String courseId, String lessonId) {
        if (courseId == null || lessonId == null) return;
        String key = courseId + "|" + lessonId;
        openAttendanceSessions.remove(key);
    }

    public boolean isAttendanceOpen(String courseId, String lessonId) {
        if (courseId == null || lessonId == null) return false;
        String key = courseId + "|" + lessonId;
        return openAttendanceSessions.contains(key);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public List<Request> getRequests() {
        List<Request> result = new ArrayList<>();
        for (Message m : messages) {
            if (m instanceof Request) result.add((Request) m);
        }
        return result;
    }

    public void addComplaint(Complaint complaint) {
        if (complaint != null && !complaints.contains(complaint)) complaints.add(complaint);
    }

    public void addOrganization(StudentOrganization org) {
        if (org != null && !organizations.contains(org)) organizations.add(org);
    }

    public List<StudentOrganization> getOrganizations() {
        return new ArrayList<>(organizations);
    }

    public void addLog(Log log) {
        if (log != null && !logs.contains(log)) logs.add(log);
    }

    public List<Log> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<Researcher> getAllResearchers() {
        List<Researcher> researchers = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Teacher) {
                Teacher t = (Teacher) u;
                if (t.getPosition() == TeacherPosition.PROFESSOR || t.isResearcher()) {
                    researchers.add(new TeacherResearcher(t));
                }
            } else if (u instanceof GraduateStudent) {
                researchers.add(new StudentResearcher((Student) u));
            } else if (u instanceof Student && ((Student) u).isResearcher()) {
                researchers.add(new StudentResearcher((Student) u));
            }
        }
        return researchers;
    }

    public Researcher getTopCitedResearcher() {
        List<Researcher> researchers = getAllResearchers();
        if (researchers.isEmpty()) return null;
        Researcher top = researchers.get(0);
        int maxH = top.calculateHIndex();
        for (Researcher r : researchers) {
            int h = r.calculateHIndex();
            if (h > maxH) { maxH = h; top = r; }
        }
        return top;
    }

    public void printAllResearchersPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> all = new ArrayList<>(papers);
        if (comparator != null) all.sort(comparator);
        System.out.println("=== All University Research Papers ===");
        for (ResearchPaper p : all) System.out.println(p);
    }


    private void copyFrom(DataStorage other) {
        if (other == null) return;
        this.users = other.users != null ? other.users : new ArrayList<>();
        this.courses = other.courses != null ? other.courses : new ArrayList<>();
        this.enrollments = other.enrollments != null ? other.enrollments : new ArrayList<>();
        this.marks = other.marks != null ? other.marks : new ArrayList<>();
        this.papers = other.papers != null ? other.papers : new ArrayList<>();
        this.projects = other.projects != null ? other.projects : new ArrayList<>();
        this.journals = other.journals != null ? other.journals : new ArrayList<>();
        this.logs = other.logs != null ? other.logs : new ArrayList<>();
        this.news = other.news != null ? other.news : new ArrayList<>();
        this.messages = other.messages != null ? other.messages : new ArrayList<>();
        this.complaints = other.complaints != null ? other.complaints : new ArrayList<>();
        this.attendances = other.attendances != null ? other.attendances : new ArrayList<>();
        this.openAttendanceSessions = other.openAttendanceSessions != null ? other.openAttendanceSessions : new ArrayList<>();
        this.organizations = other.organizations != null ? other.organizations : new ArrayList<>();
        this.diplomaPaperIds = other.diplomaPaperIds != null ? other.diplomaPaperIds : new ArrayList<>();
    }

    public User authenticate(String email, String password) {
        User user = findUserByEmail(email);
        if (user != null && user.checkPassword(password)) return user;
        return null;
    }

    public List<User> searchUsersByRegex(String regex) {
        List<User> result = new ArrayList<>();
        if (regex == null || regex.isEmpty()) return result;
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            for (User u : users) {
                String text = u.getId() + " " + u.getFirstName() + " " + u.getLastName() + " " + u.getEmail();
                if (pattern.matcher(text).find()) result.add(u);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("Wrong regex: " + e.getMessage());
        }
        return result;
    }

    public List<Course> searchCoursesByRegex(String regex) {
        List<Course> result = new ArrayList<>();
        if (regex == null || regex.isEmpty()) return result;
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            for (Course c : courses) {
                String text = c.getCourseId() + " " + c.getName() + " " + c.getMajor() + " " + c.getCourseType();
                if (pattern.matcher(text).find()) result.add(c);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("Wrong regex: " + e.getMessage());
        }
        return result;
    }

    public List<News> searchNewsByRegex(String regex) {
        List<News> result = new ArrayList<>();
        if (regex == null || regex.isEmpty()) return result;
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            for (News n : news) {
                String text = n.getTitle() + " " + n.getContent() + " " + n.getTopic();
                if (pattern.matcher(text).find()) result.add(n);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("Wrong regex: " + e.getMessage());
        }
        return result;
    }

    public String generateMarksReport() {
        return generateMarksReportForTeacher(null);
    }

    public String generateMarksReportForTeacher(Teacher teacher) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Marks Report ===\n");
        int count = 0;
        double sum = 0.0;
        int failed = 0;

        for (Mark mark : marks) {
            if (mark == null || mark.getCourse() == null) continue;
            if (teacher != null && !mark.getCourse().getTeachers().contains(teacher)) continue;

            count++;
            sum += mark.getTotalScore();
            if ("F".equals(mark.getLetterGrade())) failed++;
            sb.append(count).append(") ").append(mark).append("\n");
        }

        sb.append("Total marks: ").append(count).append("\n");
        if (count > 0) {
            sb.append("Average score: ").append(String.format("%.2f", sum / count)).append("\n");
            sb.append("Failed marks: ").append(failed).append("\n");
        }
        return sb.toString();
    }

    // ---------- object serialization (main Part C storage) ----------

    public void saveData() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(this);
            System.out.println("[DataStorage] Serialized data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
    }

    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("[DataStorage] No serialized data. Starting fresh.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof DataStorage) {
                copyFrom((DataStorage) obj);
                System.out.println("[DataStorage] Deserialized data loaded from " + DATA_FILE);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Deserialization error: " + e.getMessage());
        }
    }

    public static DataStorage loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof DataStorage) return (DataStorage) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Deserialization error: " + e.getMessage());
        }
        return null;
    }

    // Old text-file save/load is kept as a fallback and for demonstration of Scanner/PrintWriter.
    public void saveDataAsText() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try {
            saveUsers();
            saveCourses();
            saveLessons();
            saveEnrollments();
            saveMarks();
            savePapers();
            saveProjects();
            saveNews();
            saveMessages();
            saveLogs();
            saveComplaints();
            saveOrganizations();
            saveJournals();
            saveDiplomaPapers();
            System.out.println("[DataStorage] Saved to text files in " + DATA_DIR + "/");
        } catch (FileNotFoundException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    public void loadDataFromText() {
        File dir = new File(DATA_DIR);
        if (!dir.exists() || !new File(DATA_DIR, "users.txt").exists()) {
            System.out.println("[DataStorage] No text data. Starting fresh.");
            return;
        }
        try {
            users = loadUsers();
            courses = loadCourses();
            loadLessons();
            enrollments = loadEnrollments();
            marks = loadMarks();
            papers = loadPapers();
            projects = loadProjects();
            news = loadNews();
            messages = loadMessages();
            logs = loadLogs();
            complaints = loadComplaints();
            organizations = loadOrganizations();
            journals = loadJournals();
            diplomaPaperIds = loadDiplomaPapers();
            System.out.println("[DataStorage] Loaded from text files.");
        } catch (FileNotFoundException e) {
            System.err.println("Load error: " + e.getMessage());
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace(";", ",").replace("\n", " ");
    }

    private void saveUsers() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/users.txt");
        for (User u : users) {
            if (u instanceof Student && !(u instanceof GraduateStudent)) {
                Student s = (Student) u;
                pw.println("STUDENT;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(s.getStudentId()) + ";"
                        + s.getGpa() + ";" + s.getFailCount() + ";" + s.isResearcher());
            } else if (u instanceof GraduateStudent) {
                GraduateStudent g = (GraduateStudent) u;
                pw.println("GRADUATE;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(g.getStudentId()) + ";"
                        + g.getGpa() + ";" + g.getFailCount());
            } else if (u instanceof Teacher) {
                Teacher t = (Teacher) u;
                pw.println("TEACHER;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(t.getEmployeeId()) + ";" + t.getSalary() + ";"
                        + esc(t.getDepartment()) + ";" + t.getPosition() + ";" + t.getRating() + ";"
                        + t.isResearcher());
            } else if (u instanceof Manager) {
                Manager m = (Manager) u;
                pw.println("MANAGER;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(m.getEmployeeId()) + ";" + m.getSalary() + ";"
                        + esc(m.getDepartment()) + ";" + m.getManagerType());
            } else if (u instanceof Admin) {
                Admin a = (Admin) u;
                pw.println("ADMIN;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(a.getEmployeeId()) + ";" + a.getSalary() + ";"
                        + esc(a.getDepartment()));
            } else if (u instanceof TechSupportSpecialist) {
                TechSupportSpecialist t = (TechSupportSpecialist) u;
                pw.println("TECH;" + esc(u.getId()) + ";" + esc(u.getFirstName()) + ";"
                        + esc(u.getLastName()) + ";" + esc(u.getEmail()) + ";" + esc(u.getPassword())
                        + ";" + u.getLanguage() + ";" + esc(t.getEmployeeId()) + ";" + t.getSalary() + ";"
                        + esc(t.getDepartment()));
            }
        }
        pw.close();
    }

    private List<User> loadUsers() throws FileNotFoundException {
        List<User> list = new ArrayList<>();
        Scanner sc = new Scanner(new File(DATA_DIR + "/users.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            User u = createUserFromParts(p);
            if (u != null) list.add(u);
        }
        sc.close();
        return list;
    }

    private User createUserFromParts(String[] p) {
        Language lang = Language.valueOf(p[6]);
        switch (p[0]) {
            case "STUDENT": {
                Student s = new Student(p[1], p[2], p[3], p[4], p[5], lang, p[7]);
                s.setGpa(Double.parseDouble(p[8]));
                s.failCount = Integer.parseInt(p[9]);
                s.setResearcher(Boolean.parseBoolean(p[10]));
                return s;
            }
            case "GRADUATE": {
                GraduateStudent g = new GraduateStudent(p[1], p[2], p[3], p[4], p[5], lang, p[7]);
                g.setGpa(Double.parseDouble(p[8]));
                g.failCount = Integer.parseInt(p[9]);
                return g;
            }
            case "TEACHER": {
                Teacher t = new Teacher(p[1], p[2], p[3], p[4], p[5], lang, p[7],
                        Double.parseDouble(p[8]), p[9], TeacherPosition.valueOf(p[10]));
                t.setRating(Double.parseDouble(p[11]));
                t.setResearcher(Boolean.parseBoolean(p[12]));
                return t;
            }
            case "MANAGER":
                return new Manager(p[1], p[2], p[3], p[4], p[5], lang, p[7],
                        Double.parseDouble(p[8]), p[9], ManagerType.valueOf(p[10]));
            case "ADMIN":
                return new Admin(p[1], p[2], p[3], p[4], p[5], lang, p[7],
                        Double.parseDouble(p[8]), p[9]);
            case "TECH":
                return new TechSupportSpecialist(p[1], p[2], p[3], p[4], p[5], lang, p[7],
                        Double.parseDouble(p[8]), p[9]);
            default:
                return null;
        }
    }

    private void saveCourses() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/courses.txt");
        for (Course c : courses) {
            StringBuilder teacherIds = new StringBuilder();
            for (Teacher t : c.getTeachers()) {
                if (teacherIds.length() > 0) teacherIds.append(",");
                teacherIds.append(t.getId());
            }
            pw.println(esc(c.getCourseId()) + ";" + esc(c.getName()) + ";" + c.getCredits() + ";"
                    + c.getCourseType() + ";" + esc(c.getMajor()) + ";" + c.getStudyYear() + ";"
                    + teacherIds);
        }
        pw.close();
    }

    private List<Course> loadCourses() throws FileNotFoundException {
        List<Course> list = new ArrayList<>();
        Scanner sc = new Scanner(new File(DATA_DIR + "/courses.txt"));
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            Course c = new Course(p[0], p[1], Integer.parseInt(p[2]),
                    CourseType.valueOf(p[3]), p[4], Integer.parseInt(p[5]));
            if (p.length > 6 && !p[6].isEmpty()) {
                for (String tid : p[6].split(",")) {
                    User u = findUserById(tid);
                    if (u instanceof Teacher) c.addTeacher((Teacher) u);
                }
            }
            list.add(c);
        }
        sc.close();
        return list;
    }

    private void saveLessons() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/lessons.txt");
        for (Course c : courses) {
            for (Lesson l : c.getLessons()) {
                String day = l.getDayOfWeek() == null ? "" : l.getDayOfWeek().toString();
                String st = l.getStartTime() == null ? "" : l.getStartTime().toString();
                String et = l.getEndTime() == null ? "" : l.getEndTime().toString();
                pw.println(esc(c.getCourseId()) + ";" + esc(l.getLessonId()) + ";"
                        + esc(l.getTopic()) + ";" + l.getLessonType() + ";" + day + ";" + st + ";" + et);
            }
        }
        pw.close();
    }

    private void loadLessons() throws FileNotFoundException {
        File f = new File(DATA_DIR + "/lessons.txt");
        if (!f.exists()) return;
        Scanner sc = new Scanner(f);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            Course c = findCourseById(p[0]);
            if (c != null) {
                if (p.length > 6 && p[4] != null && !p[4].isEmpty()) {
                    try {
                        DayOfWeek d = DayOfWeek.valueOf(p[4]);
                        LocalTime st = p[5].isEmpty() ? null : LocalTime.parse(p[5]);
                        LocalTime et = p[6].isEmpty() ? null : LocalTime.parse(p[6]);
                        c.addLesson(new Lesson(p[1], p[2], LessonType.valueOf(p[3]), d, st, et));
                    } catch (Exception ex) {
                        c.addLesson(new Lesson(p[1], p[2], LessonType.valueOf(p[3])));
                    }
                } else {
                    c.addLesson(new Lesson(p[1], p[2], LessonType.valueOf(p[3])));
                }
            }
        }
        sc.close();
    }

    private void saveEnrollments() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/enrollments.txt");
        for (Enrollment e : enrollments) {
            pw.println(e.getStudentId() + ";" + e.getCourseId() + ";" + (e.getLessonId() == null ? "" : e.getLessonId()));
        }
        pw.close();
    }

    private List<Enrollment> loadEnrollments() throws FileNotFoundException {
        List<Enrollment> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/enrollments.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            String studentId = p.length > 0 ? p[0] : "";
            String courseId = p.length > 1 ? p[1] : "";
            String lessonId = p.length > 2 ? p[2] : "";
            list.add(new Enrollment(studentId, courseId, lessonId));
        }
        sc.close();
        return list;
    }

    private void saveMarks() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/marks.txt");
        for (Mark m : marks) {
            pw.println(m.getMarkId() + ";" + m.getStudent().getId() + ";" + m.getCourse().getCourseId()
                    + ";" + m.getFirstAttestation() + ";" + m.getSecondAttestation() + ";"
                    + m.getFinalExam());
        }
        pw.close();
    }

    private List<Mark> loadMarks() throws FileNotFoundException {
        List<Mark> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/marks.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            Student s = (Student) findUserById(p[1]);
            Course c = findCourseById(p[2]);
            if (s != null && c != null) {
                list.add(new Mark(p[0], Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                        Double.parseDouble(p[5]), s, c));
            }
        }
        sc.close();
        return list;
    }

    private void saveProjects() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/projects.txt");
        for (ResearchProject p : projects) {
            pw.println(esc(p.getTopic()));
        }
        pw.close();
    }

    private List<ResearchProject> loadProjects() throws FileNotFoundException {
        List<ResearchProject> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/projects.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) list.add(new ResearchProject(line));
        }
        sc.close();
        return list;
    }

    private void savePapers() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/papers.txt");
        for (ResearchPaper p : papers) {
            pw.println(esc(p.getPaperId()) + ";" + esc(p.getTitle()) + ";" + esc(p.getJournal())
                    + ";" + p.getPages() + ";" + p.getCitations() + ";" + esc(p.getOwnerId()));
        }
        pw.close();
    }

    private List<ResearchPaper> loadPapers() throws FileNotFoundException {
        List<ResearchPaper> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/papers.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            ResearchPaper paper = new ResearchPaper(p[1], p[2],
                    Integer.parseInt(p[3]), new Date());
            paper.setPaperId(p[0]);
            paper.setCitations(Integer.parseInt(p[4]));
            paper.setOwnerId(p[5].isEmpty() ? null : p[5]);
            list.add(paper);
        }
        sc.close();
        return list;
    }

    private void saveNews() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/news.txt");
        for (News n : news) {
            pw.println(esc(n.getNewsId()) + ";" + esc(n.getTitle()) + ";" + esc(n.getContent())
                    + ";" + esc(n.getTopic()) + ";" + n.isPinned());
        }
        pw.close();
    }

    private List<News> loadNews() throws FileNotFoundException {
        List<News> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/news.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            News n = new News(p[1], p[2], p[3]);
            if (Boolean.parseBoolean(p[4])) n.pin();
            list.add(n);
        }
        sc.close();
        return list;
    }

    private void saveMessages() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/messages.txt");
        for (Message m : messages) {
            if (m instanceof Request) {
                Request r = (Request) m;
                String senderId = r.getSender() != null ? r.getSender().getId() : "";
                pw.println("REQUEST;" + esc(r.getMessageId()) + ";" + senderId
                        + ";" + esc(r.getContent()) + ";" + r.getStatus());
            } else {
                String receiverId = m.getReceiver() != null ? m.getReceiver().getId() : "";
                String senderId = m.getSender() != null ? m.getSender().getId() : "";
                pw.println("REGULAR;" + esc(m.getMessageId()) + ";" + senderId
                        + ";" + receiverId + ";" + esc(m.getContent()));
            }
        }
        pw.close();
    }

    private List<Message> loadMessages() throws FileNotFoundException {
        List<Message> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/messages.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            User sender = findUserById(p[2]);
            if (sender == null) continue;
            if ("REQUEST".equals(p[0])) {
                Request r = new Request(p[1], sender, p[3]);
                r.setStatus(RequestStatus.valueOf(p[4]));
                list.add(r);
            } else {
                User receiver = p[3].isEmpty() ? null : findUserById(p[3]);
                list.add(new Message(p[1], sender, receiver, p[4], new Date()));
            }
        }
        sc.close();
        return list;
    }

    private void saveLogs() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/logs.txt");
        for (Log log : logs) {
            pw.println(esc(log.getLogId()) + ";" + esc(log.getUserId()) + ";"
                    + esc(log.getAction()) + ";" + log.getTimestamp().getTime());
        }
        pw.close();
    }

    private List<Log> loadLogs() throws FileNotFoundException {
        List<Log> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/logs.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            list.add(new Log(p[0], p[1], p[2], new Date(Long.parseLong(p[3]))));
        }
        sc.close();
        return list;
    }

    private void saveComplaints() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/complaints.txt");
        for (Complaint c : complaints) {
            pw.println(esc(c.getComplaintId()) + ";" + c.getStudent().getId() + ";"
                    + esc(c.getText()) + ";" + c.getUrgency());
        }
        pw.close();
    }

    private List<Complaint> loadComplaints() throws FileNotFoundException {
        List<Complaint> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/complaints.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            Student s = (Student) findUserById(p[1]);
            if (s != null) {
                list.add(new Complaint(p[0], s, p[2], ComplaintUrgency.valueOf(p[3]), new Date()));
            }
        }
        sc.close();
        return list;
    }

    private void saveOrganizations() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/organizations.txt");
        for (StudentOrganization o : organizations) {
            StringBuilder members = new StringBuilder();
            for (Student s : o.getMembers()) {
                if (members.length() > 0) members.append(",");
                members.append(s.getId());
            }
            String headId = o.getHead() != null ? o.getHead().getId() : "";
            pw.println(esc(o.getName()) + ";" + headId + ";" + members);
        }
        pw.close();
    }

    private List<StudentOrganization> loadOrganizations() throws FileNotFoundException {
        List<StudentOrganization> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/organizations.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            StudentOrganization org = new StudentOrganization(p[0]);
            if (p.length > 2 && !p[2].isEmpty()) {
                for (String mid : p[2].split(",")) {
                    User u = findUserById(mid);
                    if (u instanceof Student) org.addMember((Student) u);
                }
            }
            if (p.length > 1 && !p[1].isEmpty()) {
                User h = findUserById(p[1]);
                if (h instanceof Student) org.setHead((Student) h);
            }
            list.add(org);
        }
        sc.close();
        return list;
    }

    private void saveJournals() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/journals.txt");
        for (Journal j : journals) {
            StringBuilder subs = new StringBuilder();
            for (Observer o : j.getSubscribers()) {
                if (o instanceof User) {
                    if (subs.length() > 0) subs.append(",");
                    subs.append(((User) o).getId());
                }
            }
            pw.println(esc(j.getName()) + ";" + subs);
        }
        pw.close();
    }

    private List<Journal> loadJournals() throws FileNotFoundException {
        List<Journal> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/journals.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            Journal j = new Journal(p[0]);
            if (p.length > 1 && !p[1].isEmpty()) {
                for (String uid : p[1].split(",")) {
                    User u = findUserById(uid);
                    if (u != null) j.subscribe(u);
                }
            }
            list.add(j);
        }
        sc.close();
        return list;
    }

    private void saveDiplomaPapers() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(DATA_DIR + "/diploma.txt");
        for (String key : diplomaPaperIds) pw.println(key);
        pw.close();
    }

    private List<String> loadDiplomaPapers() throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        File file = new File(DATA_DIR + "/diploma.txt");
        if (!file.exists()) return list;
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) list.add(line);
        }
        sc.close();
        return list;
    }

    @Override
    public String toString() {
        return "DataStorage{users=" + users.size() + ", courses=" + courses.size()
                + ", marks=" + marks.size() + ", papers=" + papers.size() + "}";
    }
}
