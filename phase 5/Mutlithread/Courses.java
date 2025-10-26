/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mutlithread;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ahlamabudiab
 */
@Entity
@Table(name="Courses")

public class Courses implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer credits;
    private String department;
    @Column(nullable = false)
    private int capacity = 30;
    
    private LocalDate addDropDeadline;
    
    @OneToMany(mappedBy ="course", cascade=CascadeType.ALL)
    private List<Enrollments> enrollments;

    public Courses() {
    }

    public Courses( String title, Integer credits, String department, int capacity) {
        this.title = title;
        this.credits = credits;
        this.department = department;
        this.capacity = capacity;
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getAddDropDeadline() {
        return addDropDeadline;
    }

    public void setAddDropDeadline(LocalDate addDropDeadline) {
        this.addDropDeadline = addDropDeadline;
    }

    

  @Override
    public String toString() {
       return title + " (" + department + ") - " + credits + " cr";
}

}
  
