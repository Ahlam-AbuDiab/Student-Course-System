/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPAphase;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ahlamabudiab
 */
@Entity
@Table(name="Courses")
@NamedQueries({
    @NamedQuery(name="Course.FindAll",
        query="SELECT c FROM Course c"),
    @NamedQuery(name="Course.Count",
        query="SELECT COUNT(c) FROM Course c"),
    @NamedQuery(name="Course.CountSameCourse",
        query="SELECT COUNT(c) FROM Course c " +
              "WHERE LOWER(c.title)=LOWER(:title) AND c.department=:department"),
    @NamedQuery(name="Course.FindSearch",
        query="SELECT c FROM Course c " +
              "WHERE LOWER(c.title) LIKE :search OR LOWER(c.department) LIKE :search OR c.credits = :cr")
})
public class Course implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer credits;
    private String department;
    
    @OneToMany(mappedBy ="course", cascade=CascadeType.ALL)
    private List<Enrollment> enrollments;

    public Course() {
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

  @Override
    public String toString() {
       return title + " (" + department + ") - " + credits + " cr";
}

}
  
