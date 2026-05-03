// Sorts ResearchPaper objects by citation count descending
// Used with Researcher.printPapers() and DataStorage sorting methods
import java.util.Comparator;

public class CitationComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) return 0;
        // p2 before p1 gives descending order
        return Integer.compare(p2.getCitations(), p1.getCitations());
    }
}
