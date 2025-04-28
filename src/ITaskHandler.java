import java.util.ArrayList;

public interface ITaskHandler {

    boolean save(Task task);
    Task load(int id);
    boolean update(int id, String description);
    boolean delete(int id);
    boolean mark(int id, TaskStatus status);
    ArrayList<Task> loadAllTasks();
    ArrayList<Task> loadTasksByStatus(TaskStatus status);

    int getNextID();
}