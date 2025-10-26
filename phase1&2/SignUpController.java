/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package student.course.registration.system;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;
import javafx.css.PseudoClass;
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
import static student.course.registration.system.LoginController.md5;

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
    private final Store users = new Store("users.txt");

    @FXML
    private ImageView signUpImage;
    @FXML
    private Button backButton;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       signUpImage.setImage(new Image(getClass().getResource("signUpImage.jpg").toExternalForm()));
    }
        

    @FXML
    private void signUpButtonHandle(ActionEvent event) throws IOException, NoSuchAlgorithmException{

        
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
        }
        
        if (passwordField.getText() == null || passwordField.getText().isEmpty() || !passwordField.getText().equals(confirmPasswordField.getText())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("The two passwords do not match.");
            a.showAndWait();
            event.consume();
            return;
        }
    String email = emailTextField.getText().trim();
        if(!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error in Email field");
            alert.setHeaderText(null);
            alert.setContentText("Email should contain @ and right domain");
            alert.showAndWait();
            event.consume();
            return;
      } 
        List<String> lines = users.readFile();
        boolean duplicateEmail = lines
                                    .stream()
                                    .map(Users::fromLine)
                                    .filter(Objects::nonNull)
                                    .anyMatch(e-> e.getEmail().equalsIgnoreCase(email));
        if(duplicateEmail){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error in Email field");
            alert.setHeaderText(null);
            alert.setContentText("Email already exists!!");
            alert.showAndWait();
            event.consume();
            return;
        }  
        String id = UUID.randomUUID().toString();
        String hashedPass = md5(passwordField.getText());
        Users user = new Users(id, fNameTextField.getText(), lNameTextField.getText(), emailTextField.getText(), hashedPass);
        users.printInFile(user.toLine());
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
   
    

