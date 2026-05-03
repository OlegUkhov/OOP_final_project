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
        if (comment != null && !comment.isEmpty()) {
            comments.add(comment);
        }
    }

    public void pin() {
        this.isPinned = true;
    }

    public void unpin() {
        this.isPinned = false;
    }

    public String getNewsId() {
        return newsId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getComments() {
        return new ArrayList<>(comments);
    }

    @Override
    public String toString() {
        return "News{" +
                "newsId='" + newsId + '\'' +
                ", title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", isPinned=" + isPinned +
                ", date=" + date +
                ", comments=" + comments.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        News news = (News) o;
        return Objects.equals(newsId, news.newsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsId);
    }
}
