// Thrown when a student tries to register for a course they have already failed 3 times
// Guard should be added in Student.registerForCourse() using the failCount field
public class CourseFailLimitException extends RuntimeException {
    public CourseFailLimitException(String message) {
        super(message);
    }
}
