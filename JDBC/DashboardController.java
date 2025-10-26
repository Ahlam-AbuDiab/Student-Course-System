/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import JDBC.LoginController.saveUser;

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
    
    Connection connection;
    Statement statement;

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection =
                    DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/SCRSproject?serverTimezone=UTC","root", "12345");
             this.statement = connection.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Connection to DB Failed");
            alert.showAndWait();
            return;
        }
        try {
            updateCounts();
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void updateCounts() throws SQLException{
        int students = getTableCount("Students");
        int courses = getTableCount("Courses");
        int enrolls = getTableCount("Enrollments");
        stdCount.setText("🧑‍🎓 Total students:\n    "+students);
        courseCount.setText("📘 Total Courses:\n    "+courses);
        enrollCount.setText("📝 Total Enrollment:\n   "+enrolls);
    }
    private int getTableCount(String table) throws SQLException{
        String sql = "SELECT COUNT(*) AS c FROM `" + table + "`";
        try (
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("c") : 0;
        }
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
