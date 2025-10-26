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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
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
    private TableView<Students> stdtable;
    @FXML
    private TableColumn<Students, String> idColumn;
    @FXML
    private TableColumn<Students, String> nameCol;
    @FXML
    private TableColumn<Students, String> majorCol;
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
    
    private final Store students = new Store("student.txt");
    private ObservableList<Students> stdData = FXCollections.observableArrayList();
    private final List<Students> allStudents= new ArrayList<>();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(stdtable.getItems().indexOf(c.getValue()) + 1)));
        idColumn.setText("#");
        nameCol.setCellValueFactory(c-> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        majorCol.setCellValueFactory(c-> new javafx.beans.property.SimpleStringProperty(c.getValue().getMajor()));
        stdtable.setItems(stdData);
        try {
            loadAllStudents();
            refresh();
        } catch (IOException ex) {
            Logger.getLogger(StudentsRegisterationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stdtable.getSelectionModel().selectedItemProperty().addListener((o, old, sel)->{
            if(sel != null){
              stdNameTextField.setText(sel.getName());
              stdMajTextField.setText(sel.getMajor());
            }
        });
        sortComboBox.setItems(FXCollections.observableArrayList("Name","Major"));
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> sortTable());
        searchField.setOnAction(e -> searchButtonhandle(null));
    }   
    private void sortTable(){
        String sortDataBy = sortComboBox.getValue();
        Comparator<Students> studentComp = switch(sortDataBy){
            case "Name" -> Comparator.comparing(Students::getName, String.CASE_INSENSITIVE_ORDER);
            case "Major"-> Comparator.comparing(Students::getMajor, String.CASE_INSENSITIVE_ORDER);
            default -> null;
    };
        if(studentComp != null){
            FXCollections.sort(stdData, studentComp);
        }
    }

    private void loadAllStudents() throws IOException{
        allStudents.clear();
        for(String line: students.readFile()){
            Students student = Students.fromLine(line);
            if(student != null){
                allStudents.add(student);
            }
        }
    }
    private void refresh() throws IOException{
        stdData.clear();
        for(String line: students.readFile()){
            Students student = Students.fromLine(line);
            if(student != null){
                stdData.add(student);
            }
        }
    }
    public void persist() throws IOException{
        List<String> lines = new ArrayList<>();
        for(Students s :stdData)
            lines.add(s.toLine());
        students.writeFile(lines);

    }
    public void clearForm(){
       stdNameTextField.clear();
       stdMajTextField.clear();
       stdtable.getSelectionModel().clearSelection();
    }
    @FXML
    private void addStudent(ActionEvent event) throws IOException {
        if(stdNameTextField.getText().isEmpty() || stdMajTextField.getText().isEmpty()){
             Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You should fill all attributes");
                alert.showAndWait();
                event.consume();
                return;
        }
        List<String> lines = students.readFile();
        boolean duplicateData = lines
                                    .stream()
                                    .map(Students::fromLine)
                                    .filter(Objects::nonNull)
                                    .anyMatch(s-> s.getName().equalsIgnoreCase(stdNameTextField.getText()) 
                                            && s.getMajor().equalsIgnoreCase(stdMajTextField.getText()));
        if(duplicateData){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("Student already exists!!");
            alert.showAndWait();
            event.consume();
            return;
        } 
        stdData.add(new Students(UUID.randomUUID().toString(), stdNameTextField.getText(), stdMajTextField.getText()));
        persist();
        loadAllStudents();
        clearForm();
        }
    
    @FXML
    private void editStudent(ActionEvent event) throws IOException {
        Students selected = stdtable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected student");
            alert.showAndWait();
            event.consume();
            return;
        }
        List<String> lines = students.readFile();
        boolean duplicateData = lines
                                    .stream()
                                    .map(Students::fromLine)
                                    .filter(Objects::nonNull)
                                    .anyMatch(s-> s.getName().equalsIgnoreCase(stdNameTextField.getText()) 
                                            && s.getMajor().equalsIgnoreCase(stdMajTextField.getText()));
        if(duplicateData){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("Student already exists!!");
            alert.showAndWait();
            event.consume();
            return;
        } 
        int indexOfId = stdData.indexOf(selected);
        Students editedStudent = new Students(selected.getId(), stdNameTextField.getText(), stdMajTextField.getText());
        stdData.set(indexOfId, editedStudent);
        persist();
        loadAllStudents() ;
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
    private void searchButtonhandle(ActionEvent event) {
        String search = searchField.getText().trim().toLowerCase();
        if(search.isEmpty()){
            stdData.setAll(allStudents);
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("please fill search box <^-^> ");
                alert.showAndWait();
                event.consume();
                return;
        }
        List<Students> stream = allStudents.stream().filter(e-> (e.getName().toLowerCase().contains(search))
                || e.getMajor().toLowerCase().contains(search))
                .collect(Collectors.toList()); 
        String sortDataBy = sortComboBox.getValue();
        if(sortDataBy!=null){
            switch(sortDataBy){
                case "Name" -> Comparator.comparing(Students::getName, String.CASE_INSENSITIVE_ORDER);
                case "Major"-> Comparator.comparing(Students::getMajor, String.CASE_INSENSITIVE_ORDER);
            }
        }
        stdData.setAll(stream);
        if(stream.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No students found in the file!!");
                alert.showAndWait();
                event.consume();
                return;
        }
    }
    @FXML
    private void showButtonHandle(ActionEvent event) throws IOException {
        if (searchField != null){ 
            searchField.clear();
        }
        loadAllStudents();
        stdData.setAll(allStudents);
    }
}