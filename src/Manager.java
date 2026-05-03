// Класс менеджера университета (ОР или заведующий кафедрой).
// Управляет расписанием курсов, регистрацией студентов, новостями и отчётами.
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Manager extends Employee {

    // Тип менеджера: OR (общий регистратор) или DEPARTMENT (кафедра)
    private ManagerType managerType;
    // Список новостей, которыми управляет менеджер
    private List<News> managedNews;

    // Конструктор — инициализирует менеджера с типом
    public Manager(String id, String firstName, String lastName, String email,
                   String password, Language language, String employeeId,
                   double salary, String department, ManagerType managerType) {
        super(id, firstName, lastName, email, password, language, employeeId, salary, department);
        this.managerType = managerType;
        this.managedNews = new ArrayList<>();
    }

    // Назначить преподавателю курс
    public void assignCourse(Teacher teacher, Course course) {
        if (teacher != null && course != null) {
            // Преподаватель берёт курс под управление
            teacher.manageCourse(course);
        }
    }

    // Одобрить регистрацию студента на курс (проверяет лимит 21 кредит)
    public void approveRegistration(Student student, Course course) {
        if (student == null || course == null) return;
        if (student.getTotalCredits() + course.getCredits() > 21) {
            throw new CourseOverloadException("Student would exceed 21 credits");
        }
        // Регистрируем студента на курс
        student.registerForCourse(course);
    }

    // Добавить курс в реестр доступных для регистрации (с указанием специальности/курса)
    public void addCourseForRegistration(Course course) {
        if (course != null) {
            System.out.println("[Manager] Course added for registration: " + course);
        }
    }

    // Создать статистический отчёт по успеваемости
    public String createStatisticalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Academic Performance Report ===\n");
        // Информация о менеджере, создавшем отчёт
        sb.append("By: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("Department: ").append(department).append("\n");
        return sb.toString();
    }

    // Управлять новостью: добавить её в список и автоматически закрепить, если тема «Research»
    public void manageNews(News news) {
        if (news == null) return;
        if (!managedNews.contains(news)) {
            managedNews.add(news);
        }
        // Новости о Research автоматически закрепляются (pin) согласно требованиям
        if ("Research".equals(news.getTopic())) {
            news.pin();
        }
    }

    // Просмотреть список запросов от сотрудников
    public List<Request> viewEmployeeRequests() {
        // В данной версии запросы хранятся в DataStorage; возвращаем пустой список-заглушку
        return new ArrayList<>();
    }

    // Получить список студентов, отсортированных по GPA (по убыванию)
    public List<Student> getStudentsSortedByGpa(List<Student> students) {
        if (students == null || students.isEmpty()) return new ArrayList<>();
        List<Student> sorted = new ArrayList<>(students);
        // Сортируем по убыванию GPA
        sorted.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return sorted;
    }

    // Получить список студентов, отсортированных по имени (по возрастанию)
    public List<Student> getStudentsSortedByName(List<Student> students) {
        if (students == null || students.isEmpty()) return new ArrayList<>();
        List<Student> sorted = new ArrayList<>(students);
        // Сортируем сначала по имени, затем по фамилии
        sorted.sort(Comparator.comparing(Student::getFirstName)
                .thenComparing(Student::getLastName));
        return sorted;
    }

    // Строковое представление менеджера
    @Override
    public String toString() {
        return "Manager{id='" + id + "', name='" + firstName + " " + lastName
                + "', type=" + managerType + ", dept='" + department + "'}";
    }
}
