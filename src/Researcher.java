// Интерфейс исследователя (паттерн Decorator).
// Реализуется классами TeacherResearcher и StudentResearcher через ResearcherDecorator.
// Определяет основные операции с научными публикациями и проектами.
import java.util.Comparator;
import java.util.List;

public interface Researcher {

    // Опубликовать научную статью
    void publishPaper(ResearchPaper p);

    // Вычислить h-index исследователя
    int calculateHIndex();

    // Возглавить исследовательский проект
    void leadProject(ResearchProject rp);

    // Напечатать список статей, отсортированный компаратором
    void printPapers(Comparator<ResearchPaper> c);

    // Получить список всех статей исследователя
    List<ResearchPaper> getPapers();
}
