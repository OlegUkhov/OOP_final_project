// Университетский научный журнал (паттерн Observer).
// Пользователи могут подписаться/отписаться. При публикации новой статьи
// все подписчики автоматически получают уведомление (notifyObservers).
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Journal implements Observable {

    // Уникальный идентификатор журнала
    private String journalId;
    // Название журнала
    private String name;
    // Список опубликованных статей
    private List<ResearchPaper> papers;
    // Список подписчиков-наблюдателей
    private List<Observer> subscribers;

    // Конструктор — создаёт пустой журнал с уникальным id
    public Journal(String name) {
        this.journalId = UUID.randomUUID().toString();
        this.name = name;
        this.papers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    // Опубликовать статью и уведомить всех подписчиков
    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            // Рассылаем уведомления всем подписчикам
            notifyObservers(p);
        }
    }

    // Подписать наблюдателя на журнал
    @Override
    public void subscribe(Observer o) {
        if (o != null && !subscribers.contains(o)) {
            subscribers.add(o);
        }
    }

    // Отписать наблюдателя от журнала
    @Override
    public void unsubscribe(Observer o) {
        if (o != null) {
            subscribers.remove(o);
        }
    }

    // Уведомить всех подписчиков о новой статье
    @Override
    public void notifyObservers(ResearchPaper paper) {
        for (Observer obs : subscribers) {
            obs.update(paper);
        }
    }

    // Получить список статей журнала
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    // Получить название журнала — нужно для отображения в demo
    public String getName() {
        return name;
    }

    // Строковое представление журнала
    @Override
    public String toString() {
        return "Journal{name='" + name + "', papers=" + papers.size()
                + ", subscribers=" + subscribers.size() + "}";
    }

    // Два журнала равны, если совпадают их journalId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        Journal j = (Journal) o;
        return Objects.equals(journalId, j.journalId);
    }

    // Хэш-код по journalId
    @Override
    public int hashCode() {
        return Objects.hash(journalId);
    }
}
