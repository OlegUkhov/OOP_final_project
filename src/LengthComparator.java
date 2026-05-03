// Sorts ResearchPaper objects by page count descending (longest first)
// Uses ResearchPaper.getPages() for comparison
import java.util.Comparator;

public class LengthComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) return 0;
        // p2 before p1 gives descending order
        return Integer.compare(p2.getPages(), p1.getPages());
    }
}
