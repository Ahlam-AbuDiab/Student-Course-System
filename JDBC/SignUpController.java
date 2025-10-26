/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.ResourceBundle;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import static JDBC.LoginController.md5;

/**
 * FXML Controller class
 *
 * @author ahlamabudiab
 */
public class SignUpController implements Initializable {

    @FXML
    private Label creatingLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private TextField fNameTextField;
    @FXML
    private Label lastNameLabel;
    @FXML
    private TextField lNameTextField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signUpButton;
    @FXML
    private Label labelError;

    /**
     * Initializes the controller class.
     */

    @FXML
    private ImageView signUpImage;
    @FXML
    private Button backButton;
    
    private Connection connection;  

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       signUpImage.setImage(new Image(getClass().getResource("signUpImage.jpg").toExternalForm()));
       try {//connection to the server
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection =
                    DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/SCRSproject?serverTimezone=UTC","root", "12345");
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Connection to DB Failed");
            alert.showAndWait();
            return;
        }  
    }
    @FXML
    private void signUpButtonHandle(ActionEvent event) throws  NoSuchAlgorithmException, SQLException, IOException{  
        //to signup to the system fill all attributes and sure no field is empty
        if (fNameTextField.getText().isEmpty() || lNameTextField.getText().isEmpty() 
                || emailTextField.getText().isEmpty() || passwordField.getText().isEmpty() 
                || confirmPasswordField.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("You should fill all attributes");
                    alert.showAndWait();
                    event.consume();
                    return;
        }//to insure that the 2 password is matched 
        if (passwordField.getText() == null || passwordField.getText().isEmpty() || !passwordField.getText().equals(confirmPasswordField.getText())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("The two passwords do not match.");
            a.showAndWait();
            event.consume();
            return;
        }//toensure that the email is right domain
        String userEmail = emailTextField.getText().trim();
        if(!userEmail.isEmpty() && !userEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error in Email field");
            alert.setHeaderText(null);
            alert.setContentText("Email should contain @ and right domain");
            alert.showAndWait();
            event.consume();
            return;
        } //to ensure that the email doesnt exsits to the system
        String checkSql = "SELECT 1 FROM `SCRSproject`.`Users` WHERE email = ? LIMIT 1";
            try (
                PreparedStatement chk = connection.prepareStatement(checkSql)) {
                    chk.setString(1, userEmail);
                    try (ResultSet rs = chk.executeQuery()) {
                        if (rs.next()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error in Email");
                            alert.setHeaderText(null);
                            alert.setContentText("Email Already exists");
                            alert.showAndWait();
                            event.consume();
                            return;
                        }
                    }
                }
        String first_name = fNameTextField.getText();
        String last_name = lNameTextField.getText();
        String email = userEmail;
        String password_hash = md5(passwordField.getText().trim());
        String sql = "INSERT INTO `SCRSproject`.`Users` (first_name, last_name, email, password_hash) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, email);
            ps.setString(4, password_hash);
            int rows = ps.executeUpdate();
        if (rows > 0) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successful");
            alert.setHeaderText(null);
            alert.setContentText("Creating account done, You can Login now<^^>");
            alert.showAndWait();
        Parent loginRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No rows inserted.<^!^>");
            alert.showAndWait();
        }
    }
}
    @FXML
    private void backButtonHandle(ActionEvent event) throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene); 
        stage.show();
    }
}
   
    

