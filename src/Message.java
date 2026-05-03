// Сообщение между сотрудниками университета.
// Хранит отправителя, получателя, текст и дату.
import java.util.Date;
import java.util.Objects;

public class Message {

    // Уникальный идентификатор сообщения
    private String messageId;
    // Отправитель сообщения
    private Employee sender;
    // Получатель сообщения
    private Employee receiver;
    // Текст сообщения
    private String content;
    // Дата отправки
    private Date date;

    // Конструктор — инициализирует все поля сообщения
    public Message(String messageId, Employee sender, Employee receiver,
                   String content, Date date) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date;
    }

    // Получить текст сообщения (указано в диаграмме явно)
    public String getContent() {
        return content;
    }

    // Строковое представление сообщения
    @Override
    public String toString() {
        return "Message{from=" + (sender != null ? sender.getFirstName() + " " + sender.getLastName() : "null")
                + ", to=" + (receiver != null ? receiver.getFirstName() + " " + receiver.getLastName() : "null")
                + ", content='" + content + "', date=" + date + "}";
    }

    // Два сообщения равны, если совпадают их messageId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message m = (Message) o;
        return Objects.equals(messageId, m.messageId);
    }

    // Хэш-код по messageId
    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}
