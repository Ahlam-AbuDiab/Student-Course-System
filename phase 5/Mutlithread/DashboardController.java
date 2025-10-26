/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Mutlithread;
import Mutlithread.LoginController.SaveUser;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import AdvancedFeature.Repositories.CourseRepository;
import AdvancedFeature.Repositories.EnrollmentRepository;
import AdvancedFeature.Repositories.StudentRepository;

/**
 * FXML Controller class
 *
 * @author ahlamabudiab
 */
public class DashboardController implements Initializable {

    @FXML
    private Button studentsButton;
    @FXML
    private Button courcesButton;
    @FXML
    private Button enrollmentButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label mindLabel;
    @FXML
    private Label stdCount;
    @FXML
    private Label courseCount;
    @FXML
    private Label enrollCount;
    @FXML
    private Button logoutButton;
    @FXML
    private Button scheduleButton;
    
    private final EnrollmentRepository enrollRepo = new EnrollmentRepository();
    private final StudentRepository stdRepo = new StudentRepository();
    private final CourseRepository courseRepo = new CourseRepository();

    /**
     * Initializes the controller class.
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        User u = SaveUser.getCurrentUser();
        if (u != null) {
        String firstName = u.getFirstName(); 
        welcomeLabel.setText("Welcome, " + firstName + " 👋 !");
        }
        updateCounts();
    }
    private void updateCounts(){
        Long students = stdRepo.countStudents();
        Long courses = courseRepo.countCourses();
        Long enrollments = enrollRepo.countEnrollment();
        stdCount.setText("🧑‍🎓 Total students:\n    "+students);
        courseCount.setText("📘 Total Courses:\n    "+courses);
        enrollCount.setText("📝 Total Enrollment:\n   "+enrollments);
    }
    @FXML
    private void studentsButtonHandle(ActionEvent event) throws IOException {
        Parent stdRoot = FXMLLoader.load(getClass().getResource("StudentsRegisteration.fxml"));
        Scene stdScene = new Scene(stdRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(stdScene);
        stage.show();
    }
    @FXML
    private void coursesButtonHandle(ActionEvent event) throws IOException {
        Parent courseRoot = FXMLLoader.load(getClass().getResource("Courses.fxml"));
        Scene courseScene = new Scene(courseRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(courseScene);
        stage.show();
    }
    @FXML
    private void enrollmentButtonHandle(ActionEvent event) throws IOException {
       Parent enrollRoot = FXMLLoader.load(getClass().getResource("Enrollment.fxml"));
        Scene enrollScene = new Scene(enrollRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(enrollScene);
        stage.show();
    }
    @FXML
    private void logoutButtonHandle(ActionEvent event) throws IOException {
        Parent courseRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene courseScene = new Scene(courseRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(courseScene);
        stage.show();
    }

    @FXML
    private void scheduleButtonHandle(ActionEvent event) throws IOException {
        Parent courseRoot = FXMLLoader.load(getClass().getResource("schedule.fxml"));
        Scene courseScene = new Scene(courseRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(courseScene);
        stage.show();
    }
}
