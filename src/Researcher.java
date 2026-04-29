import java.util.Comparator;
import java.util.List;

public interface Researcher {
    public void publishPaper(ResearchPaper p);
    public int calculateHIndex();
    public void leadProject(ResearchProject rp);
    public void printPapers(Comparator<ResearchPaper> c);
    public List<ResearchPaper> getPapers();
}
