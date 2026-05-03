// Студенческая организация (кружок, клуб и т.д.).
// Имеет список участников и одного главу (head), который должен быть участником.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentOrganization {

    // Уникальный идентификатор организации
    private String organizationId;
    // Название организации
    private String name;
    // Глава организации (должен быть в списке участников)
    private Student head;
    // Список участников организации
    private List<Student> members;

    // Конструктор — создаёт организацию без участников и главы
    public StudentOrganization(String name) {
        this.organizationId = UUID.randomUUID().toString();
        this.name = name;
        this.head = null;
        this.members = new ArrayList<>();
    }

    // Добавить студента в организацию
    public void addMember(Student student) {
        if (student != null && !members.contains(student)) {
            members.add(student);
        }
    }

    // Удалить студента из организации; если это был head — сбросить head
    public void removeMember(Student student) {
        if (student == null) return;
        members.remove(student);
        // Если удалённый студент был главой — обнуляем главу
        if (head != null && head.equals(student)) {
            head = null;
        }
    }

    // Назначить главу организации (должен уже быть участником)
    public void setHead(Student student) {
        if (student != null && members.contains(student)) {
            this.head = student;
        }
    }

    // Получить список участников организации
    public List<Student> getMembers() {
        return new ArrayList<>(members);
    }

    // Получить главу организации — нужен для отображения в demo
    public Student getHead() {
        return head;
    }

    // Строковое представление организации
    @Override
    public String toString() {
        return "StudentOrganization{name='" + name + "', head="
                + (head != null ? head.getFirstName() + " " + head.getLastName() : "none")
                + ", members=" + members.size() + "}";
    }

    // Две организации равны, если совпадают их organizationId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentOrganization)) return false;
        StudentOrganization that = (StudentOrganization) o;
        return Objects.equals(organizationId, that.organizationId);
    }

    // Хэш-код по organizationId
    @Override
    public int hashCode() {
        return Objects.hash(organizationId);
    }
}
