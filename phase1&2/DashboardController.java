/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package student.course.registration.system;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import student.course.registration.system.LoginController.saveUser;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Users u = saveUser.getCurrentUser();
        if (u != null) {
        String firstName = u.getFirstName(); 
        welcomeLabel.setText("Welcome, " + firstName + " 👋 !");
    }
        try {
            updateCounts();
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateCounts() throws IOException{
        List<String> stdLines = new Store("student.txt").readFile();
        List<String> courseLines = new Store("course.txt").readFile();
        List<String> enrollLines = new Store("enrollment.txt").readFile();
        int stdTotlaCount = (int) stdLines.stream()
                            .map(Students::fromLine)
                            .filter(Objects::nonNull)
                            .count();
        int courseTotlaCount = (int) courseLines.stream()
                            .map(Course::fromLine)
                            .filter(Objects::nonNull)
                            .count();
        int enrollTotlaCount = (int) enrollLines.stream()
                            .map(Enrollment::fromLine)
                            .filter(Objects::nonNull)
                            .count();
        stdCount.setText("🧑‍🎓 Total students:\n    "+String.valueOf(stdTotlaCount));
        courseCount.setText("📘 Total Courses:\n    "+String.valueOf(courseTotlaCount));
        enrollCount.setText("📝 Total Enrollment:\n   "+String.valueOf(enrollTotlaCount));
  
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
}
