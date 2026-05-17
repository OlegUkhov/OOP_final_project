// A university news item; supports comments and a pinned flag
// Manager.manageNews() automatically pins items whose topic is "Research"
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class News {

    private String newsId;
    private String title;
    private String content;
    // Checked in Manager.manageNews() to decide if auto-pin applies
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

    // Sets isPinned to true; called by Manager.manageNews() for Research topic news
    public void pin() {
        this.isPinned = true;
    }

    // Read by Manager.manageNews() to check if this news should be auto-pinned
    public String getTopic() {
        return topic;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public List<String> getComments() {
        return new ArrayList<>(comments);
    }

    @Override
    public String toString() {
        return "News{title='" + title + "', topic='" + topic
                + "', pinned=" + isPinned + ", comments=" + comments.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        News n = (News) o;
        return Objects.equals(newsId, n.newsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsId);
    }
}
