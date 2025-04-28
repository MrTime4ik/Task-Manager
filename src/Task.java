import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    private int id;

    private String description;
    private TaskStatus status;

    public static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private final Date createdAt;
    private Date updatedAt = new Date();

    private static final ITaskHandler handler = new TaskJsonHandler();

    public Task(String description) {

        id = handler.getNextID();
        this.description = description;
        this.status = TaskStatus.TODO;
        createdAt = new Date();
        updatedAt.setTime(System.currentTimeMillis());

        if (!description.equals("null") && !Task.handler.save(this)) {
            System.out.println("Не удалось сохранить файл");
        }
    }

    public Task(int id, String description, TaskStatus status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("id=%d; description=%s; status=%s; createdAt=%s; updatedAt=%s",
                id,
                description,
                status.getStatus(),
                formatter.format(createdAt),
                formatter.format(updatedAt)
        );
    }
}
