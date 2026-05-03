import java.util.Comparator;

public class DateComparator implements Comparator<ResearchPaper> {
    @Override
    public int compare(ResearchPaper p1, ResearchPaper p2) {
        if (p1 == null || p2 == null) {
            return 0;
        }
        // Новые статьи первыми (по убыванию даты)
        return p2.getDatePublished().compareTo(p1.getDatePublished());
    }
}
