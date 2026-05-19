import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String courseId;
    private String name;
    private int credits;
    private CourseType courseType;
    private String major;
    private int studyYear;
    private List<String> teacherIds; // Store teacher IDs instead of Teacher objects
    private List<Lesson> lessons;

    public Course(String courseId, String name, int credits, CourseType courseType) {
        this(courseId, name, credits, courseType, "SITE", 2);
    }

    public Course(String courseId, String name, int credits, CourseType courseType,
                  String major, int studyYear) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
        this.major = major;
        this.studyYear = studyYear;
        this.teacherIds = new ArrayList<>();
        this.lessons = new ArrayList<>();
    }

    public void addTeacher(Teacher t) {
        if (t != null && !teacherIds.contains(t.getId())) teacherIds.add(t.getId());
    }

    public void addLesson(Lesson l) {
        if (l != null && !lessons.contains(l)) lessons.add(l);
    }

    public void removeLessonById(String lessonId) {
        lessons.removeIf(l -> l.getLessonId().equals(lessonId));
    }

    public List<Lesson> getLessons() { return new ArrayList<>(lessons); }
    public List<String> getTeacherIds() { return new ArrayList<>(teacherIds); }
    public List<Teacher> getTeachers() {
        return teacherIds.stream()
                .map(id -> (Teacher) DataStorage.getInstance().findUserById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getCourseId() { return courseId; }
    public int getCredits() { return credits; }
    public String getName() { return name; }
    public CourseType getCourseType() { return courseType; }
    public String getMajor() { return major; }
    public int getStudyYear() { return studyYear; }

    public void setMajor(String major) {
        if (major != null) this.major = major;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    @Override
    public String toString() {
        return "Course{id=\'" + courseId + "\", name=\'" + name + "\", credits=" + credits + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return Objects.equals(courseId, ((Course) o).courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}