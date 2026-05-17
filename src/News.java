import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class News {

    private String newsId;
    private String title;
    private String content;
    private String topic;
    private boolean isPinned;
    private Date date;
    private List<String> comments;

    public News(String title, String content, String topic) {
        this.newsId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.isPinned = false;
        this.date = new Date();
        this.comments = new ArrayList<>();
    }

    public void addComment(String comment) {
        if (comment != null && !comment.isEmpty()) comments.add(comment);
    }

    public void pin() { this.isPinned = true; }

    public String getNewsId() { return newsId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTopic() { return topic; }
    public boolean isPinned() { return isPinned; }

    public List<String> getComments() { return new ArrayList<>(comments); }

    /** Readable output for console (not debug-style toString). */
    public void print(User viewer) {
        System.out.println("  " + viewer.t("Title:", "Тақырып:", "Заголовок:") + " " + title);
        System.out.println("  " + viewer.t("Topic:", "Сала:", "Тема:") + " " + topic
                + (isPinned ? " [" + viewer.t("PINNED", "БЕКІТІЛГЕН", "ЗАКРЕПЛЕНО") + "]" : ""));
        System.out.println("  " + viewer.t("Text:", "Мәтін:", "Текст:"));
        System.out.println("    " + content);
        if (!comments.isEmpty()) {
            System.out.println("  " + viewer.t("Comments:", "Пікірлер:", "Комментарии:") + " " + comments.size());
        }
    }

    @Override
    public String toString() {
        return title + " (" + topic + ")" + (isPinned ? " [pinned]" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        return Objects.equals(newsId, ((News) o).newsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsId);
    }
}
