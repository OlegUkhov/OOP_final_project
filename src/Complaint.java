// Жалоба преподавателя на студента, адресованная декану.
// Имеет уровень срочности (LOW / MEDIUM / HIGH).
import java.util.Date;
import java.util.Objects;

public class Complaint {

    // Уникальный идентификатор жалобы
    private String complaintId;
    // Студент, на которого подана жалоба
    private Student student;
    // Текст жалобы
    private String text;
    // Уровень срочности
    private ComplaintUrgency urgency;
    // Дата создания жалобы
    private Date date;

    // Конструктор — инициализирует все поля жалобы
    public Complaint(String complaintId, Student student, String text,
                     ComplaintUrgency urgency, Date date) {
        this.complaintId = complaintId;
        this.student = student;
        this.text = text;
        this.urgency = urgency;
        this.date = date;
    }

    // Получить уровень срочности жалобы (указано в диаграмме явно)
    public ComplaintUrgency getUrgency() {
        return urgency;
    }

    // Строковое представление жалобы
    @Override
    public String toString() {
        return "Complaint{id='" + complaintId + "', student="
                + (student != null ? student.getFirstName() + " " + student.getLastName() : "null")
                + ", urgency=" + urgency + ", date=" + date + ", text='" + text + "'}";
    }

    // Две жалобы равны, если совпадают их complaintId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        Complaint c = (Complaint) o;
        return Objects.equals(complaintId, c.complaintId);
    }

    // Хэш-код по complaintId
    @Override
    public int hashCode() {
        return Objects.hash(complaintId);
    }
}
