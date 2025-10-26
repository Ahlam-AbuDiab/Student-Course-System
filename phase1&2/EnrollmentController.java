/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package student.course.registration.system;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.stage.Stage;

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
    private ComboBox<Students> stdComboBox;
    @FXML
    private Label courseLabel;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private Button enrollButton;

    /**
     * Initializes the controller class.
     */
    
    
    @FXML
    private Button backButton;
    @FXML
    private Button showButton;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Enrollment> enrollTable;
    @FXML
    private TableColumn<Enrollment, String> noCol;
    @FXML
    private TableColumn<Enrollment, String> studentCol;
    @FXML
    private TableColumn<Enrollment, String> courseCol;
    @FXML
    private TableColumn<Enrollment, String> dateCol;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> dateComboBox; 
    @FXML
    private DatePicker searchDate;
    @FXML
    private TextField searchField; 
     
    private Store stdStore = new Store("student.txt");
    private Store courseStore = new Store("course.txt");
    private Store enrollmentStore = new Store("enrollment.txt");
    private ObservableList<Students> students = FXCollections.observableArrayList();
    private ObservableList<Course> courses = FXCollections.observableArrayList();
    private final ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();
    private final List<Enrollment> allEnrollments = new ArrayList<>();
   
   
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        enrollTable.setItems(enrollments);
        noCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(enrollTable.getItems().indexOf(c.getValue()) + 1)));
        noCol.setText("No");
        studentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStudentName()));
        courseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCourseTitle()));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnrollmentDate()));
        try {
            loadStudents();
            loadCourses();
            loadAllEnrollments();
     
        } catch (IOException ex) {
            Logger.getLogger(EnrollmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stdComboBox.setItems(students);
        courseComboBox.setItems(courses);
        stdComboBox.setCellFactory(cb-> new ListCell<>(){
           @Override
           protected void updateItem(Students s, boolean empty) {
            super.updateItem(s, empty);
            setText(empty || s == null ? null : s.getName() + " (" + s.getMajor() + ")");
           } 
        });
        stdComboBox.setButtonCell(stdComboBox.getCellFactory().call(null));
       
        courseComboBox.setCellFactory(cb-> new ListCell<>(){
           @Override
           protected void updateItem(Course c, boolean empty) {
            super.updateItem(c, empty);
            setText(empty || c == null ? null : c.getTitle()+ " (" + c.getDepartment()+ ")-"+c.getCredits()+"cr");
         } 
        });
        courseComboBox.setButtonCell(courseComboBox.getCellFactory().call(null));
        dateComboBox.setItems(FXCollections.observableArrayList("Enrollment Date"));
        dateComboBox.valueProperty().addListener((obs, oldV, newV) -> sortByDate());
    } 
    private void loadStudents() throws IOException {
        students.clear();
        for (String line : stdStore.readFile()) {
            Students s = Students.fromLine(line);
            if (s != null){
                students.add(s);
            }
        }
    }
    private void loadCourses() throws IOException {
        courses.clear();
        Map<String, Course> unique = new LinkedHashMap<>();
        for (String line : courseStore.readFile()) {
            Course c = Course.fromLine(line);
            if (c != null){
                unique.put(c.getCourseId(), c);
            }
        }
        courses.addAll(unique.values());
    }
    private void loadAllEnrollments() throws IOException {
        allEnrollments.clear();
        enrollments.clear();
        Map<String, String> studentMap = students.stream()
                .collect(Collectors.toMap((Students s) -> s.getId(),(Students s) -> s.getName() + " (" + s.getMajor() + ")",
                (String oldV, String newV) -> oldV, LinkedHashMap::new));
       
        Map<String, String> courseMap = courses.stream()
                .collect(Collectors.toMap((Course c) -> c.getCourseId(),(Course c) -> c.getTitle() + " (" + c.getDepartment() + ")-"+c.getCredits()+"cr",
                (String oldV, String newV) -> oldV, LinkedHashMap::new));
        
        for (String line : enrollmentStore.readFile()) {
            Enrollment e = Enrollment.fromLine(line);
            if (e != null){
                e.setStudentName(studentMap.get(e.getStudentId()));
                e.setCourseTitle(courseMap.get(e.getCourceId()));
                allEnrollments.add(e);
            }
        }
        enrollments.setAll(allEnrollments);
    }
    private void sortByDate(){
        Comparator<Enrollment> byDate = Comparator.comparing(
        e -> Optional.ofNullable(parse(e.getEnrollmentDate())).orElse(LocalDate.MIN)
    );
        if("Enrollment Date".equals(dateComboBox.getValue())){
            List<Enrollment> sorted = new ArrayList<>(enrollments);
            sorted.sort(byDate);
            enrollments.setAll(sorted);
        }
    }
    private LocalDate parse(String s){
        return LocalDate.parse(s);
    }
    @FXML
    private void enrollButtonHandle(ActionEvent event) throws IOException {
        Students studentData = stdComboBox.getValue();
        Course courseData = courseComboBox.getValue();
        if(studentData == null ||courseData == null 
                || datePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should select from all !!");
                alert.showAndWait();
                event.consume();
            return;
        }
        if(datePicker.getValue().isAfter(LocalDate.now())){
             Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error in date cannot be in the future!!");
                alert.showAndWait();
                event.consume();
            return;
        }
        boolean duplicateEnrollment = enrollmentStore.readFile().stream()
                                    .map(Enrollment::fromLine)
                                    .filter(Objects::nonNull)
                                    .anyMatch(e-> e.getStudentId().equals(studentData.getId()) 
                                            && e.getCourceId().equals(courseData.getCourseId()));
        if(duplicateEnrollment){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The student already exists in this course");
                alert.showAndWait();
                event.consume();
            return;  
        }
        Enrollment enroll = new Enrollment(UUID.randomUUID().toString(), studentData.getId(), courseData.getCourseId(), datePicker.getValue().toString());
        enrollmentStore.printInFile(enroll.toLine());
        loadAllEnrollments();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("The student enrollment in this course");
                alert.showAndWait();
                return;
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
    private void showButtonHandle(ActionEvent event) throws IOException {
        loadStudents(); 
        loadCourses();
        loadAllEnrollments();
    }
    @FXML
    private void searchButtonHandle(ActionEvent event) {
        String search = searchField.getText();
        LocalDate date = searchDate.getValue();
        if (search.isBlank() && date == null){
           Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Enter Your search!!");
                alert.showAndWait();
                event.consume();
            return; 
        }
        String q =  search.trim().toLowerCase();
        List<Enrollment> result = allEnrollments.stream().filter(e-> {
            String stdName = (e.getStudentName() == null?"": e.getStudentName()).toLowerCase();
            String courseTitle = (e.getCourseTitle()==null?"":e.getCourseTitle()).toLowerCase();
            LocalDate searchDate = parse(e.getEnrollmentDate());
            boolean matchesText = q.isBlank() || stdName.contains(q) || courseTitle.contains(q);
            boolean matchesDate = (date == null) || java.util.Objects.equals(date,searchDate);
                    return matchesText && matchesDate;
        })
                .collect(Collectors.toList());
        if (result.isEmpty()) {
        enrollments.clear();
         Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No enrollments match your search.");
                alert.showAndWait();
                event.consume();
                return;
    }
        enrollments.setAll(result);
        sortByDate();
    }
}
