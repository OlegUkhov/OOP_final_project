public class TeacherResearcher extends ResearcherDecorator {

    private Teacher teacher;

    public TeacherResearcher(Teacher teacher) {
        super(null, teacher != null ? teacher.getId() : null);
        this.teacher = teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "TeacherResearcher{name='" + (teacher != null
                ? teacher.getFirstName() + " " + teacher.getLastName() : "null")
                + "', papers=" + getPapers().size()
                + ", h-index=" + calculateHIndex() + "}";
    }
}
