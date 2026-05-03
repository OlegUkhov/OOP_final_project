// Thrown by GraduateStudent.setSupervisor() when the candidate researcher h-index is below 3
public class LowHIndexException extends RuntimeException {
    public LowHIndexException(String message) {
        super(message);
    }
}
