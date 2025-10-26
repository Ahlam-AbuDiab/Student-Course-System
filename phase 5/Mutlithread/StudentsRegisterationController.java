/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Mutlithread;
import AdvancedFeature.Repositories.StudentRepository;
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
    private TableColumn<Students, Integer> idColumn;
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
    @FXML
    private Button deleteButton;
    
    private final ObservableList<Students> students = FXCollections.observableArrayList();
    private final StudentRepository stdRepo = new StudentRepository();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
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
                Comparator<Students> comp =null;
                if ("Name".equals(val)) {
                    comp = Comparator.comparing(Students::getName, String.CASE_INSENSITIVE_ORDER);
                }else if ("Major".equals(val)) {
                    comp = Comparator.comparing(Students::getMajor, String.CASE_INSENSITIVE_ORDER);
                    
                }
                FXCollections.sort(students, comp);
            });
            searchField.setOnAction(this::searchButtonhandle);
            searchButton.setOnAction(this::searchButtonhandle);
            deleteButton.disableProperty().bind(
                    stdtable.getSelectionModel().selectedItemProperty().isNull());
            showButtonHandle(null);
        } catch (SQLException ex) {
            Logger.getLogger(StudentsRegisterationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void showSelectedStudents(){
        Students std  = stdtable.getSelectionModel().getSelectedItem();
        if(std != null){
            stdNameTextField.setText(std.getName());
            stdMajTextField.setText(std.getMajor());
        }
    }
    public void refresh(List<Students> list){
        students.setAll(list);
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
        if(stdRepo.dupStd(name, major)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(name+" Already exists in the system");
            alert.showAndWait();
            event.consume();
            return;
        }
        Students student = new Students();
        student.setName(name);
        student.setMajor(major);
        stdRepo.save(student);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(name+" Insert done in the system");
        alert.showAndWait();
        event.consume();
        clearForm();
        refresh(stdRepo.findAll());
        }
    
    @FXML
    private void editStudent(ActionEvent event) throws IOException, SQLException {
        Students selected = stdtable.getSelectionModel().getSelectedItem();
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
        if(stdRepo.dupStd(name, major)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(name +" in "+major+" Already exists in the system");
            alert.showAndWait();
            event.consume();
            return;
        }
        selected.setName(name);
        selected.setMajor(major);
        stdRepo.save(selected);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student's Update done");
        alert.showAndWait();
        event.consume();
        clearForm();
        refresh(stdRepo.findAll());
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
        refresh(stdRepo.search(search));
    }
    @FXML
    private void showButtonHandle(ActionEvent event) throws SQLException {
        students.setAll(stdRepo.findAll());
        clearForm();
    }

    @FXML
    private void deleteButtonHandle(ActionEvent event) throws SQLException {
        //delete the student which selected from the table view and delete all records from enrollments table which related to this student
        Students selected = stdtable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error!!");
            alert.setHeaderText(null);
            alert.setContentText("No selected student");
            alert.showAndWait();
            return;
        }
        stdRepo.delete(selected.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(selected.getName()+"("+selected.getMajor()+")"+" deleted from the system");
        alert.showAndWait();
        refresh(stdRepo.findAll());
        clearForm();
    }
}

        
        