package dao;

import com.example.kur.DatabaseManager;
import com.example.kur.Task;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TaskDAOImpl implements TaskDAO {
    private DatabaseManager databaseManager;

    public TaskDAOImpl() {
        this.databaseManager = new DatabaseManager();
    }

    @Override
    public void saveTask(Task task) {
        // Реализация сохранения задачи в базе данных
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO tasks (name, comment, file_path, status) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, task.getName());
            statement.setString(2, task.getComment());
            statement.setString(3, task.getFile().getPath());
            statement.setString(4, task.getStatus());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                task.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task) {
        // Реализация обновления задачи в базе данных
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tasks SET name = ?, comment = ?, file_path = ?, status = ? WHERE id = ?")) {
            statement.setString(1, task.getName());
            statement.setString(2, task.getComment());
            statement.setString(3, task.getFile().getPath());
            statement.setString(4, task.getStatus());
            statement.setInt(5, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTask(Task task) {
        // Реализация удаления задачи из базы данных
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM tasks WHERE id = ?")) {
            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getAllTasks() {
        // Реализация получения всех задач из базы данных
        List<Task> taskList = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String comment = resultSet.getString("comment");
                String filePath = resultSet.getString("file_path");
                String status = resultSet.getString("status");

                File file = new File(filePath);
                Task task = new Task(id, name, comment, file, status);
                taskList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taskList;
    }


    @Override
    public List<Task> loadTasks() {
        return null;
    }

    @Override
    public void deleteTask(int taskId) {


    }

    @Override
    public void updateTaskStatus(Task task) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tasks SET name = ?, comment = ?, file_path = ?, status = ? WHERE id = ?")) {
            statement.setString(1, task.getName());
            statement.setString(2, task.getComment());
            statement.setString(3, task.getFile().getPath());
            statement.setString(4, task.getStatus());
            statement.setInt(5, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
