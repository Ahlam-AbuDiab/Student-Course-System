/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student.course.registration.system;

/**
 *
 * @author ahlamabudiab
 */
public class Course {
    private String courseId;
    private String title;
    private int credits;
    private String department;

    public Course(String courseId, String title, int credits, String department) {
        this.courseId = courseId;
        this.title = title;
        this.credits = credits;
        this.department = department;
    }
    

    /**
     * @return the courseId
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
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
        return "Course{" + "courseId=" + courseId + ", title=" + title + ", credits=" + credits + ", department=" + department + '}';
    }
    public static Course fromLine(String line) {
        String[] p = line.split(",");
        if (p.length < 4)
            return null;
        return new Course(p[0], p[1], Integer.parseInt(p[2]), p[3]);
} 

    
    public String toLine() {
        return courseId + "," + title + "," + credits + "," + department;    }  
    }
