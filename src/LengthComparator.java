import java.util.Comparator;

public class LengthComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) {
            return 0;
        }
        // Статьи с больше страниц первыми (по убыванию)
        return Integer.compare(p2.getPages(), p1.getPages());
    }
}
