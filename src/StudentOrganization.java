import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentOrganization implements Serializable {

    private static final long serialVersionUID = 1L;

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
        if (student != null && !members.contains(student)) members.add(student);
    }

    public void setHead(Student student) {
        if (student != null && members.contains(student)) this.head = student;
    }

    public List<Student> getMembers() { return new ArrayList<>(members); }
    public Student getHead() { return head; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "StudentOrganization{name='" + name + "', members=" + members.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentOrganization)) return false;
        return Objects.equals(organizationId, ((StudentOrganization) o).organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId);
    }
}
