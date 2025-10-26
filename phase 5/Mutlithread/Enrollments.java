/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ahlamabudiab
 */
package Mutlithread;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Enrollments",uniqueConstraints = @UniqueConstraint(columnNames = {"student_id","course_id"}))

public class Enrollments implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;     
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="student_id")
    private Students student;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="course_id")
    private Courses course;
    

    public Enrollments(){
    }

    public Enrollments(Students student, Courses course, LocalDate enrollmentDate) {
        this.student = student;
        this.course = course;
        this.enrollmentDate =enrollmentDate;
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

    public Students getStudent() {
        return student;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }
    
}
    