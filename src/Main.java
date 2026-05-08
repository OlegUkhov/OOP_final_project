// Entry point for the University Research System demo
// Each numbered section shows one feature of the system running in sequence
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("=".repeat(65));
        System.out.println("  University Research System - Demo");
        System.out.println("=".repeat(65));

        // DataStorage Singleton
        // getInstance() creates the single instance on first call; all sections share it
        System.out.println("\n[1] DataStorage (Singleton)");
        DataStorage storage = DataStorage.getInstance();
        System.out.println(storage);

        // Creating users
        System.out.println("\n[2] Creating users");

        Admin admin = new Admin(
            "ADM001", "Alice", "Johnson", "alice@uni.edu", "pass",
            Language.EN, "EMP001", 60000, "Administration"
        );
        storage.addUser(admin);
        System.out.println("Admin: " + admin);

        // PROFESSOR position makes DataStorage.getAllResearchers() wrap this teacher automatically
        Teacher professor = new Teacher(
            "TCH001", "Bob", "Smith", "bob@uni.edu", "pass",
            Language.EN, "EMP002", 70000, "Computer Science",
            TeacherPosition.PROFESSOR
        );
        storage.addUser(professor);
        System.out.println("Professor: " + professor);

        Manager manager = new Manager(
            "MGR001", "Charlie", "Brown", "charlie@uni.edu", "pass",
            Language.EN, "EMP003", 55000, "Academic Affairs",
            ManagerType.DEPARTMENT
        );
        storage.addUser(manager);
        System.out.println("Manager: " + manager);

        Student student = new Student(
            "STU001", "David", "Wilson", "david@uni.edu", "pass",
            Language.EN, "STU001"
        );
        student.setGpa(3.85);
        storage.addUser(student);
        System.out.println("Student: " + student);

        // GraduateStudent extends Student
        GraduateStudent graduate = new GraduateStudent(
            "STU002", "Emma", "Davis", "emma@uni.edu", "pass",
            Language.EN, "STU002"
        );
        graduate.setGpa(3.95);
        storage.addUser(graduate);
        System.out.println("Graduate: " + graduate);

        // Courses
        // Manager.assignCourse() calls Teacher.manageCourse() which also calls Course.addTeacher()
        System.out.println("\n[3] Creating courses");

        Course javaCourse = new Course("CMP101", "Advanced Java", 3, CourseType.MAJOR);
        manager.assignCourse(professor, javaCourse);
        storage.addCourse(javaCourse);

        Course algoCourse = new Course("CMP201", "Algorithms", 4, CourseType.MAJOR);
        manager.assignCourse(professor, algoCourse);
        storage.addCourse(algoCourse);

        javaCourse.addLesson(new Lesson("L01", "OOP", LessonType.LECTURE));
        javaCourse.addLesson(new Lesson("L02", "Patterns", LessonType.PRACTICE));
        System.out.println("Java course: " + javaCourse);

        // Student registration
        // approveRegistration() pre-checks credits then calls Student.registerForCourse()
        // CourseOverloadException is thrown if total would exceed 21
        System.out.println("\n[4] Student registration");
        try {
            manager.approveRegistration(student, javaCourse);
            manager.approveRegistration(student, algoCourse);
            System.out.println("Total credits: " + student.getTotalCredits());
        } catch (CourseOverloadException e) {
            System.err.println("Overload: " + e.getMessage());
        }

        // Grading
        // Teacher.putMark() pushes the Mark object into student via Student.addMark()
        System.out.println("\n[5] Grading");

        Mark mark1 = new Mark(95, 92, 98, student, javaCourse);
        professor.putMark(student, javaCourse, mark1);

        Mark mark2 = new Mark(88, 85, 90, student, algoCourse);
        professor.putMark(student, algoCourse, mark2);

        for (Mark m : student.viewMarks()) {
            System.out.println(m);
        }

        // Rating teacher
        // Student.rateTeacher() delegates to Teacher.addRating() which keeps a running average
        System.out.println("\n[6] Rating teacher");
        student.rateTeacher(professor, 5);
        student.rateTeacher(professor, 5);
        student.rateTeacher(professor, 4);
        System.out.println("Professor rating: " + String.format("%.2f", professor.getRating()));

        // Complaint
        // Teacher.sendComplaint() creates and returns a Complaint object
        System.out.println("\n[7] Complaint");
        Complaint complaint = professor.sendComplaint(student,
            "Late to class repeatedly", ComplaintUrgency.MEDIUM);
        System.out.println(complaint);

        // Researcher Decorator pattern
        // TeacherResearcher wraps professor; all paper and h-index logic is in ResearcherDecorator
        System.out.println("\n[8] Researcher (Decorator)");

        TeacherResearcher resProf = new TeacherResearcher(professor);

        ResearchPaper paper1 = new ResearchPaper(
            "Machine Learning in Java", "Journal of Software Eng.", 25, new Date());
        paper1.addAuthor("Bob Smith");
        for (int i = 0; i < 15; i++) paper1.addCitation();
        resProf.publishPaper(paper1);
        storage.addPaper(paper1);

        ResearchPaper paper2 = new ResearchPaper(
            "Design Patterns in Distributed Systems", "ACM Computing Surveys", 32, new Date());
        paper2.addAuthor("Bob Smith");
        for (int i = 0; i < 28; i++) paper2.addCitation();
        resProf.publishPaper(paper2);
        storage.addPaper(paper2);

        // Third paper pushes h-index to 3 which is the minimum to become a supervisor
        ResearchPaper paper3 = new ResearchPaper(
            "Neural Networks Overview", "IEEE Transactions", 18, new Date());
        paper3.addAuthor("Bob Smith");
        for (int i = 0; i < 10; i++) paper3.addCitation();
        resProf.publishPaper(paper3);
        storage.addPaper(paper3);

        System.out.println("h-index: " + resProf.calculateHIndex());

        // Supervisor for graduate student
        // GraduateStudent.setSupervisor() calls researcher.calculateHIndex()
        // LowHIndexException is thrown if h-index < 3
        System.out.println("\n[9] Assigning supervisor");
        try {
            graduate.setSupervisor(resProf);
            System.out.println("Supervisor assigned: " + graduate);
        } catch (LowHIndexException e) {
            System.err.println(e.getMessage());
        }

        ResearchPaper diploma = new ResearchPaper(
            "Thesis: Advanced ML", "University Repository", 120, new Date());
        diploma.addAuthor("Emma Davis");
        graduate.publishDiplomaPaper(diploma);
        System.out.println("Diploma papers: " + graduate.getDiplomaPapers().size());

        // Sorted paper printing
        // printPapers() uses ResearcherDecorator logic with the passed Comparator
        System.out.println("\n[10] Papers sorted by citations");
        resProf.printPapers(new CitationComparator());

        // Citation formats
        // getCitation() routes to private methods based on CitationFormat enum
        System.out.println("\n[11] Citation formats");
        System.out.println(paper1.getCitation(CitationFormat.PLAIN_TEXT));
        System.out.println(paper2.getCitation(CitationFormat.BIBTEX));

        // Journal and Observer pattern
        // journal.subscribe() stores the User as Observer
        // publishPaper() calls notifyObservers() which calls User.update() on each subscriber
        System.out.println("\n[12] Journal (Observer Pattern)");

        Journal journal = new Journal("Journal of Computer Science");
        storage.addJournal(journal);
        journal.subscribe(student);
        journal.subscribe(graduate);
        System.out.println("Journal: " + journal);

        ResearchPaper journalPaper = new ResearchPaper(
            "Cloud Computing Trends", "Journal of Computer Science", 45, new Date());
        journalPaper.addAuthor("Research Team");
        journal.publishPaper(journalPaper);

        // Research project
        // resProf.leadProject() calls ResearchProject.addParticipant(this)
        System.out.println("\n[13] Research project");
        ResearchProject project = new ResearchProject("AI in Education");
        resProf.leadProject(project);
        project.addPaper(paper1);
        storage.addProject(project);
        System.out.println(project);

        // Student organization
        // Student.joinOrganization() also calls org.addMember(this) internally
        System.out.println("\n[14] Student organization");
        StudentOrganization org = new StudentOrganization("CS Club");
        student.joinOrganization(org);
        graduate.joinOrganization(org);
        org.setHead(student);
        System.out.println(org);
        System.out.println("Head: " + org.getHead().getFirstName() + " " + org.getHead().getLastName());

        // News
        // Manager.manageNews() auto-pins news with topic Research via News.pin()
        System.out.println("\n[15] News");
        News researchNews = new News("New AI Lab", "University opens AI lab", "Research");
        manager.manageNews(researchNews);
        storage.addNews(researchNews);
        researchNews.addComment("Great initiative!");
        System.out.println(researchNews);

        // Tech support
        // Status flow shown VIEWED -> ACCEPTED -> DONE
        // rejectRequest() would go VIEWED -> REJECTED instead
        System.out.println("\n[16] Tech support");

        TechSupportSpecialist tech = new TechSupportSpecialist(
            "TEC001", "Frank", "Miller", "frank@uni.edu", "pass",
            Language.EN, "EMP004", 40000, "IT"
        );
        storage.addUser(tech);

        Request req = new Request("REQ001", "Fix projector in room 101", student);
        System.out.println("Created " + req);

        tech.acceptRequest(req);
        System.out.println("After accept " + req.getStatus());

        tech.markAsDone(req);
        System.out.println("After done " + req.getStatus());

        // Messaging
        // Employee.sendMessage() creates a Message object and prints it immediately
        System.out.println("\n[17] Messaging");
        professor.sendMessage(manager, "Can we schedule extra labs?");

        // Reports and transcript
        // createStatisticalReport() is a simple string builder; can be extended in Part C
        // getTranscript() iterates student.marks and formats each Mark via Mark.toString()
        System.out.println("\n[18] Reports");
        System.out.println(manager.createStatisticalReport());
        System.out.println(student.getTranscript());

        // Top cited researcher
        // DataStorage.getTopCitedResearcher() wraps users on the fly and compares h-index values
        // Casts to TeacherResearcher to call getTeacher() for display
        System.out.println("\n[19] Top cited researcher");
        Researcher top = storage.getTopCitedResearcher();
        if (top instanceof TeacherResearcher) {
            Teacher t = ((TeacherResearcher) top).getTeacher();
            System.out.println("Top " + t.getFirstName() + " " + t.getLastName()
                + " (h-index=" + top.calculateHIndex() + ")");
        }

        System.out.println("\n" + "=".repeat(65));
        System.out.println("Final state " + storage);
        System.out.println("Demo completed!");
    }
}
