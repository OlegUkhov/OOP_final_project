// Abstract decorator for the Researcher interface (Decorator pattern)
// Holds the shared papers and projects lists used by TeacherResearcher and StudentResearcher
// The h-index algorithm and printPapers logic live here so subclasses do not duplicate them
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ResearcherDecorator implements Researcher {

    // Wrapped Researcher; can be null when the decorator is the first in the chain
    protected Researcher researcher;
    // Papers owned by this researcher; used for h-index and printPapers
    protected List<ResearchPaper> papers;
    // Projects this researcher leads or participates in
    protected List<ResearchProject> projects;

    public ResearcherDecorator(Researcher researcher) {
        this.researcher = researcher;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    // Adds paper locally then delegates down the chain if there is a wrapped researcher
    @Override
    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            if (researcher != null) {
                researcher.publishPaper(p);
            }
        }
    }

    // Standard h-index definition: largest h where h papers each have at least h citations
    // Sorts citation counts descending then counts qualifying papers
    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;

        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : papers) {
            citations.add(p.getCitations());
        }
        citations.sort((a, b) -> Integer.compare(b, a));

        int hIndex = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    // Calls ResearchProject.addParticipant(this) which expects a Researcher
    // NotResearcherException would only be thrown if addParticipant receives null
    @Override
    public void leadProject(ResearchProject rp) {
        if (rp == null) return;
        try {
            rp.addParticipant(this);
            if (!projects.contains(rp)) {
                projects.add(rp);
            }
        } catch (NotResearcherException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Sorts a copy of papers so the original order is preserved
    // Comparator c comes from the caller: CitationComparator DateComparator or LengthComparator
    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        if (papers.isEmpty()) {
            System.out.println("No papers yet.");
            return;
        }
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        if (c != null) sorted.sort(c);
        System.out.println("=== Research Papers ===");
        for (ResearchPaper p : sorted) {
            System.out.println(p);
        }
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }
}
