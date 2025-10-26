/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
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
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
    
    private final ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();
    private FilteredList<Enrollment> filtered;
    private SortedList<Enrollment> sorted;
     
    Connection connection;
    Statement statement;
    @FXML
    private Button deleteButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {//connection to database server
               Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection =
                        DriverManager.getConnection("jdbc:mysql://localhost:3306/SCRSproject?serverTimezone=UTC","root", "12345");
                        this.statement = this.connection.createStatement();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        //تعريف اععمدة في الجدول وربطها مع اسماء قاعدة البيانات 
        noCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getStudentName()));
        courseCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getCourseTitle()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));  
        //فلترة وفرز لعرض البيانات حسب المطلوب 
        filtered = new FilteredList<>(enrollments, x -> true);
        sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(enrollTable.comparatorProperty());
        enrollTable.setItems(sorted);
        deleteButton.disableProperty().bind(
                enrollTable.getSelectionModel().selectedItemProperty().isNull());
        try {
            loadStudentComboBox();
            loadCourseComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(EnrollmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            try { showButtonHandle(null); } catch (Exception ignored) {}
        });
    }
    private void clearForm(){
        searchField.clear();
        searchDate.setValue(null);
        dateComboBox.getSelectionModel().clearSelection();
    }//loading the student information with updates to the enrollment to match the the student id 
    private void loadStudentComboBox() throws SQLException{
        ObservableList<Students> stds = FXCollections.observableArrayList();
        String sql = "Select * FROM Students";
        ResultSet rs = this.statement.executeQuery(sql);
        while(rs.next()){
            stds.add(new Students(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("major")
            ));
        }
        stdComboBox.setItems(stds);
        stdComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Students s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getName() + " (" + s.getMajor() + ")");
            } 
        });
        stdComboBox.setButtonCell(stdComboBox.getCellFactory().call(null));
    }//load course data from the courses table
    private void loadCourseComboBox() throws SQLException{
        ObservableList<Course> courses = FXCollections.observableArrayList();
        String sql = "Select * FROM Courses";
        ResultSet rs = this.statement.executeQuery(sql);
        while(rs.next()){
            courses.add(new Course(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("credits"),
                rs.getString("department")
            ));
        }
        courseComboBox.setItems(courses);
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
   //sort the table view by date
    private void sortByDate(){
        if (!"Enrollment Date".equals(dateComboBox.getValue())) return;
        List<Enrollment> tmp = new ArrayList<>(enrollments);
        tmp.sort(Comparator.comparing(Enrollment::getEnrollmentDate,
                                  Comparator.nullsFirst(LocalDate::compareTo)));
        enrollments.setAll(tmp);
    }
    @FXML
    private void enrollButtonHandle(ActionEvent event) throws SQLException {
        //to enroll the student to the course fill the attributes and exmnie if any field is empty 
        Students studentData = stdComboBox.getValue();
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
        }//before enroll ensure that the student doesnt enroll to this course before time 
        int student_id = studentData.getId();
        int course_id = courseData.getId();
        String duplicateEnrollmentSQL = "SELECT 1 FROM Enrollments WHERE student_id = ? AND course_id = ? LIMIT 1";
        PreparedStatement chk = connection.prepareStatement(duplicateEnrollmentSQL);
        chk.setInt(1, student_id);
        chk.setInt(2, course_id);
        ResultSet rs = chk.executeQuery();
            if(rs.next()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The student already exists in this course");
                alert.showAndWait();
                event.consume();
                return;  
            }//if no duplicate enroll the student by sql insert statement,
        String sql = "INSERT INTO Enrollments (student_id, course_id, enrollment_date) "
           + "VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, student_id);
        ps.setInt(2, course_id);
        ps.setDate(3, java.sql.Date.valueOf(datePicker.getValue()));
        int rows = ps.executeUpdate();
        if (rows > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("The student enrollment in this course");
                alert.showAndWait();
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
    private void showButtonHandle(ActionEvent event) throws IOException, SQLException {
        //present the data which i need in the table view by using join between the tables 
        String sql ="SELECT e.id, e.student_id, e.course_id, e.enrollment_date, " 
                 + " s.name AS student_name, c.title AS course_title FROM Enrollments e "
                 + "JOIN Students  s ON s.id = e.student_id "+"JOIN Courses   c ON c.id = e.course_id ";
        ObservableList<Enrollment> rows = FXCollections.observableArrayList();
        try (PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
        while(rs.next()){
            rows.add(new Enrollment( 
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getString("student_name"),
                    rs.getString("course_title")
            ));
        }
        enrollments.setAll(rows);
        filtered.setPredicate(e -> true);
        clearForm();
      
       }
    }
    @FXML
    private void searchButtonHandle(ActionEvent event) {
        //search if any col. in the table matches the search field 
    String qRaw = searchField.getText();
    String q = (qRaw == null) ? "" : qRaw.trim();
    LocalDate d = searchDate.getValue();
    filtered.setPredicate(e -> {
        if (d != null && !d.equals(e.getEnrollmentDate())) {
            return false;
        }
        if (q.isEmpty()) return true;
        try {
            int asInt = Integer.parseInt(q);
            return e.getId() == asInt
                || e.getStudentId() == asInt
                || e.getCourseId() == asInt;
        } catch (NumberFormatException ignore) {
            String ql = q.toLowerCase();
            String sName  = e.getStudentName()  == null ? "" : e.getStudentName().toLowerCase();
            String cTitle = e.getCourseTitle() == null ? "" : e.getCourseTitle().toLowerCase();
            return sName.contains(ql) || cTitle.contains(ql);
        }
    });
    if (sorted.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("No matches in the table");
        alert.showAndWait();
    }
}

    @FXML
    private void deleteButtonHandle(ActionEvent event) throws SQLException {
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
        int id = selected.getId();
        int row;
        String sql = "DELETE FROM Enrollments WHERE id= "+id;
        PreparedStatement ps = connection.prepareStatement(sql);
        row = ps.executeUpdate();
        if(row>0){
            enrollments.remove(selected);
            enrollTable.getSelectionModel().clearSelection();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Enrollment's delete done");
            alert.showAndWait();
            event.consume();
            return;
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No enrollment delete");
            alert.showAndWait();
            event.consume();
            return;
        }
    }
  }
