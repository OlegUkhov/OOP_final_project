// Новость университета.
// Новости с темой «Research» закрепляются (pin) автоматически менеджером.
// Поддерживает добавление комментариев.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class News {

    // Уникальный идентификатор новости
    private String newsId;
    // Заголовок новости
    private String title;
    // Основной текст новости
    private String content;
    // Тема новости (например, «Research»)
    private String topic;
    // Флаг закреплённости новости
    private boolean isPinned;
    // Дата создания новости
    private Date date;
    // Список комментариев к новости
    private List<String> comments;

    // Конструктор — создаёт незакреплённую новость с текущей датой
    public News(String title, String content, String topic) {
        this.newsId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.isPinned = false;
        this.date = new Date();
        this.comments = new ArrayList<>();
    }

    // Добавить комментарий к новости
    public void addComment(String comment) {
        if (comment != null && !comment.isEmpty()) {
            comments.add(comment);
        }
    }

    // Закрепить новость (pin)
    public void pin() {
        this.isPinned = true;
    }

    // Получить тему новости — нужно для проверки в Manager.manageNews()
    public String getTopic() {
        return topic;
    }

    // Получить флаг закреплённости — нужен для отображения в demo
    public boolean isPinned() {
        return isPinned;
    }

    // Получить список комментариев
    public List<String> getComments() {
        return new ArrayList<>(comments);
    }

    // Строковое представление новости
    @Override
    public String toString() {
        return "News{title='" + title + "', topic='" + topic
                + "', pinned=" + isPinned + ", comments=" + comments.size() + "}";
    }

    // Две новости равны, если совпадают их newsId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        News n = (News) o;
        return Objects.equals(newsId, n.newsId);
    }

    // Хэш-код по newsId
    @Override
    public int hashCode() {
        return Objects.hash(newsId);
    }
}
