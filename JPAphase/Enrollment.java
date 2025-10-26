/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ahlamabudiab
 */
package JPAphase;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Enrollments",uniqueConstraints = @UniqueConstraint(columnNames = {"student_id","course_id"}))
@NamedQueries({
    @NamedQuery(name="Enrollment.FindAll",
            query="SELECT e FROM Enrollment e"),
    @NamedQuery(name="Enrollment.Count",
            query="SELECT COUNT(e) FROM Enrollment e"),
    @NamedQuery(name="Enrollment.countDup",
        query="SELECT COUNT(e) FROM Enrollment e " +
              "WHERE e.student.id = :sid AND e.course.id = :cid"),
    @NamedQuery(name="Enrollment.findByAnyId",
        query="SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c " +
              "WHERE e.id = :id OR s.id = :id OR c.id = :id"),
    @NamedQuery(name="Enrollment.findSearch",
        query="SELECT e FROM Enrollment e JOIN e.student s JOIN e.course c " +
              "WHERE LOWER(s.name) LIKE :search OR LOWER(c.title) LIKE :search " +
              " OR LOWER(c.department) LIKE :search ORDER BY e.id"),
    @NamedQuery(name="Enrollment.findByDate",
        query="SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c " +
              "WHERE e.enrollmentDate = :d ORDER BY e.id")
        
})
public class Enrollment implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;     
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    

    public Enrollment(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    
}
    