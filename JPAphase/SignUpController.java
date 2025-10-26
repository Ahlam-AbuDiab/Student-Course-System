/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JPAphase;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
    
    private EntityManagerFactory emf;  

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       signUpImage.setImage(new Image(getClass().getResource("signUpImage.jpg").toExternalForm()));
       this.emf = Persistence.createEntityManagerFactory("Student_Course_Registration_SystemPU");
         
    }
    @FXML
    private void signUpButtonHandle(ActionEvent event) throws  NoSuchAlgorithmException, IOException{  
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
        } 
        
        EntityManager em = emf.createEntityManager();
        Long n = (Long) em.createNamedQuery("Users.countByEmail")
                .setParameter("email", userEmail)
                .getSingleResult();
        if(n>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error in Email");
            alert.setHeaderText(null);
            alert.setContentText("Email Already exists");
            alert.showAndWait();
            event.consume();
            return;
        }
        
        em.getTransaction().begin();
        Users user = new Users();
        user.setFirstName(fNameTextField.getText());
        user.setLastName(lNameTextField.getText());
        user.setEmail(userEmail);
        user.setPasswordHash(md5(passwordField.getText()));
        em.persist(user);
        em.getTransaction().commit();
        em.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Creating Done");
            alert.setHeaderText(null);
            alert.setContentText("Creating account done,You can login now <^ ^>");
            alert.showAndWait();
        clearForm();               
        Parent loginRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
       
    }
    public static String md5(String input){
    try {
        var md = java.security.MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for(byte b: digest) sb.append(String.format("%02x", b));
        return sb.toString();
    } catch(Exception e){
        throw new RuntimeException(e);
    }
}
    private void clearForm(){
        fNameTextField.clear();
        lNameTextField.clear();
        emailTextField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        
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
   
    

