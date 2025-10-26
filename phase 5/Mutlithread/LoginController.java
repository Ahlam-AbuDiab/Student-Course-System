/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Mutlithread;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
    


    /**
     * Initializes the controller class.
     */
    private EntityManagerFactory emf;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginImage.setImage(new Image(getClass().getResource("loginImage.jpg").toExternalForm()));
        this.emf = Persistence.createEntityManagerFactory("AdvancedFeature");
    }    
    @FXML
    private void loginButtonHandle(ActionEvent event) throws NoSuchAlgorithmException, IOException {
        //to hash of the input password 
        String hash = md5(passwordTextField.getText());
        //to ensure all fiels isnt empty
        if (emailTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
            return;
        }//to ensure that the email is matches the right domain
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
        try{
        List<User> userList = em.createNamedQuery("User.findByEmail")
                .setParameter("email", userEmail)
                .setMaxResults(1)
                .getResultList();
        if(userList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setHeaderText(null);
            alert.setContentText("Email is not registered.");
            alert.showAndWait();
            event.consume();
            return;
        }
        User user =userList.get(0);
        if (!hash.equals(user.getPasswordHash())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Wrong Password!!");
            alert.showAndWait();
            event.consume();
            return;
        }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Login successful!");
            alert.showAndWait();
            LoginController.SaveUser.setCurrentUser(user);     
            Parent dashRoot = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Scene dashScene = new Scene(dashRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(dashScene); 
            stage.show();
        }finally{
            em.close();
            }
    }
    public static String md5(String pass) throws NoSuchAlgorithmException{
       //way to hash the password to protect it 
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(pass.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();  
    }
   //method to save the user who loginin to the system to retritve his first name to use it in dashboard page  
    public class SaveUser {
        private static User currentUser;
        public static void setCurrentUser(User user) {
            currentUser = user;
        }
        public static User getCurrentUser() {
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
