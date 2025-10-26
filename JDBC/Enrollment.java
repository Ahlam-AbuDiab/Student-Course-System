/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ahlamabudiab
 */
package JDBC;

import java.time.LocalDate;

public class Enrollment {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;   
    private String studentName;
    private String courseTitle;

    public Enrollment(int id, int studentId, int courseId,LocalDate enrollmentDate,String studentName, String courseTitle) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.studentName = studentName;
        this.courseTitle = courseTitle;
    }

    public int getId() { 
        return id;
    }
    public int getStudentId() { 
        return studentId; 
    }
    public int getCourseId() { 
        return courseId; 
    }
    public LocalDate getEnrollmentDate() {
        return enrollmentDate; 
    }
    public String getStudentName() { 
        return studentName; 
    }
    public String getCourseTitle() {
        return courseTitle; 
    }

    public void setId(int id) { 
        this.id = id; 
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId; 
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId; 
    }
    public void setEnrollmentDate(LocalDate enrollmentDate) { 
        this.enrollmentDate = enrollmentDate; 
    }
    public void setStudentName(String studentName) { 
        this.studentName = studentName; 
    }
    public void setCourseTitle(String courseTitle) { 
        this.courseTitle = courseTitle; 
    }
}

