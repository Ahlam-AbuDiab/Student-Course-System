/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    
    private final ObservableList<Course> backing = FXCollections.observableArrayList();
    private FilteredList<Course> filtered;
    private SortedList<Course> sorted;
    Statement statement;
    Connection connection;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {//connection to database server
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection =
                    DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/SCRSproject?serverTimezone=UTC","root", "12345");
             this.statement = connection.createStatement();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Connection to DB Failed");
            alert.showAndWait();
            return;
        }
        //تعريف اععمدة في الجدول وربطها مع اسماء قاعدة البيانات 
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        titleColm.setCellValueFactory(new PropertyValueFactory("title"));
        creditsColm.setCellValueFactory(new PropertyValueFactory("credits"));
        departmentColm.setCellValueFactory(new PropertyValueFactory("department"));
        //declare the combobox of depatrment and give it the element of it.
        departmentComboBox.setItems(FXCollections.observableArrayList("CS", "SD","Computing","Counting","IT"));
        //to select row from the table
        courceTable.getSelectionModel().selectedItemProperty().addListener(
                event -> showSelectedCourses());
        //فلترة وفرز لعرض البيانات حسب المطلوب 
        filtered = new FilteredList<>(backing, s -> true);
        sorted   = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(courceTable.comparatorProperty());
        courceTable.setItems(sorted);
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
        if (courseComp != null) FXCollections.sort(backing, courseComp);
        });
        //for searching and delete the row which is selected
        searchField.setOnAction(e -> searchButtonHandle(null));
        deleteButton.disableProperty().bind(
                courceTable.getSelectionModel().selectedItemProperty().isNull());
        try {
            refresh();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //to make refresh for the table view
    private void refresh() throws SQLException{
        backing.clear();
        String sql = "Select * FROM Courses ";
        ResultSet rs = this.statement.executeQuery(sql);
        while(rs.next()){
            backing.add(new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("credits"),
                    rs.getString("department")
            ));
        }
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
        //to exmine if any field required to add course is empty 
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
        //to make sure that the credits is apositive number
        if(!creditsTextField.getText().matches("\\d+")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The credits can't be negative or non-numeric values ");
                alert.showAndWait();
                event.consume();
                return;
        }
        //to exmine if the course is exists in  the table before time to prevent the duplicate
        String title = titleTextField.getText();
        String creditsStr = creditsTextField.getText();
        int credits = Integer.parseInt(creditsTextField.getText());
        String department = departmentComboBox.getValue();
        String checkSql = "SELECT 1 FROM `SCRSproject`.`Courses` WHERE title = ? AND department = ? LIMIT 1";
        try (PreparedStatement chk = this.connection.prepareStatement(checkSql)) {
            chk.setString(1, title);
            chk.setString(2, department);
            try (ResultSet rs = chk.executeQuery()) {
                if(rs.next()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText(" The Course Already exists");
                    alert.showAndWait();
                    event.consume();
                    return;
                }
            }
            //لو ما كلن الكورس مسجل في الجدول من قبل سيتم اضافته على الجدول وادخال بياناته
        String sql = "INSERT INTO Courses ( title, credits,department) "
           + "VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, title);
        ps.setInt(2, credits);
        ps.setString(3, department);
        int rows = ps.executeUpdate();
        //هيفحص اذا كان عدد السجلات التي تاثرت بجملة الاستعلام اكثرمن صفر يعني الاستعلام نحج وتمت اضافة الكورس بنجاح
        if(rows>0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Course's Insert done");
                    alert.showAndWait();
                    event.consume();
                    return;
            }
        }
    }
        //after add course refresh the table and the fields
        refresh();
        clearForm();
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
        int id = selected.getId();
        String checkSql = "SELECT 1 FROM `SCRSproject`.`Courses` WHERE title = ? AND department = ?  AND id <> ? LIMIT 1";
        try (PreparedStatement chk = this.connection.prepareStatement(checkSql)) {
            chk.setString(1, title);
            chk.setString(2, department);
            chk.setInt(3, id);
            try (ResultSet rs = chk.executeQuery()) {
                if(rs.next()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Course Already exists");
                    alert.showAndWait();
                    event.consume();
                    return;
                }
            }
        }
        String sql = "UPDATE `SCRSproject`.`Courses` SET title='" + title.replace("'", "''") + "', credits=" + credits + ", " +                    // رقم بدون أقواس مفردة
                "department='" + department.replace("'", "''") +"' WHERE id=" + id;        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int row = ps.executeUpdate(sql);
            if(row>0){
                refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Course's Update done");
                    alert.showAndWait();
                    event.consume();
                    return;
            }
        }
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
        //in search exmine if any col. in the table is matched the search and show it
        String search = searchField.getText();
        filtered.setPredicate(course ->
        search.isEmpty() ||
        course.getTitle().toLowerCase().contains(search) ||
        String.valueOf(course.getCredits()).contains(search)||
        course.getDepartment().toLowerCase().contains(search));
        if(filtered.isEmpty() && !search.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("No course found in the table");
                    alert.showAndWait();
                    event.consume();
                    return;
        }
    }
    @FXML
    private void showButtonHanld(ActionEvent event)throws SQLException {
        //to present all records from the database in the table view 
        List<Course> rows = new ArrayList<>();
        String sql = "SELECT * FROM `Courses`";
        try(PreparedStatement ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
        while(rs.next()){
            rows.add(new Course(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("credits"),
                rs.getString("department")
            ));
        }
            backing.setAll(rows);
            filtered.setPredicate(s -> true);
        }
        refresh();
        clearForm();
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
        //at first delete all records which related to this course from enroolments table
        int id  = selected.getId();
        String sql = "DELETE FROM Enrollments WHERE course_id ="+id;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
        int row;
        //then delete the course from the course table
        String sql2 = "DELETE FROM Courses WHERE id = "+id;
        PreparedStatement ps2 = connection.prepareStatement(sql2);
        row = ps2.executeUpdate();
        if(row>0){
            backing.remove(selected);
            courceTable.getSelectionModel().clearSelection();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Courses's delete done");
            alert.showAndWait();
            event.consume();
            return;
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No course delete");
            alert.showAndWait();
            event.consume();
            return;
        }
    }
    }