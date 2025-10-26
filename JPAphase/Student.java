/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPAphase;

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
@Table(name="Students")
@NamedQueries({
    @NamedQuery(name="Student.FindAll",
            query="SELECT s FROM Student s"),
    @NamedQuery(name="Student.Count",
            query="SELECT COUNT(s) FROM Student s"),
    @NamedQuery(name="Student.findSameStudent",
            query="SELECT COUNT(s)FROM Student s WHERE s.name =:name AND s.major=:major"),
    @NamedQuery(name="Student.FindSearch",
           query="SELECT s FROM Student s WHERE LOWER(s.name) LIKE :search OR LOWER(s.major) LIKE :search ")
})
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String major;
    
    @OneToMany(mappedBy="student", cascade=CascadeType.ALL)
    private List<Enrollment>enrollments;

    public Student() {
    }
   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
   @Override
    public String toString() {
       return name + " (" + major + ")";
}

}
