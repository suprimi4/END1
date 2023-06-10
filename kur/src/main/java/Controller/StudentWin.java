package Controller;

import com.example.kur.DatabaseManager;
import com.example.kur.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentWin implements Initializable {

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> taskNameColumn;

    @FXML
    private TableColumn<Task, String> taskCommentColumn;

    @FXML
    private TableColumn<Task, String> taskStatusColumn;

    @FXML
    private TableColumn<Task, File> taskFileColumn;

    @FXML
    private TableColumn<Task, String> taskTextAnswerColumn;

    @FXML
    private TableColumn<Task, File> taskAnswerFileColumn;

    @FXML
    private TextField textAnswerField;

    private ObservableList<Task> taskList;
    private DatabaseManager databaseManager;

    public StudentWin() {
        this.databaseManager = new DatabaseManager();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskCommentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskFileColumn.setCellValueFactory(new PropertyValueFactory<>("file"));
        taskTextAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("textAnswer"));
        taskAnswerFileColumn.setCellValueFactory(new PropertyValueFactory<>("answerFile"));

        taskList = FXCollections.observableArrayList();
        taskTable.setItems(taskList);

        // Загрузка задач из базы данных
        loadTasksFromDatabase();

        taskFileColumn.setCellFactory(column -> {
            TableCell<Task, File> cell = new TableCell<>() {
                @Override
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);
                    if (empty || file == null) {
                        setText(null);
                    } else {
                        setText(file.getName());
                    }
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

        taskAnswerFileColumn.setCellFactory(column -> {
            TableCell<Task, File> cell = new TableCell<>() {
                @Override
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);
                    if (empty || file == null) {
                        setText(null);
                    } else {
                        setText(file.getName());
                    }
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

    @FXML
    private void saveTextAnswer(ActionEvent event) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        String textAnswer = textAnswerField.getText();

        if (selectedTask != null) {
            selectedTask.setTextAnswer(textAnswer);
            // Сохранение текстового ответа в базе данных
            saveTextAnswerInDatabase(selectedTask.getId(), textAnswer);

            // Обновление таблицы
            taskTable.refresh();
        }

        // Очистка поля ввода текстового ответа
        textAnswerField.clear();
    }

    private void saveTextAnswerInDatabase(int taskId, String textAnswer) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO StudentAnswer (text_answer) VALUES (?) RETURNING id")) {

            statement.setString(1, textAnswer);
            ResultSet resultSet = statement.executeQuery();
            int studentAnswerId = 0;
            if (resultSet.next()) {
                studentAnswerId = resultSet.getInt(1);
            }

            // Закрываем resultSet перед использованием statement для обновления
            resultSet.close();

            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE tasks SET student_answer_id = ? WHERE id = ?");
            updateStatement.setInt(1, studentAnswerId);
            updateStatement.setInt(2, taskId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTextAnswer(ActionEvent event) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            selectedTask.setTextAnswer(null);
            // Удаление текстового ответа из базы данных
            deleteTextAnswerFromDatabase(selectedTask.getId());

            // Обновление таблицы
            taskTable.refresh();
        }
    }

    private void deleteTextAnswerFromDatabase(int taskId) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tasks SET student_answer_id = NULL WHERE id = ?")) {

            statement.setInt(1, taskId);
            statement.executeUpdate();
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

    public void addAnswerFile(ActionEvent actionEvent) {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            // Открытие диалогового окна выбора файла
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                // Сохранение выбранного файла в базе данных
                saveAnswerFileInDatabase(selectedTask.getId(), selectedFile);

                // Обновление объекта Task
                selectedTask.setAnswerFile(selectedFile);

                // Обновление таблицы
                taskTable.refresh();
            }
        }
    }


    private void saveAnswerFileInDatabase(int taskId, File file) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE StudentAnswer SET file_task = ? WHERE id = (SELECT student_answer_id FROM tasks WHERE id = ?)")) {

            statement.setString(1, file.getAbsolutePath());
            statement.setInt(2, taskId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                // Если нет записей, соответствующих заданному taskId, нужно выполнить вставку новой записи в таблицу StudentAnswer
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO StudentAnswer (file_task) VALUES (?)");
                insertStatement.setString(1, file.getAbsolutePath());
                insertStatement.executeUpdate();
                insertStatement.close();

                // Получить сгенерированный идентификатор новой записи
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int studentAnswerId = generatedKeys.getInt(1);

                    // Обновить student_answer_id в таблице tasks
                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE tasks SET student_answer_id = ? WHERE id = ?");
                    updateStatement.setInt(1, studentAnswerId);
                    updateStatement.setInt(2, taskId);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
