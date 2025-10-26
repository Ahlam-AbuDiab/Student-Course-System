/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JPAphase;

import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
public class EnrollmentController implements Initializable {

    @FXML
    private Label systemLabel;
    @FXML
    private Label studentLabel;
    @FXML
    private ComboBox<Student> stdComboBox;
    @FXML
    private Label courseLabel;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private Button enrollButton;
    @FXML
    private Button backButton;
    @FXML
    private Button showButton;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Enrollment> enrollTable;
    @FXML
    private TableColumn<Enrollment, Integer> noCol;
    @FXML
    private TableColumn<Enrollment, String> studentCol;
    @FXML
    private TableColumn<Enrollment, String> courseCol;
    @FXML
    private TableColumn<Enrollment, LocalDate> dateCol;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> dateComboBox; 
    @FXML
    private DatePicker searchDate;
    @FXML
    private TextField searchField; 
    @FXML
    private Button deleteButton;
    
    private EntityManagerFactory emf;  
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    private final ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        try {
            this.emf = Persistence.createEntityManagerFactory("Student_Course_Registration_SystemPU");
            //تعريف اععمدة في الجدول وربطها مع اسماء قاعدة البيانات
            noCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            studentCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getStudent().getName()));
            courseCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getCourse().getTitle()));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
            
            loadCourseComboBox();
            loadStudentComboBox();
            
            enrollTable.setItems(enrollments);
            deleteButton.disableProperty().bind(
                    enrollTable.getSelectionModel().selectedItemProperty().isNull());
            showButtonHandle(null);
        } catch (SQLException ex) {
            Logger.getLogger(EnrollmentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EnrollmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dateComboBox.setItems(FXCollections.observableArrayList("Enrollment Date"));
        dateComboBox.valueProperty().addListener((obs, oldV, newV) -> sortByDate());
    }
    private void clearForm(){
        searchField.clear();
        searchDate.setValue(null);
        dateComboBox.getSelectionModel().clearSelection();
    }//loading the student information with updates to the enrollment to match the the student id 
    private void loadStudentComboBox() throws SQLException{
        EntityManager em = emf.createEntityManager();
        students.setAll(em.createQuery("SELECT s FROM Students s", Student.class).getResultList());
        em.close();
        stdComboBox.setItems(students);
        stdComboBox.setCellFactory(cb -> new ListCell<>(){
             @Override
            protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + " (" + s.getMajor() + ")");
            } 
        });
    }//load course data from the courses table
    private void loadCourseComboBox() throws SQLException{
        EntityManager em = emf.createEntityManager();
        courses.setAll(em.createQuery("SELECT c FROM Course c ", Course.class).getResultList());
        em.close();
        courseComboBox.setItems(courses);
        courseComboBox.setCellFactory(cb-> new ListCell<>(){
            @Override
            protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getTitle()+ " (" + c.getDepartment()+ ")-"+c.getCredits()+"cr");
            } 
        });
        }
    private void sortByDate(){
        if (!"Enrollment Date".equals(dateComboBox.getValue())) return;
        List<Enrollment> tmp = new ArrayList<>(enrollments);
        tmp.sort(Comparator.comparing(Enrollment::getEnrollmentDate,
                Comparator.nullsFirst(LocalDate::compareTo)));
        enrollments.setAll(tmp);
    }
    @FXML
    private void enrollButtonHandle(ActionEvent event) throws SQLException {
        EntityManager em = emf.createEntityManager();
        //to enroll the student to the course fill the attributes and exmnie if any field is empty 
        Student studentData = stdComboBox.getValue();
        Course courseData = courseComboBox.getValue();
        LocalDate enrollDate = datePicker.getValue();
        if(studentData == null ||courseData == null || enrollDate == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You should select from all !!");
            alert.showAndWait();
            event.consume();
            return;
        }//to ensure the date is not in the future
        if(enrollDate.isAfter(LocalDate.now())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error in date cannot be in the future!!");
            alert.showAndWait();
            event.consume();
            return;
        }
        Long dup = em.createNamedQuery("Enrollment.countDup", Long.class)
                    .setParameter("sid", studentData.getId())
                    .setParameter("cid", courseData.getId())
                    .getSingleResult();
        if(dup>0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error student already exists in this course!");
            alert.showAndWait();
            return;
        }
        em.getTransaction().begin();
        Student mStudent = em.find(Student.class, studentData.getId());
        Course mCourse = em.find(Course.class, courseData.getId());
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(mStudent);
        enrollment.setCourse(mCourse);
        enrollment.setEnrollmentDate(enrollDate);
        em.persist(enrollment);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student's Insert in the course");
        alert.showAndWait();
        event.consume();
        enrollments.setAll(em.createNamedQuery("Enrollment.FindAll", Enrollment.class).getResultList());
        em.close();
        loadCourseComboBox();
        loadStudentComboBox();
    }
    @FXML
    private void showButtonHandle(ActionEvent event) throws IOException {
       EntityManager em = emf.createEntityManager();
       enrollments.setAll(em.createNamedQuery("Enrollment.FindAll", Enrollment.class).getResultList());
       em.close();
    }
    @FXML
    private void searchButtonHandle(ActionEvent event) {
        EntityManager em = emf.createEntityManager();
        try{
        String search = searchField.getText();
        LocalDate d = searchDate.getValue();
        List<Enrollment> list;
        if(d!=null&&search.isEmpty()){
            list=em.createNamedQuery("Enrollment.findByDate",Enrollment.class)
                    .setParameter("d", d)
                    .getResultList();
        }else if(search.matches("\\d+")){
            Integer id = Integer.valueOf(search);
            list = em.createNamedQuery("Enrollment.findByAnyId",Enrollment.class)
                    .setParameter("id", id)
                    .getResultList();
        }else if(!search.isBlank()){
            list = em.createNamedQuery("Enrollment.findSearch",Enrollment.class)
                .setParameter("search", "%" + search.toLowerCase() + "%")
                .getResultList();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No search in the field && ths Date is empty");
            alert.showAndWait();
            event.consume();
            return; 
        }
        enrollments.setAll(list);
        }finally{
            em.close();
        }
    }
    @FXML
    private void deleteButtonHandle(ActionEvent event) {
        //delete the row which is selected from the table
        Enrollment selected = enrollTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected enrollment");
            alert.showAndWait();
            return;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Enrollment enrollment = em.find(Enrollment.class, selected.getId());
        em.remove(enrollment);
        em.getTransaction().commit();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Enrollment's delete done");
        alert.showAndWait();
        event.consume();
        enrollments.setAll(em.createNamedQuery("Enrollment.FindAll", Enrollment.class).getResultList());
        clearForm();
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
}
