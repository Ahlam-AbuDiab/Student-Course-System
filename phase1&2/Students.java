/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student.course.registration.system;

/**
 *
 * @author ahlamabudiab
 */
public class Students {
    
    private String id;
    private String name;
    private String major;

    public Students(String id, String Name, String major) {
        this.id = id;
        this.name = Name;
        this.major = major;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return "Students{" + "id=" + id + ", Name=" + name + ", major=" + major + '}';
    }
     public static Students fromLine(String line) {
        String[] p = line.split(",");
        if (p.length < 3)
            return null;
        return new Students( p[0], p[1], p[2]);
}  
    public String toLine() {
          return String.join(",", id, name ,major); 
    }
    
}
