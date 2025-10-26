/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package JDBC;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    
    private final ObservableList<Students> backing = FXCollections.observableArrayList();
    private FilteredList<Students> filtered;
    private SortedList<Students> sorted;
    
    Statement statement;
    Connection connection;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {//connection to the database server 
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
        }//matche the col of the table view to the col in the table of the database,
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        majorCol.setCellValueFactory(new PropertyValueFactory("major"));
        stdtable.getSelectionModel().selectedItemProperty().addListener(
                event -> showSelectedStudents());
        stdtable.getSelectionModel().selectedItemProperty().addListener((o, old, sel)->{
            if(sel != null){
                stdNameTextField.setText(sel.getName());
                stdMajTextField.setText(sel.getMajor());
            }
        });
        filtered = new FilteredList<>(backing, s -> true);
        sorted   = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(stdtable.comparatorProperty());
        stdtable.setItems(sorted);
        sortComboBox.setItems(FXCollections.observableArrayList("Name","Major"));
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((o, old, val) -> {
            Comparator<Students> comp =null;
            if ("Name".equals(val)) {
                comp = Comparator.comparing(Students::getName, String.CASE_INSENSITIVE_ORDER);
            }else if ("Major".equals(val)) {
               comp = Comparator.comparing(Students::getMajor, String.CASE_INSENSITIVE_ORDER);
            }
            if (comp != null) {
               FXCollections.sort(backing, comp); 
            }
        });
        searchField.setOnAction(e -> searchButtonhandle(null));
        deleteButton.disableProperty().bind(
                stdtable.getSelectionModel().selectedItemProperty().isNull());
        try {
            refresh();
        } catch (SQLException ex) {
            Logger.getLogger(StudentsRegisterationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refresh() throws SQLException{
        backing.clear();
        String sql = "Select * FROM Students";
        ResultSet rs = this.statement.executeQuery(sql);
        while(rs.next()){
            backing.add(new Students(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("major")
            ));
        }
    }
    private void showSelectedStudents(){
        Students std  = stdtable.getSelectionModel().getSelectedItem();
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
        //as in the course sure that the student doesnt exists in the table before time and no field is empty
        if(stdNameTextField.getText().isEmpty() || stdMajTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You should fill all attributes");
            alert.showAndWait();
            event.consume();
            return;
        }
        String name = stdNameTextField.getText();
        String major = stdMajTextField.getText();
        String checkSql = "SELECT 1 FROM Students WHERE name=? AND major=? LIMIT 1";
        try (PreparedStatement chk = connection.prepareStatement(checkSql)) {
            chk.setString(1, name);
            chk.setString(2, major);
            try (ResultSet rs = chk.executeQuery()) {
                if (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Student Already exists");
                    alert.showAndWait();
                    event.consume();
                    return;
                }
            }
            String sql = "INSERT INTO Students (name, major) VALUES (?, ?)";
            try (
                PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, major);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        refresh();
                        clearForm();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Student's Insert done");
                        alert.showAndWait();
                        event.consume();
                        return;
                    }
                }
        }
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
        String checkSql = "SELECT 1 FROM Students WHERE name=? AND major=? AND id<>? LIMIT 1";
        try (
            PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setString(1, name);
                check.setString(2, major);
                check.setInt(3, selected.getId());
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Student Already exists");
                    alert.showAndWait();
                    event.consume();
                    return;
                }
            }
            int id = selected.getId();
            String sql = "UPDATE Students SET name='" + name + "', major='" + major + "' WHERE id=" + id;
            int row = this.statement.executeUpdate(sql);
            if(row>0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Student's Update done");
                alert.showAndWait();
                event.consume();
                return;
            }
        }
        refresh();
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
                    event.consume();
                    return; 
        }
        filtered.setPredicate(st ->
        search.isEmpty() ||
        st.getName().toLowerCase().contains(search) ||
        st.getMajor().toLowerCase().contains(search) ||String.valueOf(st.getId()).contains(search));
        if(filtered.isEmpty() && !search.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No student found in the table");
            alert.showAndWait();
            event.consume();
            return;
        }
    }
    @FXML
    private void showButtonHandle(ActionEvent event) throws SQLException {
        List<Students> rows = new ArrayList<>();
        String sql = "select * from Students ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
        while(rs.next()){
            rows.add(new Students(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("major")
            ));
        }
        backing.setAll(rows);
        filtered.setPredicate(s -> true);
        refresh();
        clearForm();
    }
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
        int id  = selected.getId();
        String sql = "DELETE FROM Enrollments WHERE student_id ="+id;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
        int row;
        String sql2 = "DELETE FROM Students WHERE id = "+id;
        PreparedStatement ps2 = connection.prepareStatement(sql2);
        row = ps2.executeUpdate();
        if(row>0){
            backing.remove(selected);
            stdtable.getSelectionModel().clearSelection();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Student's delete done");
            alert.showAndWait();
            event.consume();
            return;
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No student delete");
            alert.showAndWait();
            event.consume();
            return;
        }
    }
}

        
        