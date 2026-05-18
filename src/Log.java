import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    private String logId;
    private String userId;
    private String action;
    private Date timestamp;

    public Log(String logId, String userId, String action, Date timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public String getLogId() { return logId; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] user=" + userId + " | " + action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Log)) return false;
        return Objects.equals(logId, ((Log) o).logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}
