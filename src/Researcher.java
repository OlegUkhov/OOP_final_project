// Researcher role interface - implemented via Decorator pattern
// TeacherResearcher and StudentResearcher both implement this through ResearcherDecorator
// GraduateStudent.setSupervisor() requires a Researcher reference with h-index >= 3
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public interface Researcher extends Serializable {

    // Adds the paper to the internal papers list in ResearcherDecorator
    void publishPaper(ResearchPaper p);

    // Calculates h-index from the papers list; algorithm lives in ResearcherDecorator
    int calculateHIndex();

    // Adds this researcher as a participant in the project via ResearchProject.addParticipant()
    void leadProject(ResearchProject rp);

    // Sorts the papers list using the given comparator and prints each paper
    // Comparators available: CitationComparator DateComparator LengthComparator
    void printPapers(Comparator<ResearchPaper> c);

    // Returns a copy of the internal papers list
    List<ResearchPaper> getPapers();
}
