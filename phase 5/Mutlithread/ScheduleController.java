/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Mutlithread;

import AdvancedFeature.Repositories.EnrollmentRepository;
import AdvancedFeature.Repositories.StudentRepository;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ahlamabudiab
 */
public class ScheduleController implements Initializable {

    @FXML
    private Button backButton;
    @FXML
    private ProgressBar progress;
    @FXML
    private ComboBox<Students> stdComboBox;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private Button generateButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TableColumn<Row, Number> noCol;
    @FXML
    private TableColumn<Row, String> courceCol;
    @FXML
    private TableColumn<Row, Number> countCol;
    @FXML
    private TextArea outputArea;
    @FXML
    private TableView<Row> table;

    private final EnrollmentRepository enrollRepo = new EnrollmentRepository();
    private final StudentRepository stdRepo = new StudentRepository();
    private Task<List<Enrollments>> task;
    @FXML
    private Label statusLabel;
    
    /**
     * Initializes the controller class.
     */
     public static class Row {
        private final SimpleIntegerProperty index = new SimpleIntegerProperty();
        private final SimpleStringProperty course = new SimpleStringProperty();
        private final SimpleIntegerProperty count = new SimpleIntegerProperty();

        public Row(int index, String course, int count) {
            this.index.set(index);
            this.course.set(course);
            this.count.set(count);
        }
        public Number getIndex() { return index.get(); }
        public String getCourse() { return course.get(); }
        public Number getCount() { return count.get(); }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getIndex().intValue()));
        courceCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCourse()));
        countCol.setCellValueFactory(cd-> new SimpleIntegerProperty(cd.getValue().getCount().intValue()));
        List<Students> students = stdRepo.findAll();
        stdComboBox.getItems().setAll(students);
        outputArea.setText("");
        if (progress != null) {
            progress.setProgress(0);
            progress.setVisible(false);
        }
        statusLabel.setVisible(false);
        cancelButton.setDisable(true);
        outputArea.clear();
        table.getItems().clear();
        progress.setProgress(0);
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
    private void generateButtonHandle() {
        Students student = stdComboBox.getValue();
        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();
        if(student == null || from == null || to == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a student and a valid date range");
            alert.showAndWait();
            return;
        }
        if(from.isAfter(to) || to.isBefore(from) || to.isEqual(from)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error the date should be in correct range");
            alert.showAndWait();
            return; 
        }
        if(from.isAfter(LocalDate.now())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error the from date shouldn't be in the future");
            alert.showAndWait();
            return; 
        }
        outputArea.clear();
        table.getItems().clear();
        progress.setProgress(0);
        task = new Task<>(){
            @Override
            protected List<Enrollments> call() throws Exception {
                updateMessage("Loading....");
                List<Enrollments> enrollList = enrollRepo.findByStudentAndRange(student.getId(),from, to);
                int n = enrollList.size();
                updateProgress(0, Math.max(1, n));
                for(int i=0;i<n;i++){
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        throw new CancellationException();
                    }
                    Thread.sleep(200);
                    updateProgress(i + 1, Math.max(1, n));
                    updateMessage("Processed " + (i + 1) + "/" + n);
                }
                updateMessage("Done");
                return enrollList;
            }
            
        };
        bindTaskUI();
        task.setOnSucceeded(e->{
            List<Enrollments> data = task.getValue();
            if(data.isEmpty()){
                outputArea.setText("No Enrollment found 😔");
            }else{
                String text = data.stream()
                        .map(en-> en.getCourse().getId()+ " -- " + en.getCourse().getTitle()
                                + " (" + en.getCourse().getCredits() + " cr, " + en.getCourse().getDepartment() + ")")
                        .collect(Collectors.joining("\n"));
                outputArea.setText(text);
            }
            fillTableWithGroupedSummary(data);
            unBindTaskUI();
        });
        task.setOnCancelled(e -> {
            unBindTaskUI();
            statusLabel.setText("Cancelled");
        });
        task.setOnFailed(e->{
            Throwable excep = task.getException();
            unBindTaskUI();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load schedule: " + (excep == null ? "Unknown error" : excep.getMessage()));
            alert.showAndWait();
            return;
        });
        new Thread(task, "schedule-task").start();
    }

    @FXML
    private void cancelButtonHandle() {
        if(task!=null)
            task.cancel();
    }
    private void fillTableWithGroupedSummary(List<Enrollments> data) {
        if (table == null)
            return;
        table.getItems().clear();
        if ( data.isEmpty()) {
            return;
        }
        Map<String, Integer> counts = new LinkedHashMap<>();
        data.forEach(en -> {
            String key = en.getCourse().getId() + " - " + en.getCourse().getTitle();
            counts.merge(key, 1, Integer::sum);
        });
        int i = 1;
        List<Row> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            rows.add(new Row(i++, e.getKey(), e.getValue()));
        }
        table.getItems().setAll(rows);
    }
    private void bindTaskUI(){
        if (progress != null) {
            progress.setVisible(true);
            progress.progressProperty().bind(task.progressProperty());
        }
        statusLabel.setVisible(true);
        statusLabel.textProperty().bind(task.messageProperty());
        generateButton.disableProperty().bind(task.runningProperty());
        cancelButton.disableProperty().bind(task.runningProperty().not());
    }
    private void unBindTaskUI(){
        if (progress != null) {
            progress.progressProperty().unbind();
            progress.setVisible(true);
        } 
        statusLabel.textProperty().unbind();
        generateButton.disableProperty().unbind();
        cancelButton.disableProperty().unbind();
        cancelButton.setDisable(true);
    }

        }
        

