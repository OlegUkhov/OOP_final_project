// Sorts ResearchPaper objects by publication date descending
// Uses Date.compareTo() from ResearchPaper.getDatePublished()
import java.util.Comparator;

public class DateComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) return 0;
        // p2 before p1 gives descending order
        return p2.getDatePublished().compareTo(p1.getDatePublished());
    }
}
