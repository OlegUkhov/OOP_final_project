// Запрос в техподдержку (например, починить проектор).
// Начальный статус — VIEWED. Специалист меняет его на ACCEPTED/REJECTED/DONE.
import java.util.Date;
import java.util.Objects;

public class Request {

    // Уникальный идентификатор запроса
    private String requestId;
    // Описание проблемы
    private String description;
    // Текущий статус запроса
    private RequestStatus status;
    // Пользователь, создавший запрос (студент или сотрудник)
    private User createdBy;
    // Дата создания запроса
    private Date date;

    // Конструктор — новый запрос всегда получает статус VIEWED
    public Request(String requestId, String description, User createdBy) {
        this.requestId = requestId;
        this.description = description;
        this.createdBy = createdBy;
        // Начальный статус: запрос просмотрен (но ещё не принят)
        this.status = RequestStatus.VIEWED;
        this.date = new Date();
    }

    // Получить текущий статус запроса (указано в диаграмме явно)
    public RequestStatus getStatus() {
        return status;
    }

    // Установить новый статус запроса (указано в диаграмме явно)
    public void setStatus(RequestStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    // Строковое представление запроса
    @Override
    public String toString() {
        return "Request{id='" + requestId + "', desc='" + description
                + "', status=" + status
                + ", by=" + (createdBy != null ? createdBy.getFirstName() + " " + createdBy.getLastName() : "null")
                + "}";
    }

    // Два запроса равны, если совпадают их requestId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request r = (Request) o;
        return Objects.equals(requestId, r.requestId);
    }

    // Хэш-код по requestId
    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}
