import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ResearcherDecorator implements Researcher {

    protected Researcher researcher;
    protected String ownerId;
    protected List<ResearchProject> projects;

    public ResearcherDecorator(Researcher researcher, String ownerId) {
        this.researcher = researcher;
        this.ownerId = ownerId;
        this.projects = new ArrayList<>();
    }

    @Override
    public void publishPaper(ResearchPaper p) {
        if (p == null) return;
        p.setOwnerId(ownerId);
        DataStorage.getInstance().addPaper(p);

        News announcement = new News(
                "New Research Published",
                "Paper \"" + p.getTitle() + "\" has been published.",
                "Research"
        );
        DataStorage.getInstance().publishNews(announcement);

        if (researcher != null) {
            researcher.publishPaper(p);
        }
    }

    @Override
    public int calculateHIndex() {
        List<ResearchPaper> papers = getPapers();
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

    @Override
    public void leadProject(ResearchProject rp) {
        if (rp == null) return;
        try {
            rp.addParticipant(this);
            if (!projects.contains(rp)) {
                projects.add(rp);
                DataStorage.getInstance().addProject(rp);
            }
        } catch (NotResearcherException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sorted = new ArrayList<>(getPapers());
        if (sorted.isEmpty()) {
            System.out.println("No papers yet.");
            return;
        }
        if (c != null) sorted.sort(c);
        System.out.println("=== Research Papers ===");
        for (ResearchPaper p : sorted) {
            System.out.println(p);
        }
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return DataStorage.getInstance().getPapersByOwner(ownerId);
    }
}
