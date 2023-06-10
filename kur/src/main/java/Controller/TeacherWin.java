package Controller;
import com.example.kur.DatabaseManager;
import com.example.kur.Task;
import dao.TaskDAO;
import dao.TaskDAOImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TeacherWin implements Initializable {

    public TableView<Task> taskTable;
    public TableColumn<Task, String> taskNameColumn;
    public TableColumn<Task, String> taskCommentColumn;
    public TableColumn<Task, File> taskFileColumn;
    public TableColumn<Task, String> taskStatusColumn;
    public FileChooser fileChooser;
    public TextField taskNameField;
    public TextField taskCommentField;
    public TextField taskFileField;
    public TextField taskStatusField;
    private TaskDAO taskDAO;
    public TableColumn<Task, String> taskTextAnswerColumn;
    public TableColumn<Task, File> taskAnswerFileColumn;
    private ObservableList<Task> taskList;
    private DatabaseManager databaseManager;
    private Task task;

    public TeacherWin() {
        this.databaseManager = new DatabaseManager();
        this.taskDAO = new TaskDAOImpl();
    }

    public void handleAddTaskButtonAction(ActionEvent actionEvent) {
        String name = taskNameField.getText();
        String comment = taskCommentField.getText();
        String status = taskStatusField.getText();

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Task newTask = new Task(0, name, comment, file, status);
            newTask.setId(0);
            taskList.add(newTask);
            taskTable.setItems(taskList);

            taskNameField.clear();
            taskCommentField.clear();
            taskStatusField.clear();

            // Сохранение задачи в базе данных
            saveTaskToDatabase(newTask);
        }
    }


    private void saveTaskToDatabase(Task newTask) {
        taskDAO.saveTask(newTask);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskCommentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskTextAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("textAnswer"));
        taskAnswerFileColumn.setCellValueFactory(new PropertyValueFactory<>("answerFile"));


        taskTextAnswerColumn.setCellFactory(column -> {
            TableCell<Task, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String textAnswer, boolean empty) {
                    super.updateItem(textAnswer, empty);
                    setText(empty || textAnswer == null ? null : textAnswer);
                }
            };
            return cell;
        });

        taskAnswerFileColumn.setCellFactory(column -> {
            TableCell<Task, File> cell = new TableCell<>() {
                @Override
                protected void updateItem(File answerFile, boolean empty) {
                    super.updateItem(answerFile, empty);
                    setText(empty || answerFile == null ? null : answerFile.getName());
                }
            };
            return cell;
        });

        taskList = FXCollections.observableArrayList();
        taskTable.setItems(taskList);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

        // Загрузка задач из базы данных
        loadTasksFromDatabase();

        taskFileColumn.setCellValueFactory(new PropertyValueFactory<>("file"));

        taskFileColumn.setCellFactory(column -> {
            TableCell<Task, File> cell = new TableCell<>() {
                @Override
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);
                    setText(empty || file == null ? null : file.getName());
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    File file = cell.getItem();
                    if (file != null) {
                        openFile(file);
                        System.out.println("Открыть файл: " + file.getAbsolutePath());
                    }
                }
            });

            return cell;
        });
    }

    private void loadTasksFromDatabase() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT t.id, t.name, t.comment, t.status, t.file_path, sa.text_answer, sa.file_task AS answer_file_task " +
                             "FROM tasks t " +
                             "LEFT JOIN StudentAnswer sa ON t.student_answer_id = sa.id");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String comment = resultSet.getString("comment");
                String status = resultSet.getString("status");
                String filePath = resultSet.getString("file_path");
                String textAnswer = resultSet.getString("text_answer");
                String answerFileTask = resultSet.getString("answer_file_task");

                File file = null;
                if (filePath != null) {
                    file = new File(filePath);
                }
                File answerFile = null;
                if (answerFileTask != null) {
                    answerFile = new File(answerFileTask);
                }

                Task task = new Task(id, name, comment, file, status, textAnswer, answerFile);
                taskList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    private void openFile(File file) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
            } else if (os.contains("mac")) {
                // Mac OS
                Runtime.getRuntime().exec("open " + file.getAbsolutePath());
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                // Linux/Unix
                Runtime.getRuntime().exec("xdg-open " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void handleMarkCompleteButtonAction(ActionEvent actionEvent) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            selectedTask.setStatus("Completed");
            taskTable.refresh();

            // Обновление статуса задачи в базе данных
            updateTaskStatus(selectedTask);
        }
    }

    private void updateTaskStatus(Task task) {
        taskDAO.updateTaskStatus(task);
    }

    public void handleDeleteTask(ActionEvent actionEvent) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Task");
            confirmationDialog.setHeaderText(null);
            confirmationDialog.setContentText("Are you sure you want to delete this task?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Удаление задания из базы данных
                deleteTaskFromDatabase(selectedTask);

                taskList.remove(selectedTask);
            }
        }
    }

    private void deleteTaskFromDatabase(Task task) {
        taskDAO.deleteTask(task);
    }



    public void close() {
        databaseManager.closeConnection();
    }

    public void handleFile(ActionEvent actionEvent) {
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Выбран файл, выполните необходимые действия
            String filePath = selectedFile.getAbsolutePath();
            taskFileField.setText(filePath);
        }
    }
}