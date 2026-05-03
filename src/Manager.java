// University manager (OR or DEPARTMENT type)
// Handles course assignment student registration news and sorted student views
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager extends Employee {

    private ManagerType managerType;
    private List<News> managedNews;

    public Manager(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, ManagerType managerType) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managerType = managerType;
        this.managedNews = new ArrayList<>();
    }

    // Delegates to Teacher.manageCourse() which also registers the teacher inside Course
    public void assignCourse(Teacher teacher, Course course) {
        if (teacher != null && course != null) {
            teacher.manageCourse(course);
        }
    }

    // Pre-checks credit limit before calling Student.registerForCourse()
    public void approveRegistration(Student student, Course course) {
        if (student == null || course == null) return;
        if (student.getTotalCredits() + course.getCredits() > 21) {
            throw new CourseOverloadException("Student would exceed 21 credits");
        }
        student.registerForCourse(course);
    }

    // Placeholder; in a full system this would add the course to a registry with major and year info
    public void addCourseForRegistration(Course course) {
        if (course != null) {
            System.out.println("[Manager] Course added for registration: " + course);
        }
    }

    // Returns a simple text report; could be extended to pull real data from DataStorage
    public String createStatisticalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Academic Performance Report ===\n");
        sb.append("By: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("Department: ").append(department).append("\n");
        return sb.toString();
    }

    // Adds the news to the local list and pins it automatically if topic is Research
    // News.pin() just sets the isPinned flag to true
    public void manageNews(News news) {
        if (news == null) return;
        if (!managedNews.contains(news)) {
            managedNews.add(news);
        }
        if ("Research".equals(news.getTopic())) {
            news.pin();
        }
    }

    // Stub; full version would query DataStorage for all employee requests
    public List<Request> viewEmployeeRequests() {
        return new ArrayList<>();
    }

    // Uses Comparator.comparingDouble with Student.getGpa() for descending sort
    public List<Student> getStudentsSortedByGpa(List<Student> students) {
        if (students == null || students.isEmpty()) return new ArrayList<>();
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return sorted;
    }

    // Chains two comparators: first name then last name ascending
    public List<Student> getStudentsSortedByName(List<Student> students) {
        if (students == null || students.isEmpty()) return new ArrayList<>();
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparing(Student::getFirstName)
                .thenComparing(Student::getLastName));
        return sorted;
    }

    @Override
    public String toString() {
        return "Manager{id='" + id + "', name='" + firstName + " " + lastName
                + "', type=" + managerType + ", dept='" + department + "'}";
    }
}
