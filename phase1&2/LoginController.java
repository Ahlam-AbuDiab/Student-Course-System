/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package student.course.registration.system;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ahlamabudiab
 */
public class LoginController implements Initializable {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label systemNameLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Label creatingLabel;
    @FXML
    private Button signUpButton;
    @FXML
    private ImageView loginImage;
    
   private final Store users = new Store("users.txt");


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      loginImage.setImage(new Image(getClass().getResource("loginImage.jpg").toExternalForm()));
    }    

    @FXML
    private void loginButtonHandle(ActionEvent event) throws NoSuchAlgorithmException, IOException {
        
        if (emailTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
            return;
        }
        String hashedPassword = md5(passwordTextField.getText());
        List<String> lines = users.readFile();
        Optional<Users> foundUser = lines
                                        .stream()
                                        .map(Users::fromLine)
                                        .filter(Objects::nonNull)
                                        .filter(e-> e.getEmail().equalsIgnoreCase(emailTextField.getText()))
                                        .findFirst();
        if(foundUser.isEmpty()){
            Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You login with an error emailAccount!!");
                alert.showAndWait();
                event.consume();
            return;
        }  
        if(!foundUser.get().getPasswordHash().equals(hashedPassword)){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You login with an error Password!!");
                alert.showAndWait();
                event.consume();
            return;
        }
        if (foundUser.isPresent() && foundUser.get().getPasswordHash().equals(hashedPassword)) {
        saveUser.setCurrentUser(foundUser.get());
        Parent dashRoot = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
       
        Scene dashScene = new Scene(dashRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashScene); 
        stage.show();
        
        }
    }
    
    public static String md5(String pass) throws NoSuchAlgorithmException{
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(pass.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) sb.append(String.format("%02x", b));
      return sb.toString();  
    }
    
    public class saveUser {
    private static Users currentUser;
    public static void setCurrentUser(Users user) {
        currentUser = user;
    }
    public static Users getCurrentUser() {
        return currentUser;
    }
    }

    @FXML
    private void signUpButtonHandle(ActionEvent event) throws IOException {
        
        Parent signUpRoot = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        Scene signUpScene = new Scene(signUpRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(signUpScene); 
        stage.show();
    }
    
}
