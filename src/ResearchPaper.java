// Научная статья — центральная сущность исследовательской системы.
// Содержит метаданные (авторы, журнал, цитирования, страницы, дата, DOI)
// и умеет генерировать ссылку в формате Plain Text или BibTeX.
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResearchPaper {

    // Уникальный идентификатор статьи
    private String paperId;
    // Название статьи
    private String title;
    // Список авторов
    private List<String> authors;
    // Название журнала, в котором опубликована статья
    private String journal;
    // Количество цитирований
    private int citations;
    // Количество страниц
    private int pages;
    // Дата публикации
    private Date datePublished;
    // Цифровой идентификатор объекта (DOI)
    private String doi;

    // Конструктор — генерирует уникальный id и DOI автоматически
    public ResearchPaper(String title, String journal, int pages, Date datePublished) {
        this.paperId = UUID.randomUUID().toString();
        this.title = title;
        this.authors = new ArrayList<>();
        this.journal = journal;
        this.citations = 0;
        this.pages = pages;
        this.datePublished = datePublished;
        // DOI генерируется на основе первых 8 символов уникального id
        this.doi = "10.1234/" + paperId.substring(0, 8);
    }

    // Добавить автора статьи (нужен для заполнения списка авторов извне)
    public void addAuthor(String author) {
        if (author != null && !author.isEmpty() && !authors.contains(author)) {
            authors.add(author);
        }
    }

    // Увеличить счётчик цитирований на 1 (нужен для имитации цитирований в demo)
    public void addCitation() {
        this.citations++;
    }

    // Получить строку-ссылку в формате Plain Text или BibTeX
    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) {
            return getPlainTextCitation();
        } else if (format == CitationFormat.BIBTEX) {
            return getBibtexCitation();
        }
        return "Unknown format";
    }

    // Сформировать ссылку в формате Plain Text
    private String getPlainTextCitation() {
        StringBuilder sb = new StringBuilder();
        // Перечисляем авторов через запятую
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i));
            if (i < authors.size() - 1) sb.append(", ");
        }
        sb.append(" (").append(getYear()).append("). ");
        sb.append(title).append(". ");
        sb.append(journal).append(", pp. 1-").append(pages).append(".");
        return sb.toString();
    }

    // Сформировать ссылку в формате BibTeX
    private String getBibtexCitation() {
        StringBuilder sb = new StringBuilder();
        sb.append("@article{").append(paperId.substring(0, 8)).append(",\n");
        sb.append("  title={").append(title).append("},\n");
        sb.append("  author={");
        // Авторы перечисляются через " and "
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i));
            if (i < authors.size() - 1) sb.append(" and ");
        }
        sb.append("},\n");
        sb.append("  journal={").append(journal).append("},\n");
        sb.append("  pages={1--").append(pages).append("},\n");
        sb.append("  year={").append(getYear()).append("},\n");
        sb.append("  doi={").append(doi).append("}\n");
        sb.append("}");
        return sb.toString();
    }

    // Получить год публикации из даты
    private int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(datePublished);
        return cal.get(java.util.Calendar.YEAR);
    }

    // Геттер количества цитирований — нужен для h-index и компараторов
    public int getCitations() {
        return citations;
    }

    // Геттер количества страниц — нужен для LengthComparator
    public int getPages() {
        return pages;
    }

    // Геттер даты публикации — нужен для DateComparator
    public Date getDatePublished() {
        return datePublished;
    }

    // Геттер названия — нужен для отображения уведомлений (User.update)
    public String getTitle() {
        return title;
    }

    // Строковое представление статьи
    @Override
    public String toString() {
        return "ResearchPaper{title='" + title + "', journal='" + journal
                + "', citations=" + citations + ", pages=" + pages
                + ", year=" + getYear() + "}";
    }

    // Две статьи равны, если совпадают их paperId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper p = (ResearchPaper) o;
        return Objects.equals(paperId, p.paperId);
    }

    // Хэш-код по paperId
    @Override
    public int hashCode() {
        return Objects.hash(paperId);
    }
}
