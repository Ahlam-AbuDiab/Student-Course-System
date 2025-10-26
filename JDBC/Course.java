/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JDBC;

/**
 *
 * @author ahlamabudiab
 */
public class Course {
    private int id;
    private String title;
    private int credits;
    private String department;

    public Course(int id, String title, int credits, String department) {
        this.id = id;
        this.title = title;
        this.credits = credits;
        this.department = department;
    }

    public Course() {
    }
    /**
     * @return the courseId
     */
    public int getId() {
        return id;
    }

    /**
     * @param courseId the courseId to set
     */
    public void detId(int courseId) {
        this.id = courseId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * @param credits the credits to set
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{" + "courseId=" + id + ", title=" + title + ", credits=" + credits + ", department=" + department + '}';
    }
}
  
