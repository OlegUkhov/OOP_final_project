import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ResearcherDecorator implements Researcher {
    protected Researcher researcher;
    protected List<ResearchPaper> papers;
    protected List<ResearchProject> projects;

    public ResearcherDecorator(Researcher researcher) {
        this.researcher = researcher;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    @Override
    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            // Делегируем внутреннему объекту (если у него есть публикация)
            if (researcher != null) {
                researcher.publishPaper(p);
            }
        }
    }

    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) {
            return 0;
        }
        
        // Алгоритм h-index:
        // h-index — это наибольшее число h такое, что h статей имеют >= h цитирований
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : papers) {
            citations.add(p.getCitations());
        }
        
        // Сортируем по убыванию
        citations.sort((a, b) -> Integer.compare(b, a));
        
        int hIndex = 0;
        for (int i = 0; i < citations.size(); i++) {
            int h = i + 1;  // h-index кандидат
            if (citations.get(i) >= h) {
                hIndex = h;
            } else {
                break;
            }
        }
        
        return hIndex;
    }

    @Override
    public void leadProject(ResearchProject rp) {
        if (rp != null) {
            try {
                rp.addParticipant(this);
                if (!projects.contains(rp)) {
                    projects.add(rp);
                }
            } catch (NotResearcherException e) {
                System.err.println("Error adding participant: " + e.getMessage());
            }
        }
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        if (papers.isEmpty()) {
            System.out.println("No papers published yet.");
            return;
        }
        
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        if (c != null) {
            sorted.sort(c);
        }
        
        System.out.println("=== Research Papers ===");
        for (ResearchPaper p : sorted) {
            System.out.println(p);
        }
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    public List<ResearchProject> getProjects() {
        return new ArrayList<>(projects);
    }
}
