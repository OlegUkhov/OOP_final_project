import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;

    private ManagerType managerType;

    public Manager(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, ManagerType managerType) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managerType = managerType;
    }

    public ManagerType getManagerType() { return managerType; }

    public void assignCourse(Teacher teacher, Course course) {
        if (teacher != null && course != null) teacher.manageCourse(course);
    }

    public void approveRegistration(Student student, Course course) {
        if (student != null && course != null && !course.getLessons().isEmpty())
            student.registerForCourse(course, course.getLessons().get(0));
    }


    public void addCourseForRegistration(Course course, String major, int studyYear) {
        if (course != null) {
            course.setMajor(major);
            course.setStudyYear(studyYear);
            DataStorage.getInstance().addCourse(course);
        }
    }

    public String createStatisticalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Academic Performance Report ===\n");
        List<Student> students = new ArrayList<>();
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Student) students.add((Student) u);
        }
        sb.append("Total Students: ").append(students.size()).append("\n");
        if (!students.isEmpty()) {
            double totalGpa = 0;
            for (Student s : students) totalGpa += s.getGpa();
            sb.append("Average GPA: ").append(String.format("%.2f", totalGpa / students.size()));
        }
        return sb.toString();
    }

    public void publishNews(String title, String content, String topic) {
        DataStorage.getInstance().publishNews(new News(title, content, topic));
    }

    public void manageNews(News news) {
        if (news != null) DataStorage.getInstance().publishNews(news);
    }

    public void generateTopCitedResearcherNews() {
        Researcher top = DataStorage.getInstance().getTopCitedResearcher();
        if (top == null) return;
        String name = "Unknown";
        if (top instanceof TeacherResearcher) {
            Teacher t = ((TeacherResearcher) top).getTeacher();
            if (t != null) name = t.getFirstName() + " " + t.getLastName();
        }
        publishNews("Top Cited Researcher", name + " h-index=" + top.calculateHIndex(), "Research");
    }

    public List<Request> viewEmployeeRequests() {
        List<Request> result = new ArrayList<>();
        for (Request r : DataStorage.getInstance().getRequests()) {
            if (r.getSender() instanceof Employee) result.add(r);
        }
        return result;
    }

    public List<Student> getStudentsSortedByGpa() {
        List<Student> students = new ArrayList<>();
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Student) students.add((Student) u);
        }
        students.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return students;
    }

    public List<Student> getStudentsSortedByName() {
        List<Student> students = new ArrayList<>();
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Student) students.add((Student) u);
        }
        students.sort(Comparator.comparing(Student::getFirstName)
                .thenComparing(Student::getLastName));
        return students;
    }

    @Override
    public String toString() {
        return "Manager{id='" + id + "', type=" + managerType + "}";
    }
}
