// A published research article; central entity in the research subsystem
// Holds metadata used by comparators and h-index calculation
// getCitation() produces formatted references in PLAIN_TEXT or BIBTEX
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // AI told me it is a good way to generate unique IDs

public class ResearchPaper {

    private String paperId;
    private String title;
    private List<String> authors;
    private String journal;
    // Read by CitationComparator and ResearcherDecorator.calculateHIndex()
    private int citations;
    // Read by LengthComparator
    private int pages;
    // Read by DateComparator
    private Date datePublished;
    private String doi;

    // DOI is auto-generated from the first 8 chars of the UUID
    public ResearchPaper(String title, String journal, int pages, Date datePublished) {
        this.paperId = UUID.randomUUID().toString();
        this.title = title;
        this.authors = new ArrayList<>();
        this.journal = journal;
        this.citations = 0;
        this.pages = pages;
        this.datePublished = datePublished;
        this.doi = "10.1234/" + paperId.substring(0, 8);
    }

    public void addAuthor(String author) {
        if (author != null && !author.isEmpty() && !authors.contains(author)) {
            authors.add(author);
        }
    }

    // Used in Main demo to simulate citation accumulation
    public void addCitation() {
        this.citations++;
    }

    // Routes to the correct private method based on CitationFormat enum value
    public String getCitation(CitationFormat format) {
        if (format == CitationFormat.PLAIN_TEXT) {
            return getPlainTextCitation();
        } else if (format == CitationFormat.BIBTEX) {
            return getBibtexCitation();
        }
        return "Unknown format";
    }

    // Produces Author1, Author2 (year). Title. Journal, pp. 1-N.
    private String getPlainTextCitation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i));
            if (i < authors.size() - 1) sb.append(", ");
        }
        sb.append(" (").append(getYear()).append("). ");
        sb.append(title).append(". ");
        sb.append(journal).append(", pp. 1-").append(pages).append(".");
        return sb.toString();
    }

    // Produces a standard BibTeX article entry; authors joined with " and "
    private String getBibtexCitation() {
        StringBuilder sb = new StringBuilder();
        sb.append("@article{").append(paperId.substring(0, 8)).append(",\n");
        sb.append("  title={").append(title).append("},\n");
        sb.append("  author={");
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

    private int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(datePublished);
        return cal.get(java.util.Calendar.YEAR);
    }

    // Read by ResearcherDecorator.calculateHIndex() and CitationComparator
    public int getCitations() {
        return citations;
    }

    // Read by LengthComparator
    public int getPages() {
        return pages;
    }

    // Read by DateComparator
    public Date getDatePublished() {
        return datePublished;
    }

    // Read by User.update() to print the notification message
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ResearchPaper{title='" + title + "', journal='" + journal
                + "', citations=" + citations + ", pages=" + pages
                + ", year=" + getYear() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper p = (ResearchPaper) o;
        return Objects.equals(paperId, p.paperId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperId);
    }
}
