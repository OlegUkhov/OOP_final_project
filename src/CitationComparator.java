import java.util.Comparator;

public class CitationComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) {
            return 0;
        }
        // Статьи с больше цитированиями первыми (по убыванию)
        return Integer.compare(p2.getCitations(), p1.getCitations());
    }
}
