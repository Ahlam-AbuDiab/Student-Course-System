/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
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
    


    /**
     * Initializes the controller class.
     */
    private Connection connection;  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginImage.setImage(new Image(getClass().getResource("loginImage.jpg").toExternalForm()));
        try {//connect to the database server
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection =
                    DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/SCRSproject?serverTimezone=UTC","root", "12345");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    @FXML
    private void loginButtonHandle(ActionEvent event) throws NoSuchAlgorithmException, IOException, SQLException {
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
        }//to hash of the input password 
        String hashedPassword = md5(passwordTextField.getText());
        String sql = "SELECT id, first_name, last_name, password_hash " +
                     "FROM `SCRSproject`.`Users` WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ps.setString(1, emailTextField.getText().trim());
            try (ResultSet rs = ps.executeQuery()) {
                //to ensure that the inputs matches the data in the uses table in DB server
                if(!rs.next()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error ");
                    alert.setHeaderText(null);
                    alert.setContentText("Email is not registered.");
                    alert.showAndWait();
                    event.consume();
                    return; 
            }//to ensure that the hash of the input password mathces the stored password
            String dbHash = rs.getString("password_hash");
            if (!hashedPassword.equalsIgnoreCase(dbHash)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error password field");
                alert.setHeaderText(null);
                alert.setContentText("Wrong Password!!");
                alert.showAndWait();
                event.consume();
                return;
            }
            //save the data of the user who login in to the system 
            int userId = rs.getInt("id");
            String firstName = rs.getString("first_name");
            String lastName  = rs.getString("last_name");
            saveUser.setCurrentUser(new Users(String.valueOf(userId), firstName, lastName, userEmail, dbHash));
        Parent dashRoot = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene dashScene = new Scene(dashRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashScene); 
        stage.show();
            
            }
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
