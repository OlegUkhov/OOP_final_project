// Точка входа — демонстрация работы University Research System.
// Показывает все основные сценарии: аутентификацию, курсы, оценки,
// исследования, паттерны Observer и Decorator, техподдержку.
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=".repeat(65));
        System.out.println("  University Research System — Demo");
        System.out.println("=".repeat(65));

        // ── 1. DataStorage (Singleton) ──────────────────────────────────
        System.out.println("\n[1] DataStorage (Singleton)");
        DataStorage storage = DataStorage.getInstance();
        System.out.println(storage);

        // ── 2. Создание пользователей ────────────────────────────────────
        System.out.println("\n[2] Creating users");

        // Создаём администратора
        Admin admin = new Admin(
            "ADM001", "Alice", "Johnson", "alice@uni.edu", "pass",
            Language.EN, "EMP001", 60000, "Administration"
        );
        storage.addUser(admin);
        System.out.println("Admin: " + admin);

        // Создаём профессора (позиция PROFESSOR — он автоматически Researcher)
        Teacher professor = new Teacher(
            "TCH001", "Bob", "Smith", "bob@uni.edu", "pass",
            Language.EN, "EMP002", 70000, "Computer Science",
            TeacherPosition.PROFESSOR
        );
        storage.addUser(professor);
        System.out.println("Professor: " + professor);

        // Создаём менеджера
        Manager manager = new Manager(
            "MGR001", "Charlie", "Brown", "charlie@uni.edu", "pass",
            Language.EN, "EMP003", 55000, "Academic Affairs",
            ManagerType.DEPARTMENT
        );
        storage.addUser(manager);
        System.out.println("Manager: " + manager);

        // Создаём бакалавра
        Student student = new Student(
            "STU001", "David", "Wilson", "david@uni.edu", "pass",
            Language.EN, "STU001"
        );
        // Устанавливаем GPA студенту
        student.setGpa(3.85);
        storage.addUser(student);
        System.out.println("Student: " + student);

        // Создаём магистранта
        GraduateStudent graduate = new GraduateStudent(
            "STU002", "Emma", "Davis", "emma@uni.edu", "pass",
            Language.EN, "STU002"
        );
        // Устанавливаем GPA магистранту
        graduate.setGpa(3.95);
        storage.addUser(graduate);
        System.out.println("Graduate: " + graduate);

        // ── 3. Курсы ─────────────────────────────────────────────────────
        System.out.println("\n[3] Creating courses");

        // Создаём курс Java и назначаем профессора
        Course javaCourse = new Course("CMP101", "Advanced Java", 3, CourseType.MAJOR);
        manager.assignCourse(professor, javaCourse);
        storage.addCourse(javaCourse);

        // Создаём курс Algorithms и назначаем профессора
        Course algoCourse = new Course("CMP201", "Algorithms", 4, CourseType.MAJOR);
        manager.assignCourse(professor, algoCourse);
        storage.addCourse(algoCourse);

        // Добавляем занятия в курс Java
        javaCourse.addLesson(new Lesson("L01", "OOP", LessonType.LECTURE));
        javaCourse.addLesson(new Lesson("L02", "Patterns", LessonType.PRACTICE));
        System.out.println("Java course: " + javaCourse);

        // ── 4. Регистрация на курсы ──────────────────────────────────────
        System.out.println("\n[4] Student registration");
        try {
            // Менеджер одобряет регистрацию студента на оба курса
            manager.approveRegistration(student, javaCourse);
            manager.approveRegistration(student, algoCourse);
            System.out.println("Total credits: " + student.getTotalCredits());
        } catch (CourseOverloadException e) {
            System.err.println("Overload: " + e.getMessage());
        }

        // ── 5. Оценки ────────────────────────────────────────────────────
        System.out.println("\n[5] Grading");

        // Преподаватель выставляет оценку студенту за курс Java
        Mark mark1 = new Mark(95, 92, 98, student, javaCourse);
        professor.putMark(student, javaCourse, mark1);

        // Преподаватель выставляет оценку за курс Algorithms
        Mark mark2 = new Mark(88, 85, 90, student, algoCourse);
        professor.putMark(student, algoCourse, mark2);

        // Выводим все оценки студента
        for (Mark m : student.viewMarks()) {
            System.out.println(m);
        }

        // ── 6. Рейтинг преподавателя ─────────────────────────────────────
        System.out.println("\n[6] Rating teacher");
        // Студент трижды оценивает преподавателя
        student.rateTeacher(professor, 5);
        student.rateTeacher(professor, 5);
        student.rateTeacher(professor, 4);
        System.out.println("Professor rating: " + String.format("%.2f", professor.getRating()));

        // ── 7. Жалоба ────────────────────────────────────────────────────
        System.out.println("\n[7] Complaint");
        // Преподаватель отправляет жалобу на студента
        Complaint complaint = professor.sendComplaint(student,
            "Late to class repeatedly", ComplaintUrgency.MEDIUM);
        System.out.println(complaint);

        // ── 8. Исследователь (Decorator Pattern) ─────────────────────────
        System.out.println("\n[8] Researcher (Decorator)");

        // Оборачиваем профессора в роль исследователя
        TeacherResearcher resProf = new TeacherResearcher(professor);

        // Создаём первую статью и добавляем авторов и цитирования
        ResearchPaper paper1 = new ResearchPaper(
            "Machine Learning in Java", "Journal of Software Eng.", 25, new Date());
        paper1.addAuthor("Bob Smith");
        // Добавляем 15 цитирований вручную
        for (int i = 0; i < 15; i++) paper1.addCitation();
        resProf.publishPaper(paper1);
        storage.addPaper(paper1);

        // Создаём вторую статью
        ResearchPaper paper2 = new ResearchPaper(
            "Design Patterns in Distributed Systems", "ACM Computing Surveys", 32, new Date());
        paper2.addAuthor("Bob Smith");
        // Добавляем 28 цитирований
        for (int i = 0; i < 28; i++) paper2.addCitation();
        resProf.publishPaper(paper2);
        storage.addPaper(paper2);

        // Создаём третью статью (нужна, чтобы h-index достиг 3 для назначения руководителя)
        ResearchPaper paper3 = new ResearchPaper(
            "Neural Networks Overview", "IEEE Transactions", 18, new Date());
        paper3.addAuthor("Bob Smith");
        // Добавляем 10 цитирований
        for (int i = 0; i < 10; i++) paper3.addCitation();
        resProf.publishPaper(paper3);
        storage.addPaper(paper3);

        // Вычисляем h-index профессора
        System.out.println("h-index: " + resProf.calculateHIndex());

        // ── 9. Supervisor для магистранта ────────────────────────────────
        System.out.println("\n[9] Assigning supervisor");
        try {
            // Назначаем профессора-исследователя научным руководителем
            graduate.setSupervisor(resProf);
            System.out.println("Supervisor assigned: " + graduate);
        } catch (LowHIndexException e) {
            System.err.println(e.getMessage());
        }

        // Магистрант публикует дипломную работу
        ResearchPaper diploma = new ResearchPaper(
            "Thesis: Advanced ML", "University Repository", 120, new Date());
        diploma.addAuthor("Emma Davis");
        graduate.publishDiplomaPaper(diploma);
        System.out.println("Diploma papers: " + graduate.getDiplomaPapers().size());

        // ── 10. Печать статей с сортировкой ─────────────────────────────
        System.out.println("\n[10] Papers sorted by citations");
        // Выводим статьи профессора, сортируя по убыванию цитирований
        resProf.printPapers(new CitationComparator());

        // ── 11. Форматы цитирования ──────────────────────────────────────
        System.out.println("\n[11] Citation formats");
        // Plain Text
        System.out.println(paper1.getCitation(CitationFormat.PLAIN_TEXT));
        // BibTeX
        System.out.println(paper2.getCitation(CitationFormat.BIBTEX));

        // ── 12. Observer Pattern — журнал ────────────────────────────────
        System.out.println("\n[12] Journal (Observer Pattern)");

        // Создаём журнал и добавляем подписчиков
        Journal journal = new Journal("Journal of Computer Science");
        storage.addJournal(journal);
        journal.subscribe(student);
        journal.subscribe(graduate);
        System.out.println("Journal: " + journal);

        // Публикация статьи — автоматически уведомляет всех подписчиков
        ResearchPaper journalPaper = new ResearchPaper(
            "Cloud Computing Trends", "Journal of Computer Science", 45, new Date());
        journalPaper.addAuthor("Research Team");
        journal.publishPaper(journalPaper);

        // ── 13. Исследовательский проект ─────────────────────────────────
        System.out.println("\n[13] Research project");
        ResearchProject project = new ResearchProject("AI in Education");
        // Профессор-исследователь возглавляет проект
        resProf.leadProject(project);
        project.addPaper(paper1);
        storage.addProject(project);
        System.out.println(project);

        // ── 14. Студенческая организация ────────────────────────────────
        System.out.println("\n[14] Student organization");
        StudentOrganization org = new StudentOrganization("CS Club");
        // Студенты вступают в организацию
        student.joinOrganization(org);
        graduate.joinOrganization(org);
        // Назначаем главу организации
        org.setHead(student);
        System.out.println(org);
        System.out.println("Head: " + org.getHead().getFirstName() + " " + org.getHead().getLastName());

        // ── 15. Новости ──────────────────────────────────────────────────
        System.out.println("\n[15] News");

        // Менеджер добавляет новость с темой Research — она автоматически закрепляется
        News researchNews = new News("New AI Lab", "University opens AI lab", "Research");
        manager.manageNews(researchNews);
        storage.addNews(researchNews);
        researchNews.addComment("Great initiative!");
        System.out.println(researchNews);

        // ── 16. Техподдержка ─────────────────────────────────────────────
        System.out.println("\n[16] Tech support");

        // Создаём специалиста техподдержки
        TechSupportSpecialist tech = new TechSupportSpecialist(
            "TEC001", "Frank", "Miller", "frank@uni.edu", "pass",
            Language.EN, "EMP004", 40000, "IT"
        );
        storage.addUser(tech);

        // Студент подаёт запрос
        Request req = new Request("REQ001", "Fix projector in room 101", student);
        System.out.println("Created: " + req);

        // Специалист принимает запрос (VIEWED → ACCEPTED)
        tech.acceptRequest(req);
        System.out.println("After accept: " + req.getStatus());

        // Специалист отмечает как выполненный (ACCEPTED → DONE)
        tech.markAsDone(req);
        System.out.println("After done: " + req.getStatus());

        // ── 17. Сообщения ────────────────────────────────────────────────
        System.out.println("\n[17] Messaging");
        // Профессор отправляет сообщение менеджеру
        professor.sendMessage(manager, "Can we schedule extra labs?");

        // ── 18. Отчёт и транскрипт ───────────────────────────────────────
        System.out.println("\n[18] Reports");
        System.out.println(manager.createStatisticalReport());
        System.out.println(student.getTranscript());

        // ── 19. Топ-исследователь ────────────────────────────────────────
        System.out.println("\n[19] Top cited researcher");
        Researcher top = storage.getTopCitedResearcher();
        if (top instanceof TeacherResearcher) {
            Teacher t = ((TeacherResearcher) top).getTeacher();
            System.out.println("Top: " + t.getFirstName() + " " + t.getLastName()
                + " (h-index=" + top.calculateHIndex() + ")");
        }

        // ── Итог ─────────────────────────────────────────────────────────
        System.out.println("\n" + "=".repeat(65));
        System.out.println("Final state: " + storage);
        System.out.println("Demo completed!");
    }
}
