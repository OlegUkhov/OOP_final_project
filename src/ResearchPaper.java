import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResearchPaper {
    private String paperId;
    private String title;
    private List<String> authors;
    private String journal;
    private int citations;
    private int pages;
    private Date datePublished;
    private String doi;

    public ResearchPaper(String title, String journal, int pages, Date datePublished) {
        this.paperId = UUID.randomUUID().toString();
        this.title = title;
        this.authors = new ArrayList<>();
        this.journal = journal;
        this.citations = 0;
        this.pages = pages;
        this.datePublished = datePublished;
        this.doi = generateDOI();
    }

    // Добавить автора
    public void addAuthor(String author) {
        if (author != null && !author.isEmpty() && !authors.contains(author)) {
            authors.add(author);
        }
    }

    // Получить цитату в нужном формате
    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) {
            return getPlainTextCitation();
        } else if (format == CitationFormat.BIBTEX) {
            return getBibtexCitation();
        }
        return "Unknown format";
    }

    private String getPlainTextCitation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i));
            if (i < authors.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" (").append(getYear()).append("). ");
        sb.append(title).append(". ");
        sb.append(journal).append(", pp. 1-").append(pages).append(".");
        return sb.toString();
    }

    private String getBibtexCitation() {
        StringBuilder sb = new StringBuilder();
        sb.append("@article{").append(paperId.substring(0, 8)).append(",\n");
        sb.append("  title={").append(title).append("},\n");
        sb.append("  author={");
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i));
            if (i < authors.size() - 1) {
                sb.append(" and ");
            }
        }
        sb.append("},\n");
        sb.append("  journal={").append(journal).append("},\n");
        sb.append("  pages={1--").append(pages).append("},\n");
        sb.append("  year={").append(getYear()).append("},\n");
        sb.append("  doi={").append(doi).append("}\n");
        sb.append("}");
        return sb.toString();
    }

    private String generateDOI() {
        return "10.1234/" + paperId.substring(0, 8);
    }

    private int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(datePublished);
        return cal.get(java.util.Calendar.YEAR);
    }

    public String getPaperId() {
        return paperId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return new ArrayList<>(authors);
    }

    public String getJournal() {
        return journal;
    }

    public int getCitations() {
        return citations;
    }

    public void addCitation() {
        this.citations++;
    }

    public int getPages() {
        return pages;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public String getDoi() {
        return doi;
    }

    @Override
    public String toString() {
        return "ResearchPaper{" +
                "title='" + title + '\'' +
                ", journal='" + journal + '\'' +
                ", citations=" + citations +
                ", pages=" + pages +
                ", year=" + getYear() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper paper = (ResearchPaper) o;
        return Objects.equals(paperId, paper.paperId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperId);
    }
}
