import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("  University Research System - Demo");
        System.out.println("=".repeat(70));
        
        // 1. Инициализация DataStorage (Singleton)
        DataStorage storage = DataStorage.getInstance();
        System.out.println("\n[1] Initializing DataStorage (Singleton)...");
        System.out.println(storage);
        
        // 2. Создание пользователей
        System.out.println("\n[2] Creating users...");
        
        // Админ
        Admin admin = new Admin(
            "ADM001", "Alice", "Johnson", "alice@uni.edu", "pass123",
            Language.EN, "EMP001", 60000, "Administration"
        );
        storage.addUser(admin);
        System.out.println("✓ Admin: " + admin);
        
        // Преподаватель (Профессор)
        Teacher professor = new Teacher(
            "TCH001", "Bob", "Smith", "bob@uni.edu", "pass123",
            Language.EN, "EMP002", 70000, "Computer Science",
            TeacherPosition.PROFESSOR
        );
        storage.addUser(professor);
        System.out.println("✓ Professor: " + professor);
        
        // Менеджер
        Manager manager = new Manager(
            "MGR001", "Charlie", "Brown", "charlie@uni.edu", "pass123",
            Language.EN, "EMP003", 55000, "Academic Affairs",
            ManagerType.DEPARTMENT
        );
        storage.addUser(manager);
        System.out.println("✓ Manager: " + manager);
        
        // Студент бакалавриата
        Student student1 = new Student(
            "STU001", "David", "Wilson", "david@uni.edu", "pass123",
            Language.EN, "STU001"
        );
        student1.setGpa(3.85);
        storage.addUser(student1);
        System.out.println("✓ Student 1: " + student1);
        
        // Выпускник (Магистрант)
        GraduateStudent graduate = new GraduateStudent(
            "STU002", "Emma", "Davis", "emma@uni.edu", "pass123",
            Language.EN, "STU002"
        );
        graduate.setGpa(3.95);
        storage.addUser(graduate);
        System.out.println("✓ Graduate Student: " + graduate);
        
        // 3. Создание курсов
        System.out.println("\n[3] Creating courses...");
        
        Course javaCourse = new Course(
            "CMP101", "Advanced Java", 3, CourseType.MAJOR
        );
        manager.assignCourse(professor, javaCourse);
        storage.addCourse(javaCourse);
        System.out.println("✓ Course: " + javaCourse);
        
        Course algosCourse = new Course(
            "CMP201", "Algorithms", 4, CourseType.MAJOR
        );
        manager.assignCourse(professor, algosCourse);
        storage.addCourse(algosCourse);
        System.out.println("✓ Course: " + algosCourse);
        
        // 4. Добавление занятий
        System.out.println("\n[4] Adding lessons...");
        javaCourse.addLesson(new Lesson("LES001", "Object-Oriented Programming", LessonType.LECTURE));
        javaCourse.addLesson(new Lesson("LES002", "Design Patterns", LessonType.PRACTICE));
        System.out.println("✓ Added 2 lessons to Java course");
        
        // 5. Студенты регистрируются на курсы
        System.out.println("\n[5] Students registering for courses...");
        try {
            manager.approveRegistration(student1, javaCourse);
            System.out.println("✓ " + student1.getFirstName() + " registered for Java course");
            
            manager.approveRegistration(student1, algosCourse);
            System.out.println("✓ " + student1.getFirstName() + " registered for Algorithms course");
            System.out.println("  Total credits: " + student1.getTotalCredits());
        } catch (CourseOverloadException e) {
            System.err.println("✗ Registration failed: " + e.getMessage());
        }
        
        // 6. Выставление оценок
        System.out.println("\n[6] Grading...");
        Mark mark1 = new Mark(95, 92, 98, student1, javaCourse);
        student1.addMark(mark1);
        System.out.println("✓ Mark for Java: " + mark1);
        
        Mark mark2 = new Mark(88, 85, 90, student1, algosCourse);
        student1.addMark(mark2);
        System.out.println("✓ Mark for Algorithms: " + mark2);
        
        // 7. Студент оценивает преподавателя
        System.out.println("\n[7] Student rating teacher...");
        student1.rateTeacher(professor, 5);
        student1.rateTeacher(professor, 5);
        student1.rateTeacher(professor, 4);
        System.out.println("✓ Professor rating: " + String.format("%.2f", professor.getRating()));
        
        // 8. Преподаватель отправляет жалобу
        System.out.println("\n[8] Teacher sending complaint...");
        Complaint complaint = professor.sendComplaint(
            student1,
            "Student was late to class multiple times",
            ComplaintUrgency.MEDIUM
        );
        System.out.println("✓ Complaint sent: " + complaint);
        
        // 9. Создание исследователя из преподавателя
        System.out.println("\n[9] Creating researcher from teacher (Decorator Pattern)...");
        TeacherResearcher researcherProf = new TeacherResearcher(professor);
        
        // Публикация статей
        System.out.println("\n[10] Publishing research papers...");
        ResearchPaper paper1 = new ResearchPaper(
            "Machine Learning in Java",
            "Journal of Software Engineering",
            25,
            new Date()
        );
        paper1.addAuthor("Bob Smith");
        paper1.addAuthor("Charlie Brown");
        // Добавляем цитирования
        for (int i = 0; i < 15; i++) {
            paper1.addCitation();
        }
        researcherProf.publishPaper(paper1);
        storage.addPaper(paper1);
        System.out.println("✓ Paper 1: " + paper1);
        
        ResearchPaper paper2 = new ResearchPaper(
            "Design Patterns in Distributed Systems",
            "ACM Computing Surveys",
            32,
            new Date()
        );
        paper2.addAuthor("Bob Smith");
        for (int i = 0; i < 28; i++) {
            paper2.addCitation();
        }
        researcherProf.publishPaper(paper2);
        storage.addPaper(paper2);
        System.out.println("✓ Paper 2: " + paper2);
        
        // 11. Вычисление h-index
        System.out.println("\n[11] Calculating h-index...");
        int hIndex = researcherProf.calculateHIndex();
        System.out.println("✓ Professor's h-index: " + hIndex);
        
        // 12. Назначение supervisor для выпускника
        System.out.println("\n[12] Assigning supervisor to graduate student...");
        try {
            graduate.setSupervisor(researcherProf);
            System.out.println("✓ Supervisor assigned to graduate student");
        } catch (LowHIndexException e) {
            System.err.println("✗ " + e.getMessage());
        }
        
        // 13. Публикация дипломной работы
        System.out.println("\n[13] Publishing diploma paper...");
        ResearchPaper diplomaPaper = new ResearchPaper(
            "Thesis: Advanced ML Algorithms",
            "University Repository",
            120,
            new Date()
        );
        diplomaPaper.addAuthor("Emma Davis");
        graduate.publishDiplomaPaper(diplomaPaper);
        System.out.println("✓ Diploma paper published");
        
        // 14. Создание журнала и Observer pattern
        System.out.println("\n[14] Journal subscription (Observer Pattern)...");
        Journal journal = new Journal("Journal of Computer Science");
        storage.addJournal(journal);
        
        journal.subscribe(student1);
        journal.subscribe(graduate);
        System.out.println("✓ " + journal.getName() + " has " + journal.getSubscriberCount() + " subscribers");
        
        // 15. Публикация статьи в журнале уведомляет подписчиков
        System.out.println("\n[15] Publishing paper in journal (notifying subscribers)...");
        ResearchPaper journalPaper = new ResearchPaper(
            "Cloud Computing Trends",
            "Journal of Computer Science",
            45,
            new Date()
        );
        journalPaper.addAuthor("Research Team");
        journal.publishPaper(journalPaper);
        System.out.println("✓ Paper published in journal");
        
        // 16. Печать статей с сортировкой
        System.out.println("\n[16] Printing research papers (sorted by citations)...");
        researcherProf.printPapers(new CitationComparator());
        
        // 17. Цитирование статей
        System.out.println("\n[17] Getting citations in different formats...");
        System.out.println("\nPlain Text Citation:");
        System.out.println(paper1.getCitation(CitationFormat.PLAIN_TEXT));
        System.out.println("\nBibTeX Citation:");
        System.out.println(paper2.getCitation(CitationFormat.BIBTEX));
        
        // 18. Студенческая организация
        System.out.println("\n[18] Student Organization...");
        StudentOrganization org = new StudentOrganization("Computer Science Club");
        student1.joinOrganization(org);
        graduate.joinOrganization(org);
        org.setHead(student1);
        System.out.println("✓ Organization: " + org);
        System.out.println("  Members: " + org.getMembers().size() + ", Head: " + org.getHead().getFirstName());
        
        // 19. Новости
        System.out.println("\n[19] News management...");
        News news1 = new News(
            "New Research Initiative Launched",
            "The university launches a new AI research lab",
            "Research"
        );
        manager.manageNews(news1);
        manager.pinResearchNews(news1);
        storage.addNews(news1);
        System.out.println("✓ News: " + news1);
        System.out.println("  Is pinned: " + news1.isPinned());
        
        news1.addComment("Exciting news!");
        news1.addComment("Great initiative for the university!");
        System.out.println("  Comments: " + news1.getComments().size());
        
        // 20. Техподдержка
        System.out.println("\n[20] Tech Support workflow...");
        Request request = new Request(
            "REQ001",
            "Fix the projector in room 101",
            student1
        );
        System.out.println("✓ Request created: " + request);
        
        TechSupportSpecialist techSupport = new TechSupportSpecialist(
            "TEC001", "Frank", "Miller", "frank@uni.edu", "pass123",
            Language.EN, "EMP004", 40000, "IT Services"
        );
        storage.addUser(techSupport);
        
        // Принять запрос
        techSupport.acceptRequest(request);
        System.out.println("✓ Request accepted by tech support. Status: " + request.getStatus());
        
        // Пометить как выполненный
        techSupport.markAsDone(request);
        System.out.println("✓ Request marked as done. Status: " + request.getStatus());
        
        // 21. Обмен сообщениями между сотрудниками
        System.out.println("\n[21] Employee messaging...");
        professor.sendMessage(manager, "Can you help with course scheduling?");
        System.out.println("✓ Message sent from " + professor.getFirstName() + " to " + manager.getFirstName());
        
        // 22. Статистика
        System.out.println("\n[22] Statistical report...");
        String report = manager.createStatisticalReport();
        System.out.println(report);
        
        // 23. Transcript студента
        System.out.println("\n[23] Student transcript...");
        System.out.println(student1.getTranscript());
        
        // 24. Топ исследователь
        System.out.println("\n[24] Finding top cited researcher...");
        Researcher topResearcher = storage.getTopCitedResearcher();
        if (topResearcher instanceof TeacherResearcher) {
            TeacherResearcher tr = (TeacherResearcher) topResearcher;
            System.out.println("✓ Top Researcher: " + tr.getTeacher().getFirstName() +
                    " " + tr.getTeacher().getLastName() +
                    " (h-index: " + topResearcher.calculateHIndex() + ")");
        }
        
        // 25. Финальная информация о хранилище
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Final DataStorage State:");
        System.out.println(storage);
        System.out.println("=".repeat(70));
        System.out.println("\n✓ Demo completed successfully!");
    }
}