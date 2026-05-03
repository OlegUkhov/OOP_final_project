import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentOrganization {
    private String organizationId;
    private String name;
    private Student head;
    private List<Student> members;

    public StudentOrganization(String name) {
        this.organizationId = UUID.randomUUID().toString();
        this.name = name;
        this.head = null;
        this.members = new ArrayList<>();
    }

    public void addMember(Student student) {
        if (student != null && !members.contains(student)) {
            members.add(student);
        }
    }

    public void removeMember(Student student) {
        if (student != null) {
            members.remove(student);
            // Если удаляемый студент был head, сбросить head
            if (head != null && head.equals(student)) {
                head = null;
            }
        }
    }

    public void setHead(Student student) {
        if (student != null && members.contains(student)) {
            this.head = student;
        }
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public Student getHead() {
        return head;
    }

    public List<Student> getMembers() {
        return new ArrayList<>(members);
    }

    @Override
    public String toString() {
        return "StudentOrganization{" +
                "organizationId='" + organizationId + '\'' +
                ", name='" + name + '\'' +
                ", head=" + (head != null ? head.getFirstName() + " " + head.getLastName() : "none") +
                ", members=" + members.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentOrganization)) return false;
        StudentOrganization that = (StudentOrganization) o;
        return Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId);
    }
}
