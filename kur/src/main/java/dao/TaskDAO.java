package dao;


import com.example.kur.Task;

import java.util.List;

public interface TaskDAO {
    void saveTask(Task task);
    void updateTask(Task task);
    void deleteTask(Task task);
    List<Task> getAllTasks();

    List<Task> loadTasks();

    void deleteTask(int taskId);

    void updateTaskStatus(Task task);
}
