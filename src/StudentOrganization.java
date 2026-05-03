// A student club or organization with a head and a members list
// Head must already be in the members list before being assigned
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentOrganization {

    private String organizationId;
    private String name;
    // Must be a member of this organization; checked in setHead()
    private Student head;
    private List<Student> members;

    public StudentOrganization(String name) {
        this.organizationId = UUID.randomUUID().toString();
        this.name = name;
        this.head = null;
        this.members = new ArrayList<>();
    }

    // Called by Student.joinOrganization() to keep both sides in sync
    public void addMember(Student student) {
        if (student != null && !members.contains(student)) {
            members.add(student);
        }
    }

    // If the removed student was the head the head reference is cleared
    public void removeMember(Student student) {
        if (student == null) return;
        members.remove(student);
        if (head != null && head.equals(student)) {
            head = null;
        }
    }

    // Guard: student must already be in members to become head
    public void setHead(Student student) {
        if (student != null && members.contains(student)) {
            this.head = student;
        }
    }

    public List<Student> getMembers() {
        return new ArrayList<>(members);
    }

    // Read in Main to print the head name after the organization is created
    public Student getHead() {
        return head;
    }

    @Override
    public String toString() {
        return "StudentOrganization{name='" + name + "', head="
                + (head != null ? head.getFirstName() + " " + head.getLastName() : "none")
                + ", members=" + members.size() + "}";
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
