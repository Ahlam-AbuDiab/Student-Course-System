/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package student.course.registration.system;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private TableColumn<Course, String> idColumn;
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
    
    private final Store courses = new Store("course.txt");
  
    private final ObservableList<Course> courseData = FXCollections.observableArrayList();
    
    private final List<Course> allCourses = new ArrayList<>();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        courceTable.setItems(courseData);
        idColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(courceTable.getItems().indexOf(c.getValue()) + 1)));
        idColumn.setText("#");
        titleColm.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        creditsColm.setCellValueFactory(c -> new ReadOnlyObjectWrapper(c.getValue().getCredits()));
        departmentColm.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDepartment()));
        courceTable.setItems(courseData);
        try{
            loadAllCourses();
            refresh();
        }catch(IOException e){
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, e);
        }
        
       departmentComboBox.setItems(FXCollections.observableArrayList("CS", "SD","Computing","Counting","IT"));
       courceTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->{
           if(sel != null){
               titleTextField.setText(sel.getTitle());
               creditsTextField.setText(String.valueOf(sel.getCredits()));
               departmentComboBox.getSelectionModel().select(sel.getDepartment());
           }
       });
       sortComboBox.setItems(FXCollections.observableArrayList("Title","Credits","Department"));
       sortComboBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> sortTable());
       searchField.setOnAction(e -> searchButtonHandle(null));
       
    }   
    private void sortTable(){
        String sortDataBy = sortComboBox.getValue();
        Comparator<Course> courseComp = switch(sortDataBy){
            case "Title" -> Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "Credits"-> Comparator.comparingInt(Course::getCredits);
            case "Department"-> Comparator.comparing(Course::getDepartment, String.CASE_INSENSITIVE_ORDER);
            default -> null;
    };
        if(courseComp != null){
            FXCollections.sort(courseData, courseComp);
        }
    }
    private void loadAllCourses() throws IOException{
        allCourses.clear();
        for(String line: courses.readFile()){
            Course course = Course.fromLine(line);
            if(course != null){
                allCourses.add(course);
            }
        }
    }
    private void refresh() throws IOException{
      courseData.clear();
      for(String line: courses.readFile()){
          Course course = Course.fromLine(line);
          if(course != null){
             courseData.add(course);
          }
      }
    }
    private void persist() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Course c : courseData) 
            lines.add(c.toLine());
            courses.writeFile(lines); 
    }
    private void clearForm() {
       titleTextField.clear();
       creditsTextField.clear();
       departmentComboBox.getSelectionModel().clearSelection();
       courceTable.getSelectionModel().clearSelection();
    } 

    @FXML
    private void addCourseHandle(ActionEvent event) throws IOException {
        if(titleTextField.getText().isEmpty() || creditsTextField.getText().isEmpty() 
                || departmentComboBox.getValue() == null){
              Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
                return;
        }
        if(!creditsTextField.getText().matches("\\d+")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The credits can't be negative or non-numeric values ");
                alert.showAndWait();
                event.consume();
                return;
        }
        Course selected = courceTable.getSelectionModel().getSelectedItem();

        boolean duplicateCourse = courseData
                .stream()
                .anyMatch(c-> c!=selected && c.getTitle().equalsIgnoreCase(titleTextField.getText()) 
                        && c.getDepartment().equalsIgnoreCase(departmentComboBox.getValue()));
        if(duplicateCourse){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The course already exists!!");
                alert.showAndWait();
                event.consume();
                return;
        }
        
        courseData.add(new Course(UUID.randomUUID().toString(), titleTextField.getText(), Integer.parseInt(creditsTextField.getText()), departmentComboBox.getValue()));
        courceTable.setItems(courseData);
        persist();
        loadAllCourses();
        refresh();
        clearForm();
       
    }

    @FXML
    private void editCourseHandle(ActionEvent event) throws IOException {
       Course selected = courceTable.getSelectionModel().getSelectedItem();
       if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should select course to edit");
                alert.showAndWait();
                event.consume();
                return;
       }
        if(titleTextField.getText().isEmpty() || creditsTextField.getText().isEmpty()){
              Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
                return;
        }
        boolean duplicateCourse = courseData
                .stream()
                .anyMatch(c-> c!= selected &&c.getTitle().equalsIgnoreCase(titleTextField.getText()) 
                        && c.getDepartment().equalsIgnoreCase(creditsTextField.getText()));
        if(duplicateCourse){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The course already exists!!");
                alert.showAndWait();
                event.consume();
                return;
        }
        if(!creditsTextField.getText().matches("\\d+")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The credits can't be negative or non-numeric values ");
                alert.showAndWait();
                event.consume();
                return;
        }
        int indexOfId = courseData.indexOf(selected);
        Course editedCourse = new Course(selected.getCourseId(), titleTextField.getText(), Integer.parseInt(creditsTextField.getText()), departmentComboBox.getValue());
        courseData.set(indexOfId, editedCourse);
        persist();
        loadAllCourses();
        refresh();
        clearForm();
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
        String search = searchField.getText().trim().toLowerCase();
        if(search.isEmpty()){
            courseData.setAll(allCourses);
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("please fill search box <^-^> ");
                alert.showAndWait();
                event.consume();
                return;
        }
        List<Course> stream = allCourses.stream().filter(e-> (e.getTitle().toLowerCase().contains(search))
                    ||(String.valueOf(e.getCredits()).contains(search)) || (e.getDepartment().toLowerCase().contains(search)))
                    .collect(Collectors.toList()); 
        String sortDataBy = sortComboBox.getValue();
        if(sortDataBy!=null){
            switch(sortDataBy){
                case "Title" ->stream.sort(Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER));
                case "Credits" ->stream.sort(Comparator.comparing(Course::getCredits));
                case "Department" ->stream.sort(Comparator.comparing(Course::getDepartment, String.CASE_INSENSITIVE_ORDER));
            }
        }
        courseData.setAll(stream);
        if(stream.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No courses found in the file!!");
                alert.showAndWait();
                event.consume();
                return;
        }
    }
    @FXML
    private void showButtonHanld(ActionEvent event)throws IOException {
        if (searchField != null){ 
            searchField.clear();
        }
        loadAllCourses();
        courseData.setAll(allCourses);
    }
    }