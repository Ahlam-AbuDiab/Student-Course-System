/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JDBC;
/**
 *
 * @author ahlamabudiab
 */
public class Students {
    
    private int id;
    private String name;
    private String major;

    public Students() {
    }
    

    public Students(int id, String Name, String major) {
        this.id = id;
        this.name = Name;
        this.major = major;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
