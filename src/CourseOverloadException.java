// Thrown by Student.registerForCourse() and Manager.approveRegistration()
// when adding a course would push total credits above 21
public class CourseOverloadException extends RuntimeException {
    public CourseOverloadException(String message) {
        super(message);
    }
}
