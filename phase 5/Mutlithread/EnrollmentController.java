/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Mutlithread;
import AdvancedFeature.Repositories.CourseRepository;
import AdvancedFeature.Repositories.EnrollmentRepository;
import AdvancedFeature.Repositories.StudentRepository;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
    private ComboBox<Courses> courseComboBox;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Enrollments> enrollTable;
    @FXML
    private TableColumn<Enrollments, Integer> noCol;
    @FXML
    private TableColumn<Enrollments, String> studentCol;
    @FXML
    private TableColumn<Enrollments, String> courseCol;
    @FXML
    private TableColumn<Enrollments, LocalDate> dateCol;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> dateComboBox; 
    @FXML
    private DatePicker searchDate;
    @FXML
    private TextField searchField; 
    @FXML
    private ProgressIndicator progress;
    @FXML
    private Button addButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button GenRemButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextArea sideOutPut;
    @FXML
    private Label status;
    
    private Task<?> runTask;
    private final EnrollmentRepository enrollRepo = new EnrollmentRepository();
    private final StudentRepository stdRepo = new StudentRepository();
    private final CourseRepository courseRepo = new CourseRepository();
    private final ObservableList<Enrollments> enrollments = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getStudent().getName()));
        courseCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getCourse().getTitle()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        progress.setVisible(false);
        cancelButton.setDisable(true);
        stdComboBox.getItems().setAll(stdRepo.findAll());
        courseComboBox.getItems().setAll(courseRepo.findAll());
        enrollTable.setItems(enrollments);
        dateComboBox.setItems(FXCollections.observableArrayList("Enrollment Date"));
        dateComboBox.valueProperty().addListener((obs, oldV, newV) -> sortByDate());
    }
    private void clearForm(){
        sideOutPut.clear();
        searchField.clear();
        searchDate.setValue(null);
        dateComboBox.getSelectionModel().clearSelection();
        stdComboBox.getSelectionModel().clearSelection();
        courseComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }
    private void sortByDate(){
        if (!"Enrollment Date".equals(dateComboBox.getValue())) return;
        List<Enrollments> tmp = new ArrayList<>(enrollments);
        tmp.sort(Comparator.comparing(Enrollments::getEnrollmentDate,
                Comparator.nullsFirst(LocalDate::compareTo)));
        enrollments.setAll(tmp);
    }
    @FXML
    private void addButton(ActionEvent event) throws InterruptedException {
        Students student = stdComboBox.getValue();
        Courses course = courseComboBox.getValue();
        LocalDate date = datePicker.getValue();
        if(student == null ||course == null || date == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You should select from all !!");
            alert.showAndWait();
            event.consume();
            return;
        }
        if(date.isAfter(LocalDate.now())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error in date cannot be in the future!!");
            alert.showAndWait();
            event.consume();
            return;
        }
        if(enrollRepo.existsByStudentAndCourse(student.getId(), course.getId())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error the student "+student.getName()+" already exists in "+course.getTitle()+" course!");
            alert.showAndWait();
            return;
        }
        Long count = enrollRepo.countByCourse(course.getId());
        if(course.getCapacity()>0 && count>=course.getCapacity()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(course.getTitle()+" course is full("+count+"/"+course.getCapacity()+")");
            alert.showAndWait();
            return;
        }
        Enrollments enroll = new Enrollments(student,course,date);
        enrollRepo.save(enroll);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Student "+student.getName()+ " Insert in the "+course.getTitle()+" course");
        alert.showAndWait();
        refreshButton();
        event.consume();
        clearForm();
        return;
        }

    @FXML
    private void refreshButton() throws InterruptedException {
        Task <List<Enrollments>> task  = new Task<>() {
            @Override
            protected List<Enrollments> call() throws InterruptedException{
                updateMessage("Refreshing....");
                List<Enrollments> data = enrollRepo.findByStudentAndRange(0, LocalDate.of(1970,1,1), LocalDate.of(9999,12,31));
                int n = Math.max(1, data.size());
                for(int i=0; i<n;i++){
                    if(isCancelled()){
                        updateMessage("Cancelled");
                        return data;
                    }
                    Thread.sleep(200);
                    updateProgress(i + 1, n);
                    updateMessage("Processed " + (i + 1) + "/" + n);
                }
                updateProgress(n, n);
                updateMessage("Done");
                return data;
            }
        };
        bind(task);
        task.setOnSucceeded(e -> {
            List<Enrollments> data = task.getValue();
            enrollments.setAll(data == null? Collections.emptyList() : data);
            sortByDate();
            unbind();
        });
        task.setOnCancelled(e -> {
            unbind();
            status.setText("Cancelled");
        });
        task.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Refresh Failing..."+ task.getException().getMessage());
            alert.showAndWait();
            unbind();
            return;
        });
        new Thread(task,"enroll-refresh").start();
        clearForm();
        }
    
    private void unbind() {
        progress.progressProperty().unbind();
        progress.visibleProperty().unbind(); 
        progress.setVisible(false);
        progress.setProgress(0);
        status.textProperty().unbind();
        status.setText("");
        addButton.disableProperty().unbind();
        refreshButton.disableProperty().unbind();
        GenRemButton.disableProperty().unbind();
        cancelButton.disableProperty().unbind();
        cancelButton.setDisable(true);
        runTask = null;
    }
    private void bind(Task<?> t) {
        runTask = t;
        progress.setVisible(true);
        progress.progressProperty().bind(t.progressProperty());
        progress.visibleProperty().bind(t.runningProperty());
        status.textProperty().bind(t.messageProperty());
        addButton.disableProperty().bind(t.runningProperty());
        refreshButton.disableProperty().bind(t.runningProperty());
        GenRemButton.disableProperty().bind(t.runningProperty());
        cancelButton.disableProperty().bind(t.runningProperty().not());
}

    @FXML
    private void reminaderButton() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Generating reminders...");
                var now = LocalDate.now();
                var after = now.plusDays(2);
                var records = enrollTable.getItems();
                int num = records.size();
                updateProgress(0, Math.max(1, num));
                List<Enrollments> list = new ArrayList<>();
                for(int i= 0; i<num; i++){
                    if(isCancelled()){
                        updateMessage("Cancelled");
                        break;
                    }
                    Thread.sleep(200);
                    var record = records.get(i);
                    LocalDate addDrop = (record.getCourse() != null) ? record.getCourse().getAddDropDeadline() : null;
                    boolean in48h = addDrop != null && !addDrop.isBefore(now)  && !addDrop.isAfter(after);
                    if (in48h) {
                        list.add(record);
                    }
                   
                    updateProgress(i + 1, Math.max(1, num));
                    updateMessage("Processed " + (i + 1) + "/" + num);
                }
                    Map<Students, List<Enrollments>> remStd = list.stream()
                            .collect(Collectors.groupingBy(Enrollments::getStudent, LinkedHashMap::new, Collectors.toList()));
                    StringBuilder sb = new StringBuilder();
                    if(remStd.isEmpty()){
                        sb.append("No reminders for the selected window.");
                    }else{
                        remStd.forEach((st,stdList)->{
                            sb.append("Student: ").append(st.getName()).append("\n");
                            stdList.forEach(en -> {
                                LocalDate d = en.getCourse().getAddDropDeadline();
                                sb.append("  • ").append(en.getCourse().getTitle())
                                .append(" — Add/Drop: ").append(d).append("\n"); 
                            });
                            sb.append("\n");
                        });
                    }
                
                    updateMessage("Done");
                    updateProgress(num, Math.max(1, num));
                    return sb.toString();
            }
        };
        bind(task);
        task.setOnSucceeded(e -> {
            unbind();
            sideOutPut.setText(task.getValue()); 
        });
        task.setOnCancelled(e -> {
            unbind();
            status.setText("Cancelled");
        });
        task.setOnFailed(e->{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Fail..."+ task.getException().getMessage());
            alert.showAndWait();
            unbind();
        });
        new Thread(task,"reminders-48h adddrop").start();
        clearForm();
    }

    @FXML
    private void cancelButton(ActionEvent event) {
        if(runTask!=null)
            runTask.cancel();
        clearForm();
    }

    @FXML
    private void searchButtonHandle(ActionEvent event) {
        String search = searchField.getText();
        LocalDate d = searchDate.getValue();
        if(search == null && d ==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No search in the field && ths Date is empty");
            alert.showAndWait();
            event.consume();
            return;
        }
        List<Enrollments> list = enrollRepo.search(search, d);
        enrollments.setAll(list);
        sortByDate();
        if(list.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No records Found");
            alert.showAndWait();
            event.consume();
            return; 
        }
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
}
