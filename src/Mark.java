// Оценка студента по курсу.
// Состоит из трёх компонентов: 1-я аттестация, 2-я аттестация, финальный экзамен.
// Умеет вычислять итоговый балл и букварную оценку (A–F).
import java.util.Objects;
import java.util.UUID;

public class Mark {

    // Уникальный идентификатор оценки
    private String markId;
    // Балл за первую аттестацию
    private double firstAttestation;
    // Балл за вторую аттестацию
    private double secondAttestation;
    // Балл за финальный экзамен
    private double finalExam;
    // Студент, которому выставлена оценка
    private Student student;
    // Курс, по которому выставлена оценка
    private Course course;

    // Конструктор — автоматически генерирует id оценки
    public Mark(double firstAttestation, double secondAttestation, double finalExam,
                Student student, Course course) {
        this.markId = UUID.randomUUID().toString();
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
    }

    // Вычислить среднее арифметическое трёх компонентов
    public double getTotalScore() {
        return (firstAttestation + secondAttestation + finalExam) / 3.0;
    }

    // Перевести итоговый балл в буквенную оценку по шкале A–F
    public String getLetterGrade() {
        double score = getTotalScore();
        // Шкала оценок университета
        if (score >= 94.5) return "A";
        if (score >= 89.5) return "A-";
        if (score >= 84.5) return "B+";
        if (score >= 79.5) return "B";
        if (score >= 74.5) return "B-";
        if (score >= 69.5) return "C+";
        if (score >= 64.5) return "C";
        if (score >= 59.5) return "C-";
        if (score >= 54.5) return "D+";
        if (score >= 49.5) return "D";
        return "F";
    }

    // Строковое представление оценки
    @Override
    public String toString() {
        return "Mark{student=" + (student != null ? student.getFirstName() : "null")
                + ", course=" + (course != null ? course.getName() : "null")
                + ", total=" + String.format("%.2f", getTotalScore())
                + ", grade=" + getLetterGrade() + "}";
    }

    // Две оценки равны, если совпадают их markId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;
        Mark m = (Mark) o;
        return Objects.equals(markId, m.markId);
    }

    // Хэш-код по markId
    @Override
    public int hashCode() {
        return Objects.hash(markId);
    }
}
