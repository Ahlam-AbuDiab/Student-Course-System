/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JPAphase;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author ahlamabudiab
 */
public class StudentsRegisterationController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField stdNameTextField;
    @FXML
    private Label majorLabel;
    @FXML
    private TextField stdMajTextField;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Student> stdtable;
    @FXML
    private TableColumn<Student, Integer> idColumn;
    @FXML
    private TableColumn<Student, String> nameCol;
    @FXML
    private TableColumn<Student, String> majorCol;
    @FXML
    private Button editButton;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button showButton;
    @FXML
    private Button deleteButton;
    
    private EntityManagerFactory emf;  
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            this.emf = Persistence.createEntityManagerFactory("Student_Course_Registration_SystemPU");
            //matche the col of the table view to the col in the table of the database,
            idColumn.setCellValueFactory(new PropertyValueFactory("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory("name"));
            majorCol.setCellValueFactory(new PropertyValueFactory("major"));
            stdtable.setItems(students);
            stdtable.getSelectionModel().selectedItemProperty().addListener(
                    event -> showSelectedStudents());
            stdtable.getSelectionModel().selectedItemProperty().addListener((o, old, sel)->{
                if(sel != null){
                    stdNameTextField.setText(sel.getName());
                    stdMajTextField.setText(sel.getMajor());
                }
            });
            
            sortComboBox.setItems(FXCollections.observableArrayList("Name","Major"));
            sortComboBox.getSelectionModel().selectedItemProperty().addListener((o, old, val) -> {
                Comparator<Student> comp =null;
                if ("Name".equals(val)) {
                    comp = Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
                }else if ("Major".equals(val)) {
                    comp = Comparator.comparing(Student::getMajor, String.CASE_INSENSITIVE_ORDER);
                    
                }
                FXCollections.sort(students, comp);
            });
            searchField.setOnAction(this::searchButtonhandle);
            deleteButton.disableProperty().bind(
                    stdtable.getSelectionModel().selectedItemProperty().isNull());
            showButtonHandle(null);
        } catch (SQLException ex) {
            Logger.getLogger(StudentsRegisterationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void showSelectedStudents(){
        Student std  = stdtable.getSelectionModel().getSelectedItem();
        if(std != null){
            stdNameTextField.setText(std.getName());
            stdMajTextField.setText(std.getMajor());
        }
    }
    public void clearForm(){
       stdNameTextField.clear();
       stdMajTextField.clear();
       stdtable.getSelectionModel().clearSelection();
    }
    @FXML
    private void addStudent(ActionEvent event) throws SQLException {
        String name= stdNameTextField.getText();
        String major= stdMajTextField.getText();
        //as in the course sure that the student doesnt exists in the table before time and no field is empty
        if(name.isEmpty() || major.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You should fill all attributes");
            alert.showAndWait();
            event.consume();
            return;
        }
        EntityManager em = emf.createEntityManager();
        Long n = (Long) em.createNamedQuery("Student.findSameStudent")
             .setParameter("name", name)
             .setParameter("major", major)
             .getSingleResult();
        if(n>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Student Already exists");
            alert.showAndWait();
            event.consume();
            return;
        }
        em.getTransaction().begin();
        Student student = new Student();
        student.setName(name);
        student.setMajor(major);
        em.persist(student);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student's Insert done");
        alert.showAndWait();
        event.consume();
        students.setAll(em.createNamedQuery("Student.FindAll",Student.class).getResultList());
        clearForm();
        em.close();
        }
    
    @FXML
    private void editStudent(ActionEvent event) throws IOException, SQLException {
        Student selected = stdtable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected student");
            alert.showAndWait();
            return;
        }
        String name = stdNameTextField.getText();
        String major = stdMajTextField.getText();
        if(name.isEmpty() || major.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("You should fill all attributes");
            alert.showAndWait();
            return;
        }
        EntityManager em = emf.createEntityManager();
        Long n = (Long) em.createNamedQuery("Student.findSameStudent")
             .setParameter("name", name)
             .setParameter("major", major)
             .getSingleResult();
        if(n>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Student Already exists");
            alert.showAndWait();
            event.consume();
            return;
        }
        clearForm();
        em.getTransaction().begin();
        Student student = em.find(Student.class, selected.getId());
        student.setName(name);
        student.setMajor(major);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student's Update done");
        alert.showAndWait();
        event.consume();
        clearForm();
        students.setAll(em.createNamedQuery("Student.FindAll",Student.class).getResultList());
        em.close();
        
    }
    @FXML
    private void backButtonHandle(ActionEvent event) throws IOException {
        Parent dashRoot = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Scene dashScene = new Scene(dashRoot);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(dashScene); 
        stage.show();
    }
    @FXML
    private void searchButtonhandle(ActionEvent event) {
        String search = searchField.getText();
        if(search.isEmpty()){
           Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("No search in the field");
                    alert.showAndWait();
                    return; 
        }
        EntityManager em = emf.createEntityManager();
        List<Student>stdList = em.createNamedQuery("Student.FindSearch")
                .setParameter("search", "%" + search.toLowerCase() + "%")
                .getResultList();
        students.setAll(stdList);
        em.close();

    }
    @FXML
    private void showButtonHandle(ActionEvent event) throws SQLException {
        EntityManager em = emf.createEntityManager();
        students.setAll(em.createNamedQuery("Student.FindAll",Student.class).getResultList());
        em.close();
    }

    @FXML
    private void deleteButtonHandle(ActionEvent event) throws SQLException {
        //delete the student which selected from the table view and delete all records from enrollments table which related to this student
        Student selected = stdtable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected student");
            alert.showAndWait();
            return;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Student student = em.find(Student.class, selected.getId());
        em.remove(student);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student's delete done");
        alert.showAndWait();
        students.setAll(em.createNamedQuery("Student.FindAll").getResultList());
        clearForm();
        em.close();        
    }
}

        
        