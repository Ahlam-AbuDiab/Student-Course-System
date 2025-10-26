/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student.course.registration.system;

import java.time.Instant;
import java.util.Date;

/**
 *
 * @author ahlamabudiab
 */
public class Enrollment {
    
    private String id;
    private String studentId ;
    private String courceId;
    private String enrollmentDate;
    private String studentName;
    private String courseTitle;

    public Enrollment(String id, String studentId, String courceId, String enrollmentDate) {
        this.id = id;
        this.studentId = studentId;
        this.courceId = courceId;
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the studentId
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * @param studentId the studentId to set
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * @return the courceId
     */
    public String getCourceId() {
        return courceId;
    }

    /**
     * @param courceId the courceId to set
     */
    public void setCourceId(String courceId) {
        this.courceId = courceId;
    }

    /**
     * @return the enrollmentDate
     */
    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * @param enrollmentDate the enrollmentDate to set
     */
    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "Enrollment{" + "id=" + id + ", studentId=" + studentId + ", courceId=" + courceId + ", enrollmentDate=" + enrollmentDate + '}';
    }
    public static Enrollment fromLine(String line) {
        String[] p = line.split(",");
        if (p.length < 4)
            return null;
        return new Enrollment(p[0].trim(),p[1].trim(),p[2].trim(), p[3].trim()); 
    } 
    public String toLine() {
          return String.join(",", id, studentId ,courceId, enrollmentDate); 
    }

    /**
     * @return the studentName
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * @param studentName the studentName to set
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * @return the courseName
     */
    public String getCourseTitle() {
        return courseTitle;
    }

    /**
     * @param courseName the courseName to set
     */
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    
}
