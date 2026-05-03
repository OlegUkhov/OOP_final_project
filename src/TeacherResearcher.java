import java.util.Comparator;

public class TeacherResearcher extends ResearcherDecorator {
    private Teacher teacher;

    public TeacherResearcher(Teacher teacher) {
        super(null);  // Teacher сам по себе не Researcher, но мы оборачиваем его
        this.teacher = teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "TeacherResearcher{" +
                "teacher=" + (teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "null") +
                ", papers=" + papers.size() +
                ", h-index=" + calculateHIndex() +
                '}';
    }
}
