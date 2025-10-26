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
public class CoursesController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private Label creditsLabel;
    @FXML
    private TextField creditsTextField;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private TableView<Course> courceTable;
    @FXML
    private TableColumn<Course, Integer> idColumn;
    @FXML
    private TableColumn<Course, String> titleColm;
    @FXML
    private TableColumn<Course, Integer> creditsColm;
    @FXML
    private TableColumn<Course, String> departmentColm;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private Button showButton;
    @FXML
    private Button deleteButton;
    
    private EntityManagerFactory emf;  
    private final ObservableList<Course> courses = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.emf = Persistence.createEntityManagerFactory("Student_Course_Registration_SystemPU");
       
        //تعريف اععمدة في الجدول وربطها مع اسماء قاعدة البيانات 
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        titleColm.setCellValueFactory(new PropertyValueFactory("title"));
        creditsColm.setCellValueFactory(new PropertyValueFactory("credits"));
        departmentColm.setCellValueFactory(new PropertyValueFactory("department"));
        
        courceTable.setItems(courses);
        //declare the combobox of depatrment and give it the element of it.
        departmentComboBox.setItems(FXCollections.observableArrayList("CS", "SD","Computing","Counting","IT"));
        //to select row from the table
        courceTable.getSelectionModel().selectedItemProperty().addListener(
                event -> showSelectedCourses());
       
        //combobox to choose the way to sort the table by it
        sortComboBox.setItems(FXCollections.observableArrayList("Title","Credits","Department"));
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((o, old, val) -> {
        String sortDataBy = sortComboBox.getValue();
        Comparator<Course> courseComp = switch(sortDataBy){
            case "Title" -> Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "Credits"-> Comparator.comparingInt(Course::getCredits);
            case "Department"-> Comparator.comparing(Course::getDepartment, String.CASE_INSENSITIVE_ORDER);
            default -> null;
        };
         FXCollections.sort(courses, courseComp);
        //for searching and delete the row which is selected       
                });
        searchField.setOnAction(this::searchButtonHandle);
        deleteButton.disableProperty().bind(
                courceTable.getSelectionModel().selectedItemProperty().isNull());
        showButtonHandle(null);
                }
 
    //to show the data of the selected row
    private void showSelectedCourses(){
        Course course  = courceTable.getSelectionModel().getSelectedItem();
        if(course != null){
        titleTextField.setText(course.getTitle());
        creditsTextField.setText(String.valueOf(course.getCredits()));
        departmentComboBox.getSelectionModel().select(course.getDepartment());
        }
       
    }
    //clear all controlls in the page
    private void clearForm() {
       titleTextField.clear();
       creditsTextField.clear();
       departmentComboBox.getSelectionModel().clearSelection();
       courceTable.getSelectionModel().clearSelection();
    } 
    @FXML
    private void addCourseHandle(ActionEvent event) throws IOException, SQLException {
        String title =titleTextField.getText();
        String department = departmentComboBox.getValue();
        String creditsStr = creditsTextField.getText();
        //to exmine if any field required to add course is empty 
        if(title.isEmpty() || creditsStr.isEmpty() 
                ||department == null){
              Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
                return;
        }
        //to make sure that the credits is apositive number
        if(!creditsStr.matches("\\d+")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The credits can't be negative or non-numeric values ");
                alert.showAndWait();
                event.consume();
                return;
        }
        int credits = Integer.parseInt(creditsStr);
        EntityManager em = emf.createEntityManager();
        Long n = (Long) em.createNamedQuery("Course.CountSameCourse")
                .setParameter("title", title)
                .setParameter("department", department)
                .getSingleResult();
        if(n>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Course Already exists");
            alert.showAndWait();
            event.consume();
            return;
        }
            em.getTransaction().begin();
            Course course = new Course();
            course.setTitle(title);
            course.setCredits(credits);
            course.setDepartment(department);
            em.persist(course);
            em.getTransaction().commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Courses's Insert done");
            alert.showAndWait();
            courses.setAll(em.createNamedQuery("Course.FindAll", Course.class).getResultList());
            clearForm();
            em.close();
       
    }
    @FXML
    private void editCourseHandle(ActionEvent event) throws IOException, SQLException {
        //edit the course which selected from the table
        Course selected = courceTable.getSelectionModel().getSelectedItem();
        String creditsStr = creditsTextField.getText();
        //if no selected 
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should select course to edit");
                alert.showAndWait();
                event.consume();
                return;
        }
       //to exmine if any field required to edit course is empty
        String title = titleTextField.getText();
        int credits = Integer.parseInt(creditsStr);
        String department = departmentComboBox.getValue();
        if(title.isEmpty() || creditsStr.isEmpty()
                || department.isEmpty()){
              Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
                return;
            }
        if(!creditsStr.matches("\\d+")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The credits can't be negative or non-numeric values ");
                alert.showAndWait();
                event.consume();
                return;
        }
        EntityManager em = emf.createEntityManager();
        Long n = (Long) em.createNamedQuery("Course.CountSameCourse")
                .setParameter("title", title)
                .setParameter("department", department)
                .getSingleResult();
        if(n>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Course Already exists");
            alert.showAndWait();
            event.consume();
            return;
        }
        clearForm();
        em.getTransaction().begin();
        Course course = em.find(Course.class, selected.getId());
        course.setTitle(title);
        course.setCredits(credits);
        course.setDepartment(department);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Course's Update done");
        alert.showAndWait();
        clearForm();
        courses.setAll(em.createNamedQuery("Course.FindAll").getResultList());
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
    private void searchButtonHandle(ActionEvent event) {
        String search = searchField.getText();
        if(search.isEmpty()){
           Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("No search in the field");
                    alert.showAndWait();
                    event.consume();
                    return; 
        }
        Integer searchInt = null;
         try {
        searchInt  = Integer.valueOf(search);
         } catch (NumberFormatException ignore) {
         }
        EntityManager em = emf.createEntityManager();
        try {
        List<Course> list = em.createNamedQuery("Course.FindSearch", Course.class)
            .setParameter("search", "%" + search.toLowerCase() + "%")
            .setParameter("cr", searchInt == null ? Integer.MIN_VALUE : searchInt) // دايمًا مرري :cr
            .getResultList();
        courses.setAll(list);
        }finally{
        em.close();
        }
    }
    @FXML
    private void showButtonHandle(ActionEvent event) {
        EntityManager em = emf.createEntityManager();
        courses.setAll(em.createNamedQuery("Course.FindAll").getResultList());
        em.close();
    }

    @FXML
    private void deleteButtonHandle(ActionEvent event) throws SQLException {
        //to delete the selected course which selected from the table
        Course selected = courceTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected course");
            alert.showAndWait();
            return;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Course course = em.find(Course.class, selected.getId());
        em.remove(course);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Course's delete done");
        alert.showAndWait();
        event.consume();
        courses.setAll(em.createNamedQuery("Course.FindAll").getResultList());
        clearForm();
        em.close();
        
    }
}