// Абстрактный декоратор исследователя (паттерн Decorator).
// Хранит список статей и проектов, реализует все методы интерфейса Researcher.
// Конкретные декораторы: TeacherResearcher и StudentResearcher.
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ResearcherDecorator implements Researcher {

    // Обёртываемый объект-исследователь (может быть null, если это базовый декоратор)
    protected Researcher researcher;
    // Список научных статей данного исследователя
    protected List<ResearchPaper> papers;
    // Список проектов, которые ведёт данный исследователь
    protected List<ResearchProject> projects;

    // Конструктор — принимает другой Researcher для делегирования (или null)
    public ResearcherDecorator(Researcher researcher) {
        this.researcher = researcher;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    // Добавить статью в список (делегирует вниз по цепочке декораторов)
    @Override
    public void publishPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
            // Если есть внутренний исследователь — делегируем и ему
            if (researcher != null) {
                researcher.publishPaper(p);
            }
        }
    }

    // Вычислить h-index: наибольшее h, при котором h статей имеют >= h цитирований
    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;

        // Собираем количество цитирований каждой статьи
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : papers) {
            citations.add(p.getCitations());
        }

        // Сортируем по убыванию
        citations.sort((a, b) -> Integer.compare(b, a));

        int hIndex = 0;
        for (int i = 0; i < citations.size(); i++) {
            // h-кандидат: i+1 статей с >= (i+1) цитированиями
            if (citations.get(i) >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    // Возглавить проект: добавить себя как участника и сохранить ссылку
    @Override
    public void leadProject(ResearchProject rp) {
        if (rp == null) return;
        try {
            // Добавляем себя в список участников проекта
            rp.addParticipant(this);
            if (!projects.contains(rp)) {
                projects.add(rp);
            }
        } catch (NotResearcherException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Вывести статьи в порядке, заданном компаратором
    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        if (papers.isEmpty()) {
            System.out.println("No papers yet.");
            return;
        }
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        // Применяем переданный компаратор для сортировки
        if (c != null) sorted.sort(c);
        System.out.println("=== Research Papers ===");
        for (ResearchPaper p : sorted) {
            System.out.println(p);
        }
    }

    // Получить копию списка статей
    @Override
    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }
}
